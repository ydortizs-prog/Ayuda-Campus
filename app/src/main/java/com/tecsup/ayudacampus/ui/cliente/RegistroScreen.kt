
package com.tecsup.ayudacampus.ui.cliente

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.ayudacampus.model.Rol
import com.tecsup.ayudacampus.ui.components.AyudaButton
import com.tecsup.ayudacampus.ui.components.AyudaTextField
import com.tecsup.ayudacampus.ui.theme.AyudaTheme
import com.tecsup.ayudacampus.viewmodel.RegistroViewModel

@Composable
fun RegistroScreen(
    vm: RegistroViewModel,
    onVolver: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var confirmarClave by remember { mutableStateOf("") }

    val mensaje by vm.mensaje.collectAsState()
    val registroExitoso by vm.registroExitoso.collectAsState()
    val cargando by vm.cargando.collectAsState()

    LaunchedEffect(registroExitoso) {
        if (registroExitoso) {
            onVolver()
        }
    }

    AyudaTheme(rol = Rol.CLIENTE) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Crear cuenta",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Regístrate para acceder a AyudaCampus",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                AyudaTextField(
                    valor = nombre,
                    onValorChange = { nombre = it },
                    etiqueta = "Nombre completo",
                    icono = Icons.Outlined.Person
                )

                AyudaTextField(
                    valor = correo,
                    onValorChange = { correo = it },
                    etiqueta = "Correo electrónico",
                    icono = Icons.Outlined.Email
                )

                AyudaTextField(
                    valor = clave,
                    onValorChange = { clave = it },
                    etiqueta = "Contraseña",
                    icono = Icons.Outlined.Lock,
                    esClave = true
                )

                AyudaTextField(
                    valor = confirmarClave,
                    onValorChange = { confirmarClave = it },
                    etiqueta = "Confirmar contraseña",
                    icono = Icons.Outlined.Lock,
                    esClave = true
                )

                if (mensaje.isNotBlank()) {
                    Text(
                        text = mensaje,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp
                    )
                }

                AyudaButton(
                    texto = if (cargando) "Registrando..." else "Crear cuenta",
                    onClick = {
                        if (!cargando) {
                            vm.registrar(
                                nombre,
                                correo,
                                clave,
                                confirmarClave
                            )
                        }
                    }
                )

                TextButton(
                    onClick = onVolver,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("¿Ya tienes cuenta? Inicia sesión")
                }
            }
        }
    }
}
