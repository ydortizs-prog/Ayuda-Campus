package com.tecsup.ayudacampus.ui.navigation

object Rutas {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val MIS_TICKETS = "mis_tickets"
    const val PANEL = "panel"
    const val CATEGORIAS = "categorias"
    // Las demás rutas se agregan en los pasos 7 al 12
    const val NUEVO_TICKET = "nuevo_ticket"

    const val BANDEJA = "bandeja"

    const val ATENDER = "atender/{ticketId}"
    fun atender(id: Int) = "atender/$id"

    // El id es opcional: sin id = crear categoría; con id = editarla
    const val FORMULARIO_CATEGORIA = "formulario_categoria?id={id}"
    fun formularioCategoria(id: Int? = null) =
        if (id == null) "formulario_categoria" else "formulario_categoria?id=$id"


    const val DETALLE = "detalle/{ticketId}"
    fun detalle(id: Int) = "detalle/$id"


}