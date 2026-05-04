package com.temperedsteel.tracker.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.temperedsteel.tracker.AppViewModelFactory
import com.temperedsteel.tracker.ui.exercises.ExerciseScreen
import com.temperedsteel.tracker.ui.home.HomeScreen
import com.temperedsteel.tracker.ui.log.LogScreen
import com.temperedsteel.tracker.ui.progress.ProgressScreen
import com.temperedsteel.tracker.ui.theme.*

sealed class Screen(val route: String) {
    object Home      : Screen("home")
    object Log       : Screen("log/{exerciseId}") { fun go(id: Long) = "log/$id" }
    object Progress  : Screen("progress/{exerciseId}") { fun go(id: Long) = "progress/$id" }
    object Exercises : Screen("exercises")
}

data class NavItem(val screen: Screen, val label: String, val icon: ImageVector)

private val navItems = listOf(
    NavItem(Screen.Home,      "Home",      Icons.Default.Home),
    NavItem(Screen.Exercises, "Exercises", Icons.Default.FitnessCenter)
)

@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val nav     = rememberNavController()
    val back    by nav.currentBackStackEntryAsState()
    val route   = back?.destination?.route
    val showBar = route in listOf(Screen.Home.route, Screen.Exercises.route)

    Scaffold(
        containerColor = Background,
        bottomBar = {
            if (showBar) {
                NavigationBar(containerColor = Surface) {
                    navItems.forEach { item ->
                        NavigationBarItem(
                            selected = route == item.screen.route,
                            onClick  = {
                                nav.navigate(item.screen.route) {
                                    popUpTo(Screen.Home.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState    = true
                                }
                            },
                            icon  = { Icon(item.icon, item.label) },
                            label = { Text(item.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor   = Accent,
                                selectedTextColor   = Accent,
                                unselectedIconColor = OnSurfaceDim,
                                unselectedTextColor = OnSurfaceDim,
                                indicatorColor      = AccentDim
                            )
                        )
                    }
                }
            }
        }
    ) { _ ->
        NavHost(nav, startDestination = Screen.Home.route) {

            composable(Screen.Home.route) {
                HomeScreen(
                    onExerciseTap = { nav.navigate(Screen.Log.go(it)) },
                    viewModel     = viewModel(factory = AppViewModelFactory.home(context))
                )
            }

            composable(Screen.Log.route, arguments = listOf(navArgument("exerciseId") { type = NavType.LongType })) { bs ->
                val id = bs.arguments?.getLong("exerciseId") ?: return@composable
                LogScreen(
                    onBack    = { nav.popBackStack() },
                    viewModel = viewModel(key = "log_$id", factory = AppViewModelFactory.log(context, id))
                )
            }

            composable(Screen.Progress.route, arguments = listOf(navArgument("exerciseId") { type = NavType.LongType })) { bs ->
                val id = bs.arguments?.getLong("exerciseId") ?: return@composable
                ProgressScreen(
                    onBack    = { nav.popBackStack() },
                    viewModel = viewModel(key = "prog_$id", factory = AppViewModelFactory.progress(context, id))
                )
            }

            composable(Screen.Exercises.route) {
                ExerciseScreen(
                    viewModel     = viewModel(factory = AppViewModelFactory.exercise(context)),
                    onExerciseTap = { nav.navigate(Screen.Log.go(it)) }
                )
            }
        }
    }
}
