package com.tecsup.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.ayudacampus.data.local.entity.Usuario
import com.tecsup.ayudacampus.data.local.relation.TicketConDetalle
import com.tecsup.ayudacampus.data.repository.AyudaRepository
import com.tecsup.ayudacampus.model.EstadoTicket
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TicketDetalleViewModel(
    private val repo: AyudaRepository,
    private val usuario: Usuario,
    private val ticketId: Int
) : ViewModel() {


    val ticket: StateFlow<TicketConDetalle?> = repo.observarTicket(ticketId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)


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