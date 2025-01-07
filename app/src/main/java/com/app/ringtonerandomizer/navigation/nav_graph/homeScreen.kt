package com.app.ringtonerandomizer.navigation.nav_graph

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.app.ringtonerandomizer.navigation.Screen
import com.app.ringtonerandomizer.navigation.Tab
import com.app.ringtonerandomizer.presentation.home_screen.HomeScreen
import com.app.ringtonerandomizer.presentation.home_screen.RingtoneListViewModel

@SuppressLint("NewApi")
fun NavGraphBuilder.homeScreen(
    context: Context,
    snackBarHostState: SnackbarHostState,
    viewModel: RingtoneListViewModel,
    permissionMap: MutableState<Map<String, Boolean>>,
    modifier: Modifier = Modifier
) {
    navigation(
        startDestination = Screen.HomeScreen.route,
        route = Tab.Home.route
    ) {
        // loading home screen
        composable(
            route = Screen.HomeScreen.route
        ) {
            HomeScreen(
                state = viewModel.state.collectAsStateWithLifecycle().value,
                context = context,
                onClick = viewModel::onClick,
                snackBarHostState = snackBarHostState,
                modifier = modifier,
                permissionMap = permissionMap
            )
        }
    }
}