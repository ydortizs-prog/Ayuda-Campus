package com.tecsup.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.ayudacampus.data.local.entity.Categoria
import com.tecsup.ayudacampus.data.local.entity.Usuario
import com.tecsup.ayudacampus.data.repository.AyudaRepository
import com.tecsup.ayudacampus.model.Prioridad
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.tecsup.ayudacampus.data.prefs.PreferenciasStore

data class FormularioTicket(
    val categoriaId: Int? = null,
    val titulo: String = "",
    val ubicacion: String = "",
    val prioridad: Prioridad = Prioridad.MEDIA,
    val descripcion: String = ""
)


data class ErroresTicket(
    val categoria: String? = null,
    val titulo: String? = null,
    val descripcion: String? = null
) {
    val hayErrores: Boolean
        get() = categoria != null || titulo != null || descripcion != null
}

class NuevoTicketViewModel(
    private val repo: AyudaRepository,
    private val usuario: Usuario,
    prefs: PreferenciasStore
) : ViewModel() {


    val categorias: StateFlow<List<Categoria>> = repo.observarCategoriasActivas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val mesaAbierta: StateFlow<Boolean> = prefs.recibirTickets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    private val _form = MutableStateFlow(FormularioTicket())
    val form: StateFlow<FormularioTicket> = _form.asStateFlow()

    private val _errores = MutableStateFlow(ErroresTicket())
    val errores: StateFlow<ErroresTicket> = _errores.asStateFlow()


    private val _creado = MutableStateFlow(false)
    val creado: StateFlow<Boolean> = _creado.asStateFlow()

    private var enviando = false

    fun elegirCategoria(id: Int) {
        _form.update { it.copy(categoriaId = id) }
        _errores.update { it.copy(categoria = null) }
    }

    fun cambiarTitulo(texto: String) {
        _form.update { it.copy(titulo = texto) }
        _errores.update { it.copy(titulo = null) }
    }

    fun cambiarUbicacion(texto: String) {
        _form.update { it.copy(ubicacion = texto) }
    }

    fun elegirPrioridad(p: Prioridad) {
        _form.update { it.copy(prioridad = p) }
    }

    fun cambiarDescripcion(texto: String) {
        _form.update { it.copy(descripcion = texto) }
        _errores.update { it.copy(descripcion = null) }
    }

    fun enviar() {
        if (enviando || !mesaAbierta.value) return
        val f = _form.value

        val errores = ErroresTicket(
            categoria = if (f.categoriaId == null) "Elige una categoría" else null,
            titulo = if (f.titulo.trim().length < 5) {
                "El título debe tener al menos 5 caracteres"
            } else null,
            descripcion = if (f.descripcion.trim().length < 10) {
                "La descripción debe tener al menos 10 caracteres"
            } else null
        )
        _errores.value = errores
        if (errores.hayErrores) return

        enviando = true
        viewModelScope.launch {
            repo.crearTicket(
                clienteId = usuario.id,
                categoriaId = f.categoriaId!!,
                titulo = f.titulo.trim(),
                descripcion = f.descripcion.trim(),
                ubicacion = f.ubicacion.trim(),
                prioridad = f.prioridad
            )
            _creado.value = true
        }
    }
}