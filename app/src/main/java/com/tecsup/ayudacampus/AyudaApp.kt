package com.tecsup.ayudacampus

import android.app.Application
import com.tecsup.ayudacampus.data.local.AyudaDatabase
import com.tecsup.ayudacampus.data.local.DatosIniciales
import com.tecsup.ayudacampus.data.repository.AyudaRepository
import com.tecsup.ayudacampus.data.repository.AyudaRepositoryRoom
import com.tecsup.ayudacampus.data.prefs.PreferenciasStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AyudaApp : Application() {

    val database: AyudaDatabase by lazy { AyudaDatabase.obtener(this) }
    val repository: AyudaRepository by lazy { AyudaRepositoryRoom(database) }

    val preferencias: PreferenciasStore by lazy { PreferenciasStore(this) }

    override fun onCreate() {
        super.onCreate()
        CoroutineScope(Dispatchers.IO).launch {
            DatosIniciales.sembrar(database)
        }
    }
}