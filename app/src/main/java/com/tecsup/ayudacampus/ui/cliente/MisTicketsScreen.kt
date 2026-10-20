package com.tecsup.ayudacampus.ui.cliente

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.ayudacampus.data.local.entity.Usuario
import com.tecsup.ayudacampus.model.EstadoTicket
import com.tecsup.ayudacampus.model.Prioridad
import com.tecsup.ayudacampus.model.numeroTicket
import com.tecsup.ayudacampus.model.tiempoRelativo
import com.tecsup.ayudacampus.ui.components.BarraCliente
import com.tecsup.ayudacampus.ui.components.TicketCard
import com.tecsup.ayudacampus.viewmodel.FiltroTicket
import com.tecsup.ayudacampus.viewmodel.MisTicketsViewModel

@Composable
fun MisTicketsScreen(
    vm: MisTicketsViewModel,
    usuario: Usuario,
    onTicket: (Int) -> Unit,
    onNuevo: () -> Unit,
    onPerfil: () -> Unit
) {
    val tickets by vm.tickets.collectAsState()
    val filtro by vm.filtro.collectAsState()
    val busqueda by vm.busqueda.collectAsState()
    val abiertos by vm.abiertos.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            BarraCliente(
                seleccion = 0,
                onTickets = { },
                onNuevo = onNuevo,
                onPerfil = onPerfil
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNuevo,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(20.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Nuevo ticket")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .statusBarsPadding()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Saludo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Mis tickets", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "Hola, ${usuario.nombre.substringBefore(" ")} · " +
                                "$abiertos ${if (abiertos == 1) "abierto" else "abiertos"}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(Icons.Filled.Notifications, contentDescription = "Notificaciones")
            }

            // Buscador
            OutlinedTextField(
                value = busqueda,
                onValueChange = vm::buscar,
                placeholder = { Text("Buscar por título o número...") },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // Chips de filtro
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(FiltroTicket.entries) { f ->
                    FilterChip(
                        selected = f == filtro,
                        onClick = { vm.elegirFiltro(f) },
                        label = { Text(f.etiqueta) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            // Lista o estado vacío
            if (tickets.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "No tienes tickets aquí",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(top = 4.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(tickets, key = { it.ticket.id }) { t ->
                        TicketCard(
                            titulo = t.ticket.titulo,
                            subtitulo = "${numeroTicket(t.ticket.id)} · ${t.ticket.ubicacion}",
                            icono = t.categoria.icono,
                            estado = EstadoTicket.valueOf(t.ticket.estado),
                            prioridad = Prioridad.valueOf(t.ticket.prioridad),
                            tiempo = tiempoRelativo(t.ticket.fecha),
                            onClick = { onTicket(t.ticket.id) }
                        )
                    }
                }
            }
        }
    }
}
