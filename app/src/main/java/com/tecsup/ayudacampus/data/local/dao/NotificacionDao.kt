package com.tecsup.ayudacampus.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.tecsup.ayudacampus.data.local.entity.Notificacion
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificacionDao {

    @Insert
    suspend fun insertar(notificacion: Notificacion): Long

    @Query("""
        SELECT * FROM notificaciones
        WHERE usuarioId = :usuarioId
        ORDER BY fecha DESC, id DESC
    """)
    fun observarPorUsuario(
        usuarioId: Int
    ): Flow<List<Notificacion>>

    @Query("""
        SELECT COUNT(*) FROM notificaciones
        WHERE usuarioId = :usuarioId AND leida = 0
    """)
    fun contarNoLeidas(usuarioId: Int): Flow<Int>

    @Query("""
        UPDATE notificaciones
        SET leida = 1
        WHERE id = :id AND usuarioId = :usuarioId
    """)
    suspend fun marcarLeida(id: Int, usuarioId: Int)
}