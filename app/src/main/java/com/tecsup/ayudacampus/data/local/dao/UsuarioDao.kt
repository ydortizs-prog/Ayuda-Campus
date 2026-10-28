
package com.tecsup.ayudacampus.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.tecsup.ayudacampus.data.local.entity.Usuario

@Dao
interface UsuarioDao {

    @Query("SELECT * FROM usuarios WHERE correo = :correo AND clave = :clave LIMIT 1")
    suspend fun login(correo: String, clave: String): Usuario?

    @Insert
    suspend fun insertarTodos(lista: List<Usuario>)

    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contar(): Int

    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: Int): Usuario?

    // REGISTRO DE ESTUDIANTES

    @Query("""
        SELECT * FROM usuarios
        WHERE LOWER(correo) = LOWER(:correo)
        LIMIT 1
    """)
    suspend fun buscarPorCorreo(correo: String): Usuario?

    @Insert
    suspend fun insertarUsuario(usuario: Usuario): Long
}
