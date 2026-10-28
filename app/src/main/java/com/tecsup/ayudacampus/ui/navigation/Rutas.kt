package com.tecsup.ayudacampus.ui.navigation

object Rutas {
    const val SPLASH = "splash"
    const val LOGIN = "login"

    const val REGISTRO = "registro"
    const val MIS_TICKETS = "mis_tickets"
    const val PANEL = "panel"
    const val CATEGORIAS = "categorias"

    const val PERFIL = "perfil"
    const val AJUSTES = "ajustes"
    const val NUEVO_TICKET = "nuevo_ticket"

    const val BANDEJA = "bandeja"

    const val NOTIFICACIONES = "notificaciones"

    const val ATENDER = "atender/{ticketId}"
    fun atender(id: Int) = "atender/$id"


    const val FORMULARIO_CATEGORIA = "formulario_categoria?id={id}"
    fun formularioCategoria(id: Int? = null) =
        if (id == null) "formulario_categoria" else "formulario_categoria?id=$id"


    const val DETALLE = "detalle/{ticketId}"
    fun detalle(id: Int) = "detalle/$id"


}