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
import com.tecsup.ayudacampus.ui.cliente.MisTicketsScreen
import com.tecsup.ayudacampus.ui.cliente.NuevoTicketScreen
import com.tecsup.ayudacampus.ui.cliente.SplashScreen
import com.tecsup.ayudacampus.ui.components.AyudaButton
import com.tecsup.ayudacampus.ui.components.EstiloBoton
import com.tecsup.ayudacampus.ui.soporte.CategoriasScreen
import com.tecsup.ayudacampus.viewmodel.AyudaViewModelFactory
import com.tecsup.ayudacampus.viewmodel.CategoriasViewModel
import com.tecsup.ayudacampus.viewmodel.MisTicketsViewModel
import com.tecsup.ayudacampus.viewmodel.NuevoTicketViewModel
import com.tecsup.ayudacampus.viewmodel.SesionViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.tecsup.ayudacampus.ui.cliente.DetalleTicketScreen
import com.tecsup.ayudacampus.viewmodel.TicketDetalleViewModel
import com.tecsup.ayudacampus.ui.soporte.BandejaScreen
import com.tecsup.ayudacampus.ui.soporte.PanelScreen
import com.tecsup.ayudacampus.viewmodel.BandejaViewModel
import com.tecsup.ayudacampus.viewmodel.PanelViewModel
import com.tecsup.ayudacampus.ui.soporte.AtenderTicketScreen
import com.tecsup.ayudacampus.ui.soporte.FormularioCategoriaScreen
import com.tecsup.ayudacampus.viewmodel.AtenderViewModel


private fun destinoPrincipal(usuario: Usuario?): String = when {
    usuario == null -> Rutas.LOGIN
    usuario.rol == Rol.ADMIN.name -> Rutas.PANEL
    else -> Rutas.MIS_TICKETS
}

@Composable
fun NavAyuda(factory: AyudaViewModelFactory, sesionVm: SesionViewModel) {
    val navController = rememberNavController()


    val salirAlLogin = {
        sesionVm.cerrarSesion()
        navController.navigate(Rutas.LOGIN) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    val irATab: (String) -> Unit = { ruta ->
        navController.navigate(ruta) {
            popUpTo(Rutas.PANEL)
            launchSingleTop = true
        }
    }

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
            usuario?.let { u ->
                val vm: MisTicketsViewModel = viewModel(
                    key = "mis_tickets_${u.id}",
                    factory = factory.paraUsuario(u)
                )
                MisTicketsScreen(
                    vm = vm,
                    usuario = u,
                    onTicket = { id -> navController.navigate(Rutas.detalle(id)) },
                    onNuevo = { navController.navigate(Rutas.NUEVO_TICKET) },
                    onPerfil = salirAlLogin
                )
            }
        }

        composable(
            route = Rutas.DETALLE,
            arguments = listOf(navArgument("ticketId") { type = NavType.IntType })
        ) { entrada ->
            val ticketId = entrada.arguments?.getInt("ticketId") ?: return@composable
            val usuario by sesionVm.usuarioActual.collectAsState()
            usuario?.let { u ->
                val vm: TicketDetalleViewModel = viewModel(
                    key = "detalle_${u.id}_$ticketId",
                    factory = factory.paraTicket(u, ticketId)
                )
                DetalleTicketScreen(
                    vm = vm,
                    usuario = u,
                    onVolver = { navController.popBackStack() },
                    onNuevo = { navController.navigate(Rutas.NUEVO_TICKET) },
                    onPerfil = salirAlLogin
                )
            }
        }

        composable(Rutas.NUEVO_TICKET) {
            val usuario by sesionVm.usuarioActual.collectAsState()
            usuario?.let { u ->
                val vm: NuevoTicketViewModel = viewModel(
                    key = "nuevo_ticket_${u.id}",
                    factory = factory.paraUsuario(u)
                )
                NuevoTicketScreen(
                    vm = vm,
                    onVolver = { navController.popBackStack() },
                    onCreado = { navController.popBackStack() },
                    onPerfil = salirAlLogin
                )
            }
        }


        composable(Rutas.PANEL) {
            val vm: PanelViewModel = viewModel(factory = factory)
            PanelScreen(
                vm = vm,
                onVerAltas = { irATab(Rutas.BANDEJA) },
                onTickets = { irATab(Rutas.BANDEJA) },
                onCategorias = { irATab(Rutas.CATEGORIAS) },
                // TEMPORAL: hasta el Paso 12, "Ajustes" cierra la sesión
                onAjustes = salirAlLogin
            )
        }
        composable(Rutas.BANDEJA) {
            val vm: BandejaViewModel = viewModel(factory = factory)
            BandejaScreen(
                vm = vm,
                onVolver = { irATab(Rutas.PANEL) },
                onTicket = { id -> navController.navigate(Rutas.atender(id)) },
                onPanel = { irATab(Rutas.PANEL) },
                onCategorias = { irATab(Rutas.CATEGORIAS) },
                onAjustes = salirAlLogin
            )
        }
        composable(Rutas.CATEGORIAS) {
            val vm: CategoriasViewModel = viewModel(factory = factory)
            CategoriasScreen(
                vm = vm,
                onNueva = { navController.navigate(Rutas.formularioCategoria()) },
                onEditar = { id -> navController.navigate(Rutas.formularioCategoria(id)) },
                onPanel = { irATab(Rutas.PANEL) },
                onTickets = { irATab(Rutas.BANDEJA) },
                // TEMPORAL: hasta el Paso 12, "Ajustes" cierra la sesión
                onAjustes = salirAlLogin
            )
            composable(
                route = Rutas.FORMULARIO_CATEGORIA,
                arguments = listOf(
                    navArgument("id") {
                        type = NavType.IntType
                        defaultValue = -1
                    }
                )
            ) { entrada ->
                val id = entrada.arguments?.getInt("id") ?: -1
                val vm: CategoriasViewModel = viewModel(factory = factory)
                FormularioCategoriaScreen(
                    vm = vm,
                    categoriaId = id.takeIf { it != -1 },
                    onVolver = { navController.popBackStack() }
                )
            }
            composable(
                route = Rutas.ATENDER,
                arguments = listOf(navArgument("ticketId") { type = NavType.IntType })
            ) { entrada ->
                val ticketId = entrada.arguments?.getInt("ticketId") ?: return@composable
                val usuario by sesionVm.usuarioActual.collectAsState()
                usuario?.let { u ->
                    val vm: AtenderViewModel = viewModel(
                        key = "atender_${u.id}_$ticketId",
                        factory = factory.paraTicket(u, ticketId)
                    )
                    AtenderTicketScreen(
                        vm = vm,
                        usuario = u,
                        onVolver = { navController.popBackStack() },
                        onPanel = { irATab(Rutas.PANEL) },
                        onCategorias = { irATab(Rutas.CATEGORIAS) },
                        onAjustes = salirAlLogin
                    )
                }
            }
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