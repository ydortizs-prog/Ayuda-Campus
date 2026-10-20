package com.tecsup.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.ayudacampus.data.local.relation.TicketConDetalle
import com.tecsup.ayudacampus.data.repository.AyudaRepository
import com.tecsup.ayudacampus.model.EstadoTicket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class BandejaViewModel(repo: AyudaRepository) : ViewModel() {

    private val _filtro = MutableStateFlow(FiltroTicket.TODOS)
    val filtro: StateFlow<FiltroTicket> = _filtro.asStateFlow()


    val tickets: StateFlow<List<TicketConDetalle>> =
        combine(repo.observarTodosLosTickets(), _filtro) { lista, filtro ->
            lista.filter { coincide(it, filtro) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun elegirFiltro(f: FiltroTicket) {
        _filtro.value = f
    }

    private fun coincide(t: TicketConDetalle, filtro: FiltroTicket): Boolean =
        when (filtro) {
            FiltroTicket.TODOS -> true
            FiltroTicket.ABIERTOS -> t.ticket.estado == EstadoTicket.ABIERTO.name
            FiltroTicket.EN_PROCESO -> t.ticket.estado == EstadoTicket.EN_PROCESO.name
            FiltroTicket.RESUELTOS -> t.ticket.estado == EstadoTicket.RESUELTO.name
        }
}