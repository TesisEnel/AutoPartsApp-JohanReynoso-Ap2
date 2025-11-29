package com.autoparts.presentation.Inicio

import com.autoparts.dominio.model.Usuarios
import com.autoparts.dominio.model.Producto

data class InicioUiState(
    val userId: String? = null,
    val isLoadingUser: Boolean = false,
    val isLoadingList: Boolean = false,
    val isLoadingProductos: Boolean = false,
    val listUsuarios: List<Usuarios> = emptyList(),
    val listProductos: List<Producto> = emptyList(),
    val email: String = "",
    val emailError: String? = null,
    val phoneNumber: String = "",
    val phoneNumberError: String? = null,
    val userMessage: String? = null,
    val showDialog: Boolean = false,
    val searchQuery: String = ""
)
