package com.autoparts.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.autoparts.presentation.Inicio.HomeScreen
import com.autoparts.presentation.Login.LoginScreen
import com.autoparts.presentation.productos.ProductosScreen
import com.autoparts.presentation.productodetalle.ProductoDetalleScreen


@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
){
    NavHost(
        navController = navController,
        startDestination = "home_screen",
        modifier = modifier
    ){
        composable(
            route = Screen.Home.route,
            arguments = listOf(
                navArgument(Screen.Home.ARG){
                    type = NavType.IntType
                    defaultValue = -1
                    nullable = false
                }
            )
        ) {backStackEntry ->
            val id = backStackEntry.arguments?.getInt(Screen.Home.ARG)?.takeIf { it != -1 }
            HomeScreen(
                navController,
                userId = id as String?
            )
        }
        composable("home_screen") {
            HomeScreen(
                navController,
                userId = null
            )
        }
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }
        composable(Screen.Productos.route) {
            ProductosScreen(navController)
        }
        composable(
            route = Screen.ProductoDetalle.route,
            arguments = listOf(
                navArgument(Screen.ProductoDetalle.ARG){type = NavType.IntType}
            )
        ) { backStackEntry ->
            val productoId = backStackEntry.arguments?.getInt(Screen.ProductoDetalle.ARG) ?: 0
            ProductoDetalleScreen(
                navController = navController,
                productoId = productoId
            )
        }
    }

}
