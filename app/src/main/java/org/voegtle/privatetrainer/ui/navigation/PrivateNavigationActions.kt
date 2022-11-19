package org.voegtle.privatetrainer.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Save
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import org.voegtle.privatetrainer.R

object PrivateRoute {
    const val START = "Start"
    const val SETTINGS = "Settings"
    const val SAVED_SETTINGS = "SavedSettings"
    const val ABOUT = "About"
}

data class PrivateTopLevelDestination(
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val iconTextId: Int
)

class PrivateNavigationActions(private val navController: NavHostController) {

    fun navigateTo(destination: PrivateTopLevelDestination) {
        navController.navigate(destination.route) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }
    }
}

val TOP_LEVEL_DESTINATIONS = listOf(
    PrivateTopLevelDestination(
        route = PrivateRoute.START,
        selectedIcon = Icons.Default.Start,
        unselectedIcon = Icons.Default.Start,
        iconTextId = R.string.tab_start
    ),
    PrivateTopLevelDestination(
        route = PrivateRoute.SETTINGS,
        selectedIcon = Icons.Default.Settings,
        unselectedIcon = Icons.Default.Settings,
        iconTextId = R.string.tab_settings
    ),
    PrivateTopLevelDestination(
        route = PrivateRoute.SAVED_SETTINGS,
        selectedIcon = Icons.Default.Save,
        unselectedIcon = Icons.Outlined.Save,
        iconTextId = R.string.tab_saved_settings
    ),
    PrivateTopLevelDestination(
        route = PrivateRoute.ABOUT,
        selectedIcon = Icons.Default.Info,
        unselectedIcon = Icons.Default.Info,
        iconTextId = R.string.tab_about
    )

)

