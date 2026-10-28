package com.tecsup.ayudacampus.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecsup.ayudacampus.data.local.entity.Usuario
import com.tecsup.ayudacampus.model.Rol
import com.tecsup.ayudacampus.ui.cliente.DetalleTicketScreen
import com.tecsup.ayudacampus.ui.cliente.LoginScreen
import com.tecsup.ayudacampus.ui.cliente.MisTicketsScreen
import com.tecsup.ayudacampus.ui.cliente.NuevoTicketScreen
import com.tecsup.ayudacampus.ui.cliente.PerfilScreen
import com.tecsup.ayudacampus.ui.cliente.SplashScreen
import com.tecsup.ayudacampus.ui.soporte.AjustesScreen
import com.tecsup.ayudacampus.ui.soporte.AtenderTicketScreen
import com.tecsup.ayudacampus.ui.soporte.BandejaScreen
import com.tecsup.ayudacampus.ui.soporte.CategoriasScreen
import com.tecsup.ayudacampus.ui.soporte.FormularioCategoriaScreen
import com.tecsup.ayudacampus.ui.soporte.PanelScreen
import com.tecsup.ayudacampus.viewmodel.AtenderViewModel
import com.tecsup.ayudacampus.viewmodel.AyudaViewModelFactory
import com.tecsup.ayudacampus.viewmodel.BandejaViewModel
import com.tecsup.ayudacampus.viewmodel.CategoriasViewModel
import com.tecsup.ayudacampus.viewmodel.MisTicketsViewModel
import com.tecsup.ayudacampus.viewmodel.NuevoTicketViewModel
import com.tecsup.ayudacampus.viewmodel.PanelViewModel
import com.tecsup.ayudacampus.viewmodel.SesionViewModel
import com.tecsup.ayudacampus.viewmodel.TicketDetalleViewModel
import com.tecsup.ayudacampus.ui.cliente.NotificacionesScreen
import com.tecsup.ayudacampus.viewmodel.NotificacionesViewModel
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


    val cambiarPerfil = {
        sesionVm.cambiarDePerfil {
            navController.navigate(destinoPrincipal(sesionVm.usuarioActual.value)) {
                popUpTo(navController.graph.id) { inclusive = true }
            }
        }
    }


    val irATab: (String) -> Unit = { ruta ->
        navController.navigate(ruta) {
            popUpTo(Rutas.PANEL)
            launchSingleTop = true
        }
    }


    val irAClienteTab: (String) -> Unit = { ruta ->
        navController.navigate(ruta) {
            popUpTo(Rutas.MIS_TICKETS)
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
                    onPerfil = { irAClienteTab(Rutas.PERFIL) },
                    onNotificaciones = {
                        navController.navigate(Rutas.NOTIFICACIONES)
                    }
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
                    onPerfil = { irAClienteTab(Rutas.PERFIL) }
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
                    onPerfil = { irAClienteTab(Rutas.PERFIL) }
                )
            }
        }

        composable(Rutas.NOTIFICACIONES) {

            val usuario by sesionVm.usuarioActual.collectAsState()

            usuario?.let { u ->

                val vm: NotificacionesViewModel = viewModel(
                    key = "notificaciones_${u.id}",
                    factory = factory.paraUsuario(u)
                )

                NotificacionesScreen(
                    vm = vm,
                    onVolver = {
                        navController.popBackStack()
                    },
                    onTicket = { ticketId ->
                        navController.navigate(Rutas.detalle(ticketId))
                    }
                )
            }
        }


        composable(Rutas.PERFIL) {
            val usuario by sesionVm.usuarioActual.collectAsState()
            usuario?.let { u ->
                val ticketsVm: MisTicketsViewModel = viewModel(
                    key = "mis_tickets_${u.id}",
                    factory = factory.paraUsuario(u)
                )
                PerfilScreen(
                    usuario = u,
                    sesionVm = sesionVm,
                    ticketsVm = ticketsVm,
                    onTickets = { irAClienteTab(Rutas.MIS_TICKETS) },
                    onNuevo = { irAClienteTab(Rutas.NUEVO_TICKET) },
                    onCambiarPerfil = { cambiarPerfil() },
                    onCerrarSesion = { salirAlLogin() }
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
                onAjustes = { irATab(Rutas.AJUSTES) }
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
                onAjustes = { irATab(Rutas.AJUSTES) }
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
                    onAjustes = { irATab(Rutas.AJUSTES) }
                )
            }
        }

        composable(Rutas.CATEGORIAS) {
            val vm: CategoriasViewModel = viewModel(factory = factory)
            CategoriasScreen(
                vm = vm,
                onNueva = { navController.navigate(Rutas.formularioCategoria()) },
                onEditar = { id -> navController.navigate(Rutas.formularioCategoria(id)) },
                onPanel = { irATab(Rutas.PANEL) },
                onTickets = { irATab(Rutas.BANDEJA) },
                onAjustes = { irATab(Rutas.AJUSTES) }
            )
        }

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

        composable(Rutas.AJUSTES) {
            val usuario by sesionVm.usuarioActual.collectAsState()
            usuario?.let { u ->
                AjustesScreen(
                    usuario = u,
                    sesionVm = sesionVm,
                    onPanel = { irATab(Rutas.PANEL) },
                    onTickets = { irATab(Rutas.BANDEJA) },
                    onCategorias = { irATab(Rutas.CATEGORIAS) },
                    onCambiarPerfil = { cambiarPerfil() },
                    onCerrarSesion = { salirAlLogin() }
                )
            }
        }
    }
}