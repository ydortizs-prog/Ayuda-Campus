package com.tecsup.ayudacampus.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun numeroTicket(id: Int): String = "#" + id.toString().padStart(4, '0')


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


fun marcaDeTiempo(fecha: Long, ahora: Long = System.currentTimeMillis()): String =
    if (ahora - fecha < 24 * 60 * 60_000L) {
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(fecha))
    } else {
        tiempoRelativo(fecha, ahora)
    }