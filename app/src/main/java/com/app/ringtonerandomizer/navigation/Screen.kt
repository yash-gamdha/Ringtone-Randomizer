package com.app.ringtonerandomizer.navigation

import androidx.annotation.DrawableRes
import com.app.ringtonerandomizer.R
import androidx.compose.ui.res.vectorResource

sealed class Screen(val route: String) {
    data object HomeScreen: Screen("home_screen")
    data object AboutScreen: Screen("about_screen")
}

sealed class Tab(
    val route: String,
    val label: String,
    @DrawableRes val icon: Int,
    @DrawableRes val selectedIcon: Int
) {
    data object Home: Tab(
        route = "home_tab",
        label = "Home",
        icon = NavIcons.homeIcon,
        selectedIcon = NavIcons.homeIconSelected
    )

    data object About: Tab(
        route = "about_tab",
        label = "About",
        icon = NavIcons.aboutIcon,
        selectedIcon = NavIcons.aboutIconSelected
    )
}

private object NavIcons {
    val homeIcon = R.drawable.home_variant_outline
    val homeIconSelected = R.drawable.home_variant_filled

    val aboutIcon = R.drawable.info_outline
    val aboutIconSelected = R.drawable.info
}

val navList = listOf(
    Tab.Home, Tab.About
)