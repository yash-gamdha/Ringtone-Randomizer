package com.app.ringtonerandomizer.navigation.nav_graph

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.app.ringtonerandomizer.navigation.Screen
import com.app.ringtonerandomizer.navigation.Tab
import com.app.ringtonerandomizer.presentation.about_screen.AboutScreen

fun NavGraphBuilder.aboutScreen(
    navController: NavController
) {
    navigation(
        startDestination = Screen.AboutScreen.route,
        route = Tab.About.route
    ) {
        composable(
            route = Screen.AboutScreen.route
        ) {
            AboutScreen(
                navController = navController
            )
        }
    }
}