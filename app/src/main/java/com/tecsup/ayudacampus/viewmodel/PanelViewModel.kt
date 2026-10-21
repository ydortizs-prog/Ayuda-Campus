package com.tecsup.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.ayudacampus.data.repository.AyudaRepository
import com.tecsup.ayudacampus.model.EstadoTicket
import com.tecsup.ayudacampus.ui.components.graficos.DatoGrafico
import com.tecsup.ayudacampus.ui.theme.EstadoAbierto
import com.tecsup.ayudacampus.ui.theme.EstadoCerrado
import com.tecsup.ayudacampus.ui.theme.EstadoEnProceso
import com.tecsup.ayudacampus.ui.theme.EstadoResuelto
import com.tecsup.ayudacampus.ui.theme.VioletaPrimario
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class PanelViewModel(private val repo: AyudaRepository) : ViewModel() {

    private fun contador(estado: EstadoTicket): StateFlow<Int> =
        repo.observarCantidadPorEstado(estado)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)


    val abiertos: StateFlow<Int> = contador(EstadoTicket.ABIERTO)
    val enProceso: StateFlow<Int> = contador(EstadoTicket.EN_PROCESO)
    val resueltos: StateFlow<Int> = contador(EstadoTicket.RESUELTO)
    private val cerrados: StateFlow<Int> = contador(EstadoTicket.CERRADO)


    val porEstado: StateFlow<List<DatoGrafico>> =
        combine(abiertos, enProceso, resueltos, cerrados) { a, e, r, c ->
            listOf(
                DatoGrafico("Abierto", a, EstadoAbierto),
                DatoGrafico("En proceso", e, EstadoEnProceso),
                DatoGrafico("Resuelto", r, EstadoResuelto),
                DatoGrafico("Cerrado", c, EstadoCerrado)
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    val porCategoria: StateFlow<List<DatoGrafico>> =
        repo.observarCantidadPorCategoria()
            .map { lista ->
                lista.map {

                    DatoGrafico(it.nombre.substringBefore(" /"), it.total, VioletaPrimario)
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    @OptIn(ExperimentalCoroutinesApi::class)
    val altasSinAtender: StateFlow<Int> =
        flow {
            while (true) {
                emit(System.currentTimeMillis() - 60 * 60_000L)
                delay(60_000)
            }
        }
            .flatMapLatest { limite -> repo.observarAltasSinAtender(limite) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val tiempoPromedioResolucion: StateFlow<Double?> =
        repo.observarTiempoPromedioResolucion()
            .map { millis -> millis?.let { it / 3600000.0 } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}