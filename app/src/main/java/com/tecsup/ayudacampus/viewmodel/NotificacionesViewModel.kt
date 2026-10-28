
package com.tecsup.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.ayudacampus.data.local.entity.Notificacion
import com.tecsup.ayudacampus.data.repository.AyudaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotificacionesViewModel(
    private val repository: AyudaRepository,
    private val usuarioId: Int
) : ViewModel() {

    val notificaciones: StateFlow<List<Notificacion>> =
        repository.observarNotificaciones(usuarioId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val noLeidas: StateFlow<Int> =
        repository.contarNotificacionesNoLeidas(usuarioId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = 0
            )

    fun marcarLeida(id: Int) {
        viewModelScope.launch {
            repository.marcarNotificacionLeida(id, usuarioId)
        }
    }
}

