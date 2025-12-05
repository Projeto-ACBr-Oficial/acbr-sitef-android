package com.mjtech.fintesthub.android.ui.common.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.mjtech.fintesthub.android.ui.common.routes.AppScreen
import com.mjtech.fintesthub.android.ui.common.routes.PaymentRoute
import com.mjtech.fintesthub.android.ui.theme.Gray500
import com.mjtech.fintesthub.android.ui.theme.MainColor

@Composable
fun BottomNavigationBar(navController: NavController) {

    val items = listOf(
        AppScreen.PaymentNavBar,
        AppScreen.SettingsNavBar
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        items.forEach { screen ->
            AddItem(
                screen = screen,
                currentDestination = currentDestination,
                navController = navController
            )
        }
    }
}

@Composable
fun RowScope.AddItem(
    screen: AppScreen,
    currentDestination: androidx.navigation.NavDestination?,
    navController: NavController
) {

    val screenRouteClassName = screen.route::class.java.simpleName

    val selectedHierarchical = currentDestination?.route?.contains(screenRouteClassName) == true

    val finalSelection = selectedHierarchical

    NavigationBarItem(
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MainColor,
            unselectedIconColor = Gray500,
            selectedTextColor = MainColor,
            unselectedTextColor = Gray500,
            indicatorColor = MainColor.copy(alpha = 0.2f)
        ),
        selected = finalSelection,
        onClick = {
            navController.navigate(screen.route) {
                popUpTo(PaymentRoute) {
                    saveState = true
                    inclusive = false
                }
                launchSingleTop = true
                restoreState = true
            }
        },
        icon = {
            if (screen.icon != null) Icon(
                imageVector = screen.icon,
                contentDescription = screen.label
            ) else Icon(
                painter = androidx.compose.ui.res.painterResource(id = screen.painter!!),
                contentDescription = screen.label
            )
        },
        label = {
            Text(text = screen.label)
        }
    )
}