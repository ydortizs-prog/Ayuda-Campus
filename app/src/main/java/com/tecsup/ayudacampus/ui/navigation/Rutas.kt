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

    const val DETALLE = "detalle/{ticketId}"
    fun detalle(id: Int) = "detalle/$id"
}