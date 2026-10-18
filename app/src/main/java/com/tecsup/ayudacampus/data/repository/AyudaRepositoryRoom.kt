package com.tecsup.ayudacampus.data.repository

import com.tecsup.ayudacampus.data.local.AyudaDatabase
import com.tecsup.ayudacampus.data.local.entity.Categoria
import kotlinx.coroutines.flow.Flow

class AyudaRepositoryRoom(private val db: AyudaDatabase) : AyudaRepository {

    private val categoriaDao = db.categoriaDao()

    override fun observarCategorias(): Flow<List<Categoria>> =
        categoriaDao.observarTodas()

    override fun observarCategoriasActivas(): Flow<List<Categoria>> =
        categoriaDao.observarActivas()

    override suspend fun guardarCategoria(c: Categoria) {
        categoriaDao.insertar(c)
    }

    override suspend fun eliminarCategoria(c: Categoria) {
        categoriaDao.eliminar(c)
    }

    override suspend fun cambiarActiva(id: Int, activa: Boolean) {
        categoriaDao.cambiarActiva(id, activa)
    }
}