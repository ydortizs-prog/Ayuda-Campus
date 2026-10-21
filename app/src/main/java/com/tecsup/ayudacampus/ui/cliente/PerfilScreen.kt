package com.tecsup.ayudacampus.ui.cliente

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.ayudacampus.data.local.entity.Usuario
import com.tecsup.ayudacampus.ui.components.AppTopBar
import com.tecsup.ayudacampus.ui.components.AyudaButton
import com.tecsup.ayudacampus.ui.components.BarraCliente
import com.tecsup.ayudacampus.ui.components.CabeceraUsuario
import com.tecsup.ayudacampus.ui.components.EstiloBoton
import com.tecsup.ayudacampus.ui.components.FilaPreferencia
import com.tecsup.ayudacampus.ui.components.NotaDataStore
import com.tecsup.ayudacampus.ui.components.TarjetaCambiarPerfil
import com.tecsup.ayudacampus.ui.components.TarjetaContador
import com.tecsup.ayudacampus.ui.components.TarjetaPreferencias
import com.tecsup.ayudacampus.ui.theme.EstadoAbierto
import com.tecsup.ayudacampus.ui.theme.EstadoResuelto
import com.tecsup.ayudacampus.viewmodel.MisTicketsViewModel
import com.tecsup.ayudacampus.viewmodel.SesionViewModel

@Composable
fun PerfilScreen(
    usuario: Usuario,
    sesionVm: SesionViewModel,
    ticketsVm: MisTicketsViewModel,
    onTickets: () -> Unit,
    onNuevo: () -> Unit,
    onCambiarPerfil: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val abiertos by ticketsVm.abiertos.collectAsState()
    val resueltos by ticketsVm.resueltos.collectAsState()
    val total by ticketsVm.total.collectAsState()
    val modoOscuro by sesionVm.modoOscuro.collectAsState()
    val notificaciones by sesionVm.notificaciones.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { AppTopBar(titulo = "Perfil", onAtras = onTickets) },
        bottomBar = {
            BarraCliente(
                seleccion = 2,
                onTickets = onTickets,
                onNuevo = onNuevo,
                onPerfil = { }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            CabeceraUsuario(usuario.nombre, usuario.correo, "Estudiante")


            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaContador("Abiertos", abiertos, EstadoAbierto, Modifier.weight(1f))
                TarjetaContador("Resueltos", resueltos, EstadoResuelto, Modifier.weight(1f))
                TarjetaContador("Total", total, MaterialTheme.colorScheme.primary, Modifier.weight(1f))
            }

            TarjetaCambiarPerfil(
                subtitulo = "Modo práctica: ir a vista Soporte TI",
                onClick = onCambiarPerfil
            )

            Text("Preferencias", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            TarjetaPreferencias {
                FilaPreferencia(
                    titulo = "Modo oscuro",
                    subtitulo = "Cambia el tema de la app",
                    activo = modoOscuro,
                    onCambio = sesionVm::cambiarModoOscuro
                )
                HorizontalDivider()
                FilaPreferencia(
                    titulo = "Notificaciones",
                    subtitulo = "Avisar cambios de estado",
                    activo = notificaciones,
                    onCambio = sesionVm::cambiarNotificaciones
                )
            }

            NotaDataStore()

            AyudaButton(
                texto = "Cerrar sesión",
                onClick = onCerrarSesion,
                estilo = EstiloBoton.PELIGRO
            )
        }
    }
}