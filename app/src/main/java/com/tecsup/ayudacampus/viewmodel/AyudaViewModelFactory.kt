package com.tecsup.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tecsup.ayudacampus.data.prefs.PreferenciasStore
import com.tecsup.ayudacampus.data.repository.AyudaRepository
import com.tecsup.ayudacampus.data.local.entity.Usuario

class AyudaViewModelFactory(
    private val repo: AyudaRepository,
    private val prefs: PreferenciasStore
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(CategoriasViewModel::class.java) ->
            CategoriasViewModel(repo) as T
        modelClass.isAssignableFrom(SesionViewModel::class.java) ->
            SesionViewModel(repo, prefs) as T
        // Aquí se agregan los demás ViewModels en sus pasos
        else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    }

    // Para los ViewModels que necesitan saber qué usuario inició sesión
    fun paraUsuario(usuario: Usuario): ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
                modelClass.isAssignableFrom(MisTicketsViewModel::class.java) ->
                    MisTicketsViewModel(repo, usuario) as T
                // Los de los pasos 8, 9 y 12 se agregan aquí
                else -> this@AyudaViewModelFactory.create(modelClass)
            }
        }
}