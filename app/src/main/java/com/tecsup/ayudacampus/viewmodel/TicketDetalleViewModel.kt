package com.tecsup.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.ayudacampus.data.local.entity.Usuario
import com.tecsup.ayudacampus.data.local.relation.TicketConDetalle
import com.tecsup.ayudacampus.data.repository.AyudaRepository
import com.tecsup.ayudacampus.model.EstadoTicket
import com.tecsup.ayudacampus.model.Prioridad
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

class TicketDetalleViewModel(
    private val repo: AyudaRepository,
    private val usuario: Usuario,
    private val ticketId: Int
) : ViewModel() {


    val ticket: StateFlow<TicketConDetalle?> = repo.observarTicket(ticketId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val slaTexto: StateFlow<String?> =
        flow {
            while (true) {
                emit(System.currentTimeMillis())
                delay(60_000)
            }
        }.combine(repo.observarTicket(ticketId)) { ahora, detalle ->
            if (detalle == null) return@combine null
            val t = detalle.ticket
            val estado = try { EstadoTicket.valueOf(t.estado) } catch (e: Exception) { EstadoTicket.ABIERTO }
            if (estado == EstadoTicket.RESUELTO || estado == EstadoTicket.CERRADO) {
                return@combine null
            }
            val prioridad = try { Prioridad.valueOf(t.prioridad) } catch (e: Exception) { Prioridad.BAJA }
            val horasSla = when (prioridad) {
                Prioridad.ALTA -> 4L
                Prioridad.MEDIA -> 24L
                Prioridad.BAJA -> 72L
            }
            val limite = t.fecha + horasSla * 3600_000L
            val diff = limite - ahora
            val minutosTotal = (diff / 60_000L).absoluteValue
            val horas = minutosTotal / 60L
            val minutos = minutosTotal % 60L

            if (diff >= 0) {
                if (horas > 0) {
                    if (minutos > 0) "SLA: $horas h $minutos min restantes"
                    else "SLA: $horas h restantes"
                } else {
                    "SLA: $minutos min restantes"
                }
            } else {
                if (horas > 0) {
                    if (minutos > 0) "SLA vencido hace $horas h $minutos min"
                    else "SLA vencido hace $horas h"
                } else {
                    if (minutos > 0) "SLA vencido hace $minutos min"
                    else "SLA vencido hace un momento"
                }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)


    fun comentar(texto: String) {
        val limpio = texto.trim()
        val actual = ticket.value ?: return
        if (limpio.isEmpty() || actual.ticket.estado == EstadoTicket.CERRADO.name) return

        viewModelScope.launch {
            repo.agregarComentario(ticketId, usuario, limpio)
        }
    }


    fun cerrar(calificacion: Int, comentarioFinal: String = "") {
        val actual = ticket.value ?: return
        if (actual.ticket.estado != EstadoTicket.RESUELTO.name) return
        if (calificacion !in 1..5) return

        viewModelScope.launch {
            val final = comentarioFinal.trim()
            if (final.isNotEmpty()) {
                repo.agregarComentario(ticketId, usuario, final)
            }
            repo.cerrarConCalificacion(ticketId, calificacion)
        }
    }


    fun reabrir() {
        val actual = ticket.value ?: return
        if (actual.ticket.estado != EstadoTicket.RESUELTO.name) return

        viewModelScope.launch {
            repo.reabrir(ticketId)
        }
    }
}