package org.voegtle.privatetrainer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.window.layout.DisplayFeature
import androidx.window.layout.FoldingFeature
import kotlinx.coroutines.launch
import org.voegtle.privatetrainer.business.PrivateTrainerState
import org.voegtle.privatetrainer.ui.EmptyComingSoon
import org.voegtle.privatetrainer.ui.navigation.*
import org.voegtle.privatetrainer.ui.utils.*

@Composable
fun PrivateTrainerApp (
    windowSize: WindowSizeClass,
    displayFeatures: List<DisplayFeature>,
    privateTrainerState: PrivateTrainerState,
    closeDetailScreen: () -> Unit = {},
    navigateToDetail: (Long, PrivateContentType) -> Unit = { _, _ -> }

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
        displayFeatures = displayFeatures,
        navigationContentPosition = navigationContentPosition,
        privateTrainerState = privateTrainerState,
        closeDetailScreen = closeDetailScreen,
        navigateToDetail = navigateToDetail
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PrivateTrainerNavigationWrapper(
    navigationType: PrivateNavigationType,
    contentType: PrivateContentType,
    displayFeatures: List<DisplayFeature>,
    navigationContentPosition: PrivateNavigationContentPosition,
    privateTrainerState: PrivateTrainerState,
    closeDetailScreen: () -> Unit,
    navigateToDetail: (Long, PrivateContentType) -> Unit
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
                contentType = contentType,
                displayFeatures = displayFeatures,
                navigationContentPosition = navigationContentPosition,
                privateTrainerState = privateTrainerState,
                navController = navController,
                selectedDestination = selectedDestination,
                navigateToTopLevelDestination = navigationActions::navigateTo,
                closeDetailScreen = closeDetailScreen,
                navigateToDetail = navigateToDetail
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
                contentType = contentType,
                displayFeatures = displayFeatures,
                navigationContentPosition = navigationContentPosition,
                privateTrainerState = privateTrainerState,
                navController = navController,
                selectedDestination = selectedDestination,
                navigateToTopLevelDestination = navigationActions::navigateTo,
                closeDetailScreen = closeDetailScreen,
                navigateToDetail = navigateToDetail
            ) {
                scope.launch {
                    drawerState.open()
                }
            }
        }
    }
}

@Composable
fun PrivateTrainerAppContent(
    modifier: Modifier = Modifier,
    navigationType: PrivateNavigationType,
    contentType: PrivateContentType,
    displayFeatures: List<DisplayFeature>,
    navigationContentPosition: PrivateNavigationContentPosition,
    privateTrainerState: PrivateTrainerState,
    navController: NavHostController,
    selectedDestination: String,
    navigateToTopLevelDestination: (PrivateTopLevelDestination) -> Unit,
    closeDetailScreen: () -> Unit,
    navigateToDetail: (Long, PrivateContentType) -> Unit,
    onDrawerClicked: () -> Unit = {}
) {
    Row(modifier = modifier.fillMaxSize()) {
//        AnimatedVisibility(visible = navigationType == PrivateNavigationType.NAVIGATION_RAIL) {
//            ReplyNavigationRail(
//                selectedDestination = selectedDestination,
//                navigationContentPosition = navigationContentPosition,
//                navigateToTopLevelDestination = navigateToTopLevelDestination,
//                onDrawerClicked = onDrawerClicked,
//            )
//        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.inverseOnSurface)
        ) {
            PrivateTrainerNavHost(
                navController = navController,
                contentType = contentType,
                displayFeatures = displayFeatures,
                privateTrainerState = privateTrainerState,
                navigationType = navigationType,
                closeDetailScreen = closeDetailScreen,
                navigateToDetail = navigateToDetail,
                modifier = Modifier.weight(1f),
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
    contentType: PrivateContentType,
    displayFeatures: List<DisplayFeature>,
    privateTrainerState: PrivateTrainerState,
    navigationType: PrivateNavigationType,
    closeDetailScreen: () -> Unit,
    navigateToDetail: (Long, PrivateContentType) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = PrivateRoute.START,
    ) {
        composable(PrivateRoute.START) {
            EmptyComingSoon()
        }
        composable(PrivateRoute.SETTINGS) {
            EmptyComingSoon()
        }
        composable(PrivateRoute.SAVED_SETTINGS) {
            EmptyComingSoon()
        }
        composable(PrivateRoute.ABOUT) {
            EmptyComingSoon()
        }
    }
}
