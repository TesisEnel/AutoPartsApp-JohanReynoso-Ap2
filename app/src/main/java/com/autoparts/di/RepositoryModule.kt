package com.autoparts.di

import com.autoparts.Data.Repository.ProductoRepositoryImpl
import com.autoparts.Data.Repository.UsuarioRepositoryImpl
import com.autoparts.dominio.repository.ProductoRepository
import com.autoparts.dominio.repository.UsuarioRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindGastoRepository(
        impl: UsuarioRepositoryImpl
    ): UsuarioRepository

    @Binds
    @Singleton
    abstract fun bindProductoRepository(
        impl: ProductoRepositoryImpl
    ): ProductoRepository
}