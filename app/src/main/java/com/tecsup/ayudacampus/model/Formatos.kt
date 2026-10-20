package com.tecsup.ayudacampus.model

// "#0012": el número del ticket a partir de su id
fun numeroTicket(id: Int): String = "#" + id.toString().padStart(4, '0')

// "Hace 25 min", "Hace 2 h", "Ayer", "Hace 3 d"
fun tiempoRelativo(fecha: Long, ahora: Long = System.currentTimeMillis()): String {
    val minutos = (ahora - fecha) / 60_000
    return when {
        minutos < 1 -> "Ahora"
        minutos < 60 -> "Hace $minutos min"
        minutos < 24 * 60 -> "Hace ${minutos / 60} h"
        minutos < 48 * 60 -> "Ayer"
        else -> "Hace ${minutos / (24 * 60)} d"
    }
}