package com.tecsup.ayudacampus.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tecsup.ayudacampus.data.local.entity.Usuario
import com.tecsup.ayudacampus.model.Rol
import com.tecsup.ayudacampus.ui.cliente.LoginScreen
import com.tecsup.ayudacampus.ui.cliente.SplashScreen
import com.tecsup.ayudacampus.ui.components.AyudaButton
import com.tecsup.ayudacampus.ui.components.EstiloBoton
import com.tecsup.ayudacampus.ui.soporte.CategoriasScreen
import com.tecsup.ayudacampus.viewmodel.AyudaViewModelFactory
import com.tecsup.ayudacampus.viewmodel.CategoriasViewModel
import com.tecsup.ayudacampus.viewmodel.SesionViewModel


private fun destinoPrincipal(usuario: Usuario?): String = when {
    usuario == null -> Rutas.LOGIN
    usuario.rol == Rol.ADMIN.name -> Rutas.PANEL
    else -> Rutas.MIS_TICKETS
}

@Composable
fun NavAyuda(factory: AyudaViewModelFactory, sesionVm: SesionViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Rutas.SPLASH) {

        composable(Rutas.SPLASH) {
            val sesionLista by sesionVm.sesionRestaurada.collectAsState()
            val usuario by sesionVm.usuarioActual.collectAsState()
            var splashTerminado by remember { mutableStateOf(false) }

            SplashScreen(onTerminar = { splashTerminado = true })


            LaunchedEffect(sesionLista, splashTerminado) {
                if (sesionLista && splashTerminado) {
                    navController.navigate(destinoPrincipal(usuario)) {
                        popUpTo(Rutas.SPLASH) { inclusive = true }
                    }
                }
            }
        }

        composable(Rutas.LOGIN) {
            val usuario by sesionVm.usuarioActual.collectAsState()


            LaunchedEffect(usuario) {
                usuario?.let {
                    navController.navigate(destinoPrincipal(it)) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                }
            }
            LoginScreen(vm = sesionVm)
        }


        composable(Rutas.MIS_TICKETS) {
            val usuario by sesionVm.usuarioActual.collectAsState()
            PantallaProvisional(
                titulo = "Mis tickets",
                detalle = "Sesión de ${usuario?.nombre ?: ""} (Estudiante)",
                onCerrarSesion = {
                    sesionVm.cerrarSesion()
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }


        composable(Rutas.PANEL) {
            val usuario by sesionVm.usuarioActual.collectAsState()
            PantallaProvisional(
                titulo = "Panel de soporte",
                detalle = "Sesión de ${usuario?.nombre ?: ""} (Soporte TI)",
                onCerrarSesion = {
                    sesionVm.cerrarSesion()
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }

        composable(Rutas.CATEGORIAS) {
            val vm: CategoriasViewModel = viewModel(factory = factory)
            CategoriasScreen(vm = vm, onEditar = { /* Paso 11: formulario de categoría */ })
        }
    }
}

@Composable
private fun PantallaProvisional(titulo: String, detalle: String, onCerrarSesion: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            titulo,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(detalle)
        Spacer(modifier = Modifier.height(24.dp))
        AyudaButton(
            texto = "Cerrar sesión",
            onClick = onCerrarSesion,
            estilo = EstiloBoton.PELIGRO
        )
    }
}