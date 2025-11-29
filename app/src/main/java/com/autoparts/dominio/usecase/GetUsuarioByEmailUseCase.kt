package com.autoparts.dominio.usecase

import com.autoparts.Data.Remote.Resource
import com.autoparts.dominio.model.Usuarios
import com.autoparts.dominio.repository.UsuarioRepository
import javax.inject.Inject

/**
 * Caso de uso para obtener un usuario por su email.
 */
class GetUsuarioByEmailUseCase @Inject constructor(
    private val repository: UsuarioRepository
) {
    suspend operator fun invoke(email: String): Resource<Usuarios> {
        return repository.getUserByEmail(email)
    }
}

