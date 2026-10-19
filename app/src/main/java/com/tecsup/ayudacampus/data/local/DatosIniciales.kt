package com.tecsup.ayudacampus.data.local

import com.tecsup.ayudacampus.data.local.entity.Categoria
import com.tecsup.ayudacampus.data.local.entity.Comentario
import com.tecsup.ayudacampus.data.local.entity.Ticket
import com.tecsup.ayudacampus.data.local.entity.Usuario
import com.tecsup.ayudacampus.model.EstadoTicket
import com.tecsup.ayudacampus.model.Prioridad
import com.tecsup.ayudacampus.model.Rol

object DatosIniciales {

    private const val MINUTO = 60_000L
    private const val HORA = 60 * MINUTO
    private const val DIA = 24 * HORA

    suspend fun sembrar(db: AyudaDatabase) {
        val usuarioDao = db.usuarioDao()
        if (usuarioDao.contar() != 0) return
        val categoriaDao = db.categoriaDao()
        val ticketDao = db.ticketDao()
        val ahora = System.currentTimeMillis()


        val hardware = categoriaDao.insertar(Categoria(nombre = "Hardware", icono = "pc")).toInt()
        val software = categoriaDao.insertar(Categoria(nombre = "Software", icono = "code")).toInt()
        val red = categoriaDao.insertar(Categoria(nombre = "Red / Wi-Fi", icono = "wifi")).toInt()
        val cuenta = categoriaDao.insertar(Categoria(nombre = "Cuenta", icono = "key")).toInt()
        val impresion = categoriaDao.insertar(Categoria(nombre = "Impresión", icono = "printer")).toInt()

        // ---------- 2 usuarios ----------
        usuarioDao.insertarTodos(
            listOf(
                Usuario(nombre = "Juan Pérez", correo = "juan@campus.test", clave = "1234", rol = Rol.CLIENTE.name),
                Usuario(nombre = "Soporte TI", correo = "soporte@campus.test", clave = "admin", rol = Rol.ADMIN.name)
            )
        )
        val juan = usuarioDao.login("juan@campus.test", "1234")!!
        val soporte = usuarioDao.login("soporte@campus.test", "admin")!!


        val t1 = ticketDao.insertarTicket(
            Ticket(
                clienteId = juan.id, categoriaId = red,
                titulo = "Sin Wi-Fi en el laboratorio 3",
                descripcion = "Desde ayer no hay señal de Wi-Fi en todo el laboratorio. Afecta a 30 alumnos durante la clase.",
                ubicacion = "Lab 3 · Pabellón B",
                prioridad = Prioridad.ALTA.name, estado = EstadoTicket.EN_PROCESO.name,
                fecha = ahora - 2 * HORA
            )
        ).toInt()

        ticketDao.insertarTicket(
            Ticket(
                clienteId = juan.id, categoriaId = hardware,
                titulo = "Servidor del aula 305 caído",
                descripcion = "El servidor del aula no responde y no se puede iniciar la clase práctica.",
                ubicacion = "Aula 305",
                prioridad = Prioridad.ALTA.name, estado = EstadoTicket.ABIERTO.name,
                fecha = ahora - 3 * HORA
            )
        )

        ticketDao.insertarTicket(
            Ticket(
                clienteId = juan.id, categoriaId = hardware,
                titulo = "Teclado no responde",
                descripcion = "El teclado de la PC 12 dejó de responder al presionar las teclas.",
                ubicacion = "Aula 204",
                prioridad = Prioridad.MEDIA.name, estado = EstadoTicket.ABIERTO.name,
                fecha = ahora - 1 * DIA
            )
        )

        ticketDao.insertarTicket(
            Ticket(
                clienteId = juan.id, categoriaId = software,
                titulo = "Instalar Android Studio",
                descripcion = "Necesito instalar Android Studio en la PC del laboratorio para el curso de móviles.",
                ubicacion = "Lab 1",
                prioridad = Prioridad.MEDIA.name, estado = EstadoTicket.ABIERTO.name,
                fecha = ahora - 3 * HORA
            )
        )

        val t5 = ticketDao.insertarTicket(
            Ticket(
                clienteId = juan.id, categoriaId = impresion,
                titulo = "No puedo imprimir en biblioteca",
                descripcion = "La impresora de la biblioteca no imprime mis documentos desde el campus virtual.",
                ubicacion = "Biblioteca",
                prioridad = Prioridad.BAJA.name, estado = EstadoTicket.RESUELTO.name,
                fecha = ahora - 3 * DIA
            )
        ).toInt()

        ticketDao.insertarTicket(
            Ticket(
                clienteId = juan.id, categoriaId = cuenta,
                titulo = "Olvidé mi contraseña del aula virtual",
                descripcion = "No puedo ingresar al aula virtual porque olvidé mi contraseña.",
                ubicacion = "Virtual",
                prioridad = Prioridad.BAJA.name, estado = EstadoTicket.CERRADO.name,
                fecha = ahora - 5 * DIA,
                fechaCierre = ahora - 4 * DIA,
                calificacion = 5
            )
        )


        ticketDao.insertarComentario(
            Comentario(
                ticketId = t1, autorId = soporte.id, autorNombre = soporte.nombre, autorRol = soporte.rol,
                texto = "Revisaremos el access point del pabellón B hoy.",
                fecha = ahora - 90 * MINUTO
            )
        )
        ticketDao.insertarComentario(
            Comentario(
                ticketId = t1, autorId = juan.id, autorNombre = juan.nombre, autorRol = juan.rol,
                texto = "Gracias, quedo atento.",
                fecha = ahora - 80 * MINUTO
            )
        )
        ticketDao.insertarComentario(
            Comentario(
                ticketId = t1, autorId = soporte.id, autorNombre = soporte.nombre, autorRol = soporte.rol,
                texto = "Reiniciamos el equipo. ¿Ya tienes señal?",
                fecha = ahora - 30 * MINUTO
            )
        )
        ticketDao.insertarComentario(
            Comentario(
                ticketId = t5, autorId = soporte.id, autorNombre = soporte.nombre, autorRol = soporte.rol,
                texto = "Reinstalamos el driver de la impresora. Ya debería imprimir.",
                fecha = ahora - 2 * DIA
            )
        )
    }
}