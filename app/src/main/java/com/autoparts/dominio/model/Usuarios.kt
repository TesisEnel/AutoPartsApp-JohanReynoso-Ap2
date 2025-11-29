package com.autoparts.dominio.model

data class Usuarios(
    val id: String = "",
    val userName: String? = null,
    val email: String = "",
    val phoneNumber: String? = null,
    val emailConfirmed: Boolean = false,
    val roles: List<String> = emptyList()
)

data class CreateUser(
    val email: String,
    val password: String,
    val phoneNumber: String? = null
)

// Para login
data class LoginUser(
    val email: String,
    val password: String
)

data class UpdateUser(
    val email: String? = null,
    val phoneNumber: String? = null,
    val currentPassword: String? = null,
    val newPassword: String? = null
)

