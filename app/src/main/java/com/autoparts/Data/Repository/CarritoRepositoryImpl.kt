package com.autoparts.Data.Repository

import com.autoparts.Data.Mappers.toDomain
import com.autoparts.Data.Mappers.toDto
import com.autoparts.Data.Remote.CarritoApiService
import com.autoparts.Data.Remote.Resource
import com.autoparts.dominio.model.Carrito
import com.autoparts.dominio.model.CarritoTotal
import com.autoparts.dominio.model.AddCarrito
import com.autoparts.dominio.model.UpdateCarrito
import com.autoparts.dominio.repository.CarritoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class CarritoRepositoryImpl @Inject constructor(
    private val carritoApiService: CarritoApiService
) : CarritoRepository {

    override suspend fun getCarrito(): Resource<List<Carrito>> = withContext(Dispatchers.IO) {
        try {
            val response = carritoApiService.getCarrito()
            Resource.Success(response.map { it.toDomain() })
        } catch (e: HttpException) {
            Resource.Error(e.message() ?: "Error desconocido")
        } catch (e: IOException) {
            Resource.Error("Error de conexión. Verifica tu internet.")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error desconocido")
        }
    }

    override suspend fun getCarritoTotal(): Resource<CarritoTotal> = withContext(Dispatchers.IO) {
        try {
            val response = carritoApiService.getCarritoTotal()
            Resource.Success(response.toDomain())
        } catch (e: HttpException) {
            Resource.Error(e.message() ?: "Error desconocido")
        } catch (e: IOException) {
            Resource.Error("Error de conexión. Verifica tu internet.")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error desconocido")
        }
    }

    override suspend fun addItem(addCarrito: AddCarrito): Resource<Carrito> = withContext(Dispatchers.IO) {
        try {
            val response = carritoApiService.addItem(addCarrito.toDto())
            Resource.Success(response.toDomain())
        } catch (e: HttpException) {
            Resource.Error(e.message() ?: "Error desconocido")
        } catch (e: IOException) {
            Resource.Error("Error de conexión. Verifica tu internet.")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error desconocido")
        }
    }

    override suspend fun updateItem(carritoId: Int, updateCarrito: UpdateCarrito): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            carritoApiService.updateItem(carritoId, updateCarrito.toDto())
            Resource.Success(Unit)
        } catch (e: HttpException) {
            Resource.Error(e.message() ?: "Error desconocido")
        } catch (e: IOException) {
            Resource.Error("Error de conexión. Verifica tu internet.")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error desconocido")
        }
    }

    override suspend fun deleteItem(carritoId: Int): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            carritoApiService.deleteItem(carritoId)
            Resource.Success(Unit)
        } catch (e: HttpException) {
            Resource.Error(e.message() ?: "Error desconocido")
        } catch (e: IOException) {
            Resource.Error("Error de conexión. Verifica tu internet.")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error desconocido")
        }
    }

    override suspend fun clearCarrito(): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            carritoApiService.clearCarrito()
            Resource.Success(Unit)
        } catch (e: HttpException) {
            Resource.Error(e.message() ?: "Error desconocido")
        } catch (e: IOException) {
            Resource.Error("Error de conexión. Verifica tu internet.")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error desconocido")
        }
    }
}

