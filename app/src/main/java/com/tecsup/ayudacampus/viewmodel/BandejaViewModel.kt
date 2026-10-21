package com.tecsup.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.ayudacampus.data.local.entity.Categoria
import com.tecsup.ayudacampus.data.local.relation.TicketConDetalle
import com.tecsup.ayudacampus.data.repository.AyudaRepository
import com.tecsup.ayudacampus.model.EstadoTicket
import com.tecsup.ayudacampus.model.Prioridad
import com.tecsup.ayudacampus.model.numeroTicket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class BandejaViewModel(repo: AyudaRepository) : ViewModel() {

    private val _filtro = MutableStateFlow(FiltroTicket.TODOS)
    val filtro: StateFlow<FiltroTicket> = _filtro.asStateFlow()

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    private val _prioridad = MutableStateFlow<Prioridad?>(null)
    val prioridad: StateFlow<Prioridad?> = _prioridad.asStateFlow()

    private val _categoriaId = MutableStateFlow<Int?>(null)
    val categoriaId: StateFlow<Int?> = _categoriaId.asStateFlow()

    val categorias: StateFlow<List<Categoria>> = repo.observarCategorias()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    val tickets: StateFlow<List<TicketConDetalle>> =
        combine(
            repo.observarTodosLosTickets(),
            _filtro,
            _busqueda,
            _prioridad,
            _categoriaId
        ) { lista, filtroEstado, texto, prio, catId ->
            lista.filter { t ->
                coincideEstado(t, filtroEstado) &&
                coincidePrioridad(t, prio) &&
                coincideCategoria(t, catId) &&
                coincideTexto(t, texto)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun elegirFiltro(f: FiltroTicket) {
        _filtro.value = f
    }

    fun buscar(texto: String) {
        _busqueda.value = texto
    }

    fun elegirPrioridad(p: Prioridad?) {
        _prioridad.value = p
    }

    fun elegirCategoria(id: Int?) {
        _categoriaId.value = id
    }

    private fun coincideEstado(t: TicketConDetalle, filtro: FiltroTicket): Boolean =
        when (filtro) {
            FiltroTicket.TODOS -> true
            FiltroTicket.ABIERTOS -> t.ticket.estado == EstadoTicket.ABIERTO.name
            FiltroTicket.EN_PROCESO -> t.ticket.estado == EstadoTicket.EN_PROCESO.name
            FiltroTicket.RESUELTOS -> t.ticket.estado == EstadoTicket.RESUELTO.name
        }

    private fun coincidePrioridad(t: TicketConDetalle, p: Prioridad?): Boolean =
        p == null || t.ticket.prioridad == p.name

    private fun coincideCategoria(t: TicketConDetalle, catId: Int?): Boolean =
        catId == null || t.ticket.categoriaId == catId

    private fun coincideTexto(t: TicketConDetalle, texto: String): Boolean {
        val q = texto.trim()
        if (q.isEmpty()) return true
        val numero = numeroTicket(t.ticket.id)
        return t.ticket.titulo.contains(q, ignoreCase = true) ||
                numero.contains(q.removePrefix("#"), ignoreCase = true) ||
                t.cliente.nombre.contains(q, ignoreCase = true)
    }
}