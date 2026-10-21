package com.tecsup.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.ayudacampus.data.local.entity.Usuario
import com.tecsup.ayudacampus.data.local.relation.TicketConDetalle
import com.tecsup.ayudacampus.data.repository.AyudaRepository
import com.tecsup.ayudacampus.model.EstadoTicket
import com.tecsup.ayudacampus.model.Prioridad
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AtenderViewModel(
    private val repo: AyudaRepository,
    private val usuario: Usuario,
    private val ticketId: Int
) : ViewModel() {

    val ticket: StateFlow<TicketConDetalle?> = repo.observarTicket(ticketId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)


    private val _estadoElegido = MutableStateFlow<EstadoTicket?>(null)
    val estadoElegido: StateFlow<EstadoTicket?> = _estadoElegido.asStateFlow()

    private val _prioridadElegida = MutableStateFlow<Prioridad?>(null)
    val prioridadElegida: StateFlow<Prioridad?> = _prioridadElegida.asStateFlow()

    private fun estaCerrado(): Boolean =
        ticket.value?.ticket?.estado == EstadoTicket.CERRADO.name


    fun cambiarEstado(estado: EstadoTicket) {
        if (estaCerrado() || estado == EstadoTicket.CERRADO) return
        _estadoElegido.value = estado
    }

    fun cambiarPrioridad(p: Prioridad) {
        if (estaCerrado()) return
        _prioridadElegida.value = p
    }


    fun responder(texto: String) {
        val limpio = texto.trim()
        if (limpio.isEmpty() || estaCerrado()) return
        viewModelScope.launch {
            repo.agregarComentario(ticketId, usuario, limpio)
        }
    }


    fun guardar(alTerminar: () -> Unit) {
        val actual = ticket.value ?: return
        if (estaCerrado()) return

        viewModelScope.launch {
            _estadoElegido.value?.let {
                if (it != EstadoTicket.CERRADO && it.name != actual.ticket.estado) {
                    repo.cambiarEstado(ticketId, it)
                }
            }
            _prioridadElegida.value?.let {
                if (it.name != actual.ticket.prioridad) {
                    repo.cambiarPrioridad(ticketId, it)
                }
            }
            alTerminar()
        }
    }
}