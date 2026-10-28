
package com.tecsup.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.ayudacampus.data.local.entity.Usuario
import com.tecsup.ayudacampus.data.repository.AyudaRepository
import com.tecsup.ayudacampus.model.Rol
import com.tecsup.ayudacampus.security.PasswordUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegistroViewModel(
    private val repository: AyudaRepository
) : ViewModel() {

    private val _mensaje = MutableStateFlow("")
    val mensaje: StateFlow<String> = _mensaje

    private val _registroExitoso = MutableStateFlow(false)
    val registroExitoso: StateFlow<Boolean> = _registroExitoso

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    fun registrar(
        nombre: String,
        correo: String,
        clave: String,
        confirmarClave: String
    ) {
        if (_cargando.value) return

        val nombreLimpio = nombre.trim()
        val correoLimpio = correo.trim().lowercase()

        when {
            nombreLimpio.isBlank() ||
                    correoLimpio.isBlank() ||
                    clave.isBlank() ||
                    confirmarClave.isBlank() -> {
                _mensaje.value = "Completa todos los campos"
                return
            }

            !android.util.Patterns.EMAIL_ADDRESS
                .matcher(correoLimpio).matches() -> {
                _mensaje.value = "Ingresa un correo válido"
                return
            }

            clave.length < 8 -> {
                _mensaje.value =
                    "La contraseña debe tener al menos 8 caracteres"
                return
            }

            clave != confirmarClave -> {
                _mensaje.value = "Las contraseñas no coinciden"
                return
            }
        }

        viewModelScope.launch {
            _cargando.value = true
            _mensaje.value = ""

            try {
                val existente =
                    repository.buscarUsuarioPorCorreo(correoLimpio)

                if (existente != null) {
                    _mensaje.value = "Este correo ya está registrado"
                    return@launch
                }


                val claveSegura = withContext(Dispatchers.Default) {
                    PasswordUtils.generarHash(clave)
                }

                repository.registrarUsuario(
                    Usuario(
                        nombre = nombreLimpio,
                        correo = correoLimpio,
                        clave = claveSegura,
                        rol = Rol.CLIENTE.name
                    )
                )

                _registroExitoso.value = true
                _mensaje.value = "Registro exitoso"

            } catch (e: Exception) {
                _mensaje.value = "No se pudo completar el registro"
            } finally {
                _cargando.value = false
            }
        }
    }
}
