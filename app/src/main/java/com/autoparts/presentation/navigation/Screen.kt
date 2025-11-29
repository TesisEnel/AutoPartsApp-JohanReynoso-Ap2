package com.autoparts.presentation.navigation

sealed class Screen(val route: String){
    data object Home: Screen("home_screen?id={id}"){
        const val ARG = "id"
        fun createRoute(id: String? = null) = if (id != null) "home_screen?id=$id" else "home_screen"
    }
    data object Login: Screen("login_screen")
    data object Productos: Screen("productos_screen")
    data object ProductoDetalle: Screen("producto_detalle/{productoId}"){
        const val ARG = "productoId"
        fun createRoute(productoId: Int) = "producto_detalle/$productoId"
    }
}