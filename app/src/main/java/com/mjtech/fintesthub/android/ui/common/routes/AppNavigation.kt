package com.mjtech.fintesthub.android.ui.common.routes

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.mjtech.fintesthub.android.ui.checkout.CheckoutScreen
import com.mjtech.fintesthub.android.ui.home.HomeScreen
import com.mjtech.fintesthub.android.ui.splash.SplashScreen

@Composable
fun AppNavigation(paddingValues: PaddingValues) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = SplashRoute
    ) {

        composable<SplashRoute> {
            SplashScreen(
                onNavigate = {
                    navController.navigate(HomeRoute) {
                        popUpTo(SplashRoute) { inclusive = true }
                    }
                }
            )
        }

        composable<HomeRoute> {
            HomeScreen(rootNavController = navController)
        }

        composable<CheckoutRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<CheckoutRoute>()
            CheckoutScreen(paddingValues = paddingValues, valueInCents = route.valueInCents, onCancel =  {
                navController.popBackStack()
            })
        }
    }
}