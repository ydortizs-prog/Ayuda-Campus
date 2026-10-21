package com.tecsup.ayudacampus.ui.soporte

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.tecsup.ayudacampus.ui.components.BarraSoporte
import com.tecsup.ayudacampus.ui.components.CabeceraUsuario
import com.tecsup.ayudacampus.ui.components.EstiloBoton
import com.tecsup.ayudacampus.ui.components.FilaPreferencia
import com.tecsup.ayudacampus.ui.components.NotaDataStore
import com.tecsup.ayudacampus.ui.components.TarjetaCambiarPerfil
import com.tecsup.ayudacampus.ui.components.TarjetaPreferencias
import com.tecsup.ayudacampus.viewmodel.SesionViewModel

@Composable
fun AjustesScreen(
    usuario: Usuario,
    sesionVm: SesionViewModel,
    onPanel: () -> Unit,
    onTickets: () -> Unit,
    onCategorias: () -> Unit,
    onCambiarPerfil: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val modoOscuro by sesionVm.modoOscuro.collectAsState()
    val notificaciones by sesionVm.notificaciones.collectAsState()
    val recibirTickets by sesionVm.recibirTickets.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { AppTopBar(titulo = "Ajustes", onAtras = onPanel) },
        bottomBar = {
            BarraSoporte(
                seleccion = 3,
                onPanel = onPanel,
                onTickets = onTickets,
                onCategorias = onCategorias,
                onAjustes = { }
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
            CabeceraUsuario(usuario.nombre, usuario.correo, "Perfil activo")

            TarjetaCambiarPerfil(
                subtitulo = "Modo práctica: ir a vista Estudiante",
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
                    subtitulo = "Avisar tickets nuevos",
                    activo = notificaciones,
                    onCambio = sesionVm::cambiarNotificaciones
                )
                HorizontalDivider()
                FilaPreferencia(
                    titulo = "Recibir tickets",
                    subtitulo = "Abrir o cerrar la mesa de ayuda",
                    activo = recibirTickets,
                    onCambio = sesionVm::cambiarRecibirTickets
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
