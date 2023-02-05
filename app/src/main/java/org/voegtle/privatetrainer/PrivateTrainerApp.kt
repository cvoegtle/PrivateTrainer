package org.voegtle.privatetrainer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.window.layout.DisplayFeature
import androidx.window.layout.FoldingFeature
import kotlinx.coroutines.launch
import org.voegtle.privatetrainer.business.BluetoothState
import org.voegtle.privatetrainer.business.DeviceSettings
import org.voegtle.privatetrainer.ui.EmptyComingSoon
import org.voegtle.privatetrainer.ui.OverviewScreen
import org.voegtle.privatetrainer.ui.SettingsManagementScreen
import org.voegtle.privatetrainer.ui.navigation.*
import org.voegtle.privatetrainer.ui.utils.*

@Composable
fun PrivateTrainerApp (
    windowSize: WindowSizeClass,
    displayFeatures: List<DisplayFeature>,
    savedDeviceSettings: List<DeviceSettings>,
    onSearchDeviceClicked: (MutableState<BluetoothState>) -> Unit
) {
    /**
     * This will help us select type of navigation and content type depending on window size and
     * fold state of the device.
     */
    val navigationType: PrivateNavigationType
    val contentType: PrivateContentType

    /**
     * We are using display's folding features to map the device postures a fold is in.
     * In the state of folding device If it's half fold in BookPosture we want to avoid content
     * at the crease/hinge
     */
    val foldingFeature = displayFeatures.filterIsInstance<FoldingFeature>().firstOrNull()

    val foldingDevicePosture = when {
        isBookPosture(foldingFeature) ->
            DevicePosture.BookPosture(foldingFeature.bounds)

        isSeparating(foldingFeature) ->
            DevicePosture.Separating(foldingFeature.bounds, foldingFeature.orientation)

        else -> DevicePosture.NormalPosture
    }

    when (windowSize.widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            navigationType = PrivateNavigationType.BOTTOM_NAVIGATION
            contentType = PrivateContentType.SINGLE_PANE
        }
        WindowWidthSizeClass.Medium -> {
            navigationType = PrivateNavigationType.NAVIGATION_RAIL
            contentType = if (foldingDevicePosture != DevicePosture.NormalPosture) {
                PrivateContentType.DUAL_PANE
            } else {
                PrivateContentType.SINGLE_PANE
            }
        }
        WindowWidthSizeClass.Expanded -> {
            navigationType = if (foldingDevicePosture is DevicePosture.BookPosture) {
                PrivateNavigationType.NAVIGATION_RAIL
            } else {
                PrivateNavigationType.PERMANENT_NAVIGATION_DRAWER
            }
            contentType = PrivateContentType.DUAL_PANE
        }
        else -> {
            navigationType = PrivateNavigationType.BOTTOM_NAVIGATION
            contentType = PrivateContentType.SINGLE_PANE
        }
    }

    /**
     * Content inside Navigation Rail/Drawer can also be positioned at top, bottom or center for
     * ergonomics and reachability depending upon the height of the device.
     */
    val navigationContentPosition = when (windowSize.heightSizeClass) {
        WindowHeightSizeClass.Compact -> {
            PrivateNavigationContentPosition.TOP
        }
        WindowHeightSizeClass.Medium,
        WindowHeightSizeClass.Expanded -> {
            PrivateNavigationContentPosition.CENTER
        }
        else -> {
            PrivateNavigationContentPosition.TOP
        }
    }

    PrivateTrainerNavigationWrapper(
        navigationType = navigationType,
        contentType = contentType,
        savedDeviceSettings = savedDeviceSettings,
        displayFeatures = displayFeatures,
        navigationContentPosition = navigationContentPosition,
        onSearchDeviceClicked = onSearchDeviceClicked
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PrivateTrainerNavigationWrapper(
    navigationType: PrivateNavigationType,
    contentType: PrivateContentType,
    savedDeviceSettings: List<DeviceSettings>,
    displayFeatures: List<DisplayFeature>,
    navigationContentPosition: PrivateNavigationContentPosition,
    onSearchDeviceClicked: (state: MutableState<BluetoothState>) -> Unit,
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navController = rememberNavController()
    val navigationActions = remember(navController) {
        PrivateNavigationActions(navController)
    }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val selectedDestination =
        navBackStackEntry?.destination?.route ?: PrivateRoute.START

    if (navigationType == PrivateNavigationType.PERMANENT_NAVIGATION_DRAWER) {
        // TODO check on custom width of PermanentNavigationDrawer: b/232495216
        PermanentNavigationDrawer(drawerContent = {
            PermanentNavigationDrawerContent(
                selectedDestination = selectedDestination,
                navigationContentPosition = navigationContentPosition,
                navigateToTopLevelDestination = navigationActions::navigateTo,
            )
        }) {
            PrivateTrainerAppContent(
                navigationType = navigationType,
                savedDeviceSettings = savedDeviceSettings,
                contentType = contentType,
                displayFeatures = displayFeatures,
                navigationContentPosition = navigationContentPosition,
                navController = navController,
                selectedDestination = selectedDestination,
                navigateToTopLevelDestination = navigationActions::navigateTo,
                onSearchDeviceClicked = onSearchDeviceClicked
            )
        }
    } else {
        ModalNavigationDrawer(
            drawerContent = {
                ModalNavigationDrawerContent(
                    selectedDestination = selectedDestination,
                    navigationContentPosition = navigationContentPosition,
                    navigateToTopLevelDestination = navigationActions::navigateTo,
                    onDrawerClicked = {
                        scope.launch {
                            drawerState.close()
                        }
                    }
                )
            },
            drawerState = drawerState
        ) {
            PrivateTrainerAppContent(
                navigationType = navigationType,
                savedDeviceSettings = savedDeviceSettings,
                contentType = contentType,
                displayFeatures = displayFeatures,
                navigationContentPosition = navigationContentPosition,
                navController = navController,
                selectedDestination = selectedDestination,
                navigateToTopLevelDestination = navigationActions::navigateTo,
                onDrawerClicked = {
                    scope.launch {
                        drawerState.open()
                    }
                },
                onSearchDeviceClicked = onSearchDeviceClicked,
            )
        }
    }
}

@Composable
fun PrivateTrainerAppContent(
    modifier: Modifier = Modifier,
    savedDeviceSettings: List<DeviceSettings>,
    navigationType: PrivateNavigationType,
    contentType: PrivateContentType,
    displayFeatures: List<DisplayFeature>,
    navigationContentPosition: PrivateNavigationContentPosition,
    navController: NavHostController,
    selectedDestination: String,
    navigateToTopLevelDestination: (PrivateTopLevelDestination) -> Unit,
    onDrawerClicked: () -> Unit = {},
    onSearchDeviceClicked: (state: MutableState<BluetoothState>) -> Unit
) {
    Row(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.inverseOnSurface)
        ) {
            PrivateTrainerNavHost(
                navController = navController,
                savedDeviceSettings = savedDeviceSettings,
                contentType = contentType,
                displayFeatures = displayFeatures,
                navigationType = navigationType,
                modifier = Modifier.weight(1f),
                onSearchDeviceClicked = onSearchDeviceClicked
            )
            AnimatedVisibility(visible = navigationType == PrivateNavigationType.BOTTOM_NAVIGATION) {
                PrivateBottomNavigationBar(
                    selectedDestination = selectedDestination,
                    navigateToTopLevelDestination = navigateToTopLevelDestination
                )
            }
        }
    }
}


@Composable
private fun PrivateTrainerNavHost(
    navController: NavHostController,
    savedDeviceSettings: List<DeviceSettings>,
    contentType: PrivateContentType,
    displayFeatures: List<DisplayFeature>,
    navigationType: PrivateNavigationType,
    modifier: Modifier = Modifier,
    onSearchDeviceClicked: (state: MutableState<BluetoothState>) -> Unit,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = PrivateRoute.START,
    ) {
        composable(PrivateRoute.START) {
            OverviewScreen(onSearchDeviceClicked)
        }
        composable(PrivateRoute.SAVED_SETTINGS) {
            SettingsManagementScreen(savedDeviceSettings)
        }
        composable(PrivateRoute.SETTINGS) {
            EmptyComingSoon()
        }
        composable(PrivateRoute.ABOUT) {
            EmptyComingSoon()
        }
    }
}
