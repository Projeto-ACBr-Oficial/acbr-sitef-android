package com.mjtech.fintesthub.android.ui.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mjtech.fintesthub.android.ui.common.components.BottomNavigationBar
import com.mjtech.fintesthub.android.ui.common.routes.CheckoutRoute
import com.mjtech.fintesthub.android.ui.common.routes.ConfigurationRoute
import com.mjtech.fintesthub.android.ui.common.routes.HomeRoute
import com.mjtech.fintesthub.android.ui.common.routes.PaymentRoute
import com.mjtech.fintesthub.android.ui.configuration.ConfigurationPage
import com.mjtech.fintesthub.android.ui.payment.PaymentPage

@Composable
fun HomeScreen(rootNavController: NavController) {

    val nestedNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = nestedNavController)
        }
    ) { innerPadding ->
        NavHost(
            navController = nestedNavController,
            startDestination = PaymentRoute,
            modifier = Modifier.padding(innerPadding)
        ) {

            composable<PaymentRoute> {
                PaymentPage(onNavigate = { amountInCents ->
                    rootNavController.navigate(CheckoutRoute(valueInCents = amountInCents)) {
                        popUpTo(HomeRoute) { inclusive = false }
                    }
                })
            }

            composable<ConfigurationRoute> {
                ConfigurationPage()
            }
        }
    }
}