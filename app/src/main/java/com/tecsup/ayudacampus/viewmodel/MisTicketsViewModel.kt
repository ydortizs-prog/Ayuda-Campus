package com.tecsup.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.ayudacampus.data.local.entity.Usuario
import com.tecsup.ayudacampus.data.local.relation.TicketConDetalle
import com.tecsup.ayudacampus.data.repository.AyudaRepository
import com.tecsup.ayudacampus.model.EstadoTicket
import com.tecsup.ayudacampus.model.numeroTicket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn


enum class FiltroTicket(val etiqueta: String) {
    TODOS("Todos"),
    ABIERTOS("Abiertos"),
    EN_PROCESO("En proceso"),
    RESUELTOS("Resueltos")
}

class MisTicketsViewModel(
    repo: AyudaRepository,
    usuario: Usuario
) : ViewModel() {

    private val _filtro = MutableStateFlow(FiltroTicket.TODOS)
    val filtro: StateFlow<FiltroTicket> = _filtro.asStateFlow()

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()


    private val delCliente = repo.observarTicketsCliente(usuario.id)


    val tickets: StateFlow<List<TicketConDetalle>> =
        combine(delCliente, _filtro, _busqueda) { lista, filtro, texto ->
            lista.filter { coincideEstado(it, filtro) && coincideTexto(it, texto) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notificacionesNoLeidas =
        repo.contarNotificacionesNoLeidas(usuario.id)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = 0
            )
    val abiertos: StateFlow<Int> = delCliente
        .map { lista -> lista.count { it.ticket.estado == EstadoTicket.ABIERTO.name } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val resueltos: StateFlow<Int> = delCliente
        .map { lista -> lista.count { it.ticket.estado == EstadoTicket.RESUELTO.name } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val total: StateFlow<Int> = delCliente
        .map { lista -> lista.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    fun elegirFiltro(f: FiltroTicket) {
        _filtro.value = f
    }

    fun buscar(texto: String) {
        _busqueda.value = texto
    }

    private fun coincideEstado(t: TicketConDetalle, filtro: FiltroTicket): Boolean =
        when (filtro) {
            FiltroTicket.TODOS -> true
            FiltroTicket.ABIERTOS -> t.ticket.estado == EstadoTicket.ABIERTO.name
            FiltroTicket.EN_PROCESO -> t.ticket.estado == EstadoTicket.EN_PROCESO.name
            FiltroTicket.RESUELTOS -> t.ticket.estado == EstadoTicket.RESUELTO.name
        }

    private fun coincideTexto(t: TicketConDetalle, texto: String): Boolean {
        val q = texto.trim()
        if (q.isEmpty()) return true
        val numero = numeroTicket(t.ticket.id)
        return t.ticket.titulo.contains(q, ignoreCase = true) ||
                numero.contains(q.removePrefix("#"))
    }
}