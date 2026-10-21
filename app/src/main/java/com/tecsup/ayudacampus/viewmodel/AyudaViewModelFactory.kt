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
        modelClass.isAssignableFrom(PanelViewModel::class.java) ->
            PanelViewModel(repo) as T
        modelClass.isAssignableFrom(BandejaViewModel::class.java) ->
            BandejaViewModel(repo) as T

        else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    }


    fun paraUsuario(usuario: Usuario): ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
                modelClass.isAssignableFrom(MisTicketsViewModel::class.java) ->
                    MisTicketsViewModel(repo, usuario) as T
                modelClass.isAssignableFrom(NuevoTicketViewModel::class.java) ->
                    NuevoTicketViewModel(repo, usuario, prefs) as T
                modelClass.isAssignableFrom(NuevoTicketViewModel::class.java) ->
                    NuevoTicketViewModel(repo, usuario, prefs) as T

                else -> this@AyudaViewModelFactory.create(modelClass)
            }

        }

    fun paraTicket(usuario: Usuario, ticketId: Int): ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
                modelClass.isAssignableFrom(TicketDetalleViewModel::class.java) ->
                    TicketDetalleViewModel(repo, usuario, ticketId) as T
                modelClass.isAssignableFrom(AtenderViewModel::class.java) ->
                    AtenderViewModel(repo, usuario, ticketId) as T
                else -> this@AyudaViewModelFactory.create(modelClass)
            }
        }
}