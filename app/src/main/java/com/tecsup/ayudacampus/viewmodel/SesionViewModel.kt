package com.tecsup.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.ayudacampus.data.local.entity.Usuario
import com.tecsup.ayudacampus.data.prefs.PreferenciasStore
import com.tecsup.ayudacampus.data.repository.AyudaRepository
import com.tecsup.ayudacampus.model.Rol
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SesionViewModel(
    private val repo: AyudaRepository,
    private val prefs: PreferenciasStore
) : ViewModel() {

    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    val modoOscuro: StateFlow<Boolean> = prefs.modoOscuro
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)


    private val _sesionRestaurada = MutableStateFlow(false)
    val sesionRestaurada: StateFlow<Boolean> = _sesionRestaurada.asStateFlow()

    init {
        viewModelScope.launch {
            val perfil = prefs.perfilActivo.first()
            val id = prefs.usuarioId.first()
            if (perfil.isNotEmpty() && id != 0) {
                _usuarioActual.value = repo.buscarUsuario(id)
            }
            _sesionRestaurada.value = true
        }
    }

    fun login(correo: String, clave: String, rolSeleccionado: Rol) {
        if (correo.isBlank() || clave.isBlank()) {
            _error.value = "Ingresa tu correo y tu contraseña"
            return
        }
        viewModelScope.launch {
            val usuario = repo.login(correo.trim(), clave)
            when {
                usuario == null ->
                    _error.value = "Correo o contraseña incorrectos"

                usuario.rol != rolSeleccionado.name ->
                    _error.value = if (rolSeleccionado == Rol.ADMIN) {
                        "Esta cuenta no pertenece a Soporte TI"
                    } else {
                        "Esta cuenta no pertenece a un Estudiante"
                    }

                else -> {
                    prefs.guardarSesion(rolSeleccionado, usuario.id)
                    _error.value = null
                    _usuarioActual.value = usuario
                }
            }
        }
    }

    fun limpiarError() {
        _error.value = null
    }

    fun cerrarSesion() {
        _usuarioActual.value = null   // primero en memoria, para que la navegación no rebote
        viewModelScope.launch { prefs.cerrarSesion() }
    }
}