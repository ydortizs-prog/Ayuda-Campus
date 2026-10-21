package com.tecsup.ayudacampus.ui.soporte

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tecsup.ayudacampus.model.EstadoTicket
import com.tecsup.ayudacampus.model.Prioridad
import com.tecsup.ayudacampus.model.numeroTicket
import com.tecsup.ayudacampus.model.tiempoRelativo
import com.tecsup.ayudacampus.ui.components.AppTopBar
import com.tecsup.ayudacampus.ui.components.BarraSoporte
import com.tecsup.ayudacampus.ui.components.TicketCard
import com.tecsup.ayudacampus.viewmodel.BandejaViewModel
import com.tecsup.ayudacampus.viewmodel.FiltroTicket

@Composable
fun BandejaScreen(
    vm: BandejaViewModel,
    onVolver: () -> Unit,
    onTicket: (Int) -> Unit,
    onPanel: () -> Unit,
    onCategorias: () -> Unit,
    onAjustes: () -> Unit
) {
    val tickets by vm.tickets.collectAsState()
    val filtro by vm.filtro.collectAsState()
    val busqueda by vm.busqueda.collectAsState()
    val prioridadFiltro by vm.prioridad.collectAsState()
    val categoriaIdFiltro by vm.categoriaId.collectAsState()
    val categorias by vm.categorias.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { AppTopBar(titulo = "Tickets", onAtras = onVolver) },
        bottomBar = {
            BarraSoporte(
                seleccion = 1,
                onPanel = onPanel,
                onTickets = { },
                onCategorias = onCategorias,
                onAjustes = onAjustes
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = busqueda,
                onValueChange = { vm.buscar(it) },
                placeholder = { Text("Buscar por título, número o cliente...") },
                singleLine = true,
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier.fillMaxWidth()
            )

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

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = prioridadFiltro == null,
                        onClick = { vm.elegirPrioridad(null) },
                        label = { Text("Todas") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
                items(Prioridad.entries) { p ->
                    val label = when (p) {
                        Prioridad.ALTA -> "Alta"
                        Prioridad.MEDIA -> "Media"
                        Prioridad.BAJA -> "Baja"
                    }
                    FilterChip(
                        selected = prioridadFiltro == p,
                        onClick = { vm.elegirPrioridad(p) },
                        label = { Text(label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = categoriaIdFiltro == null,
                        onClick = { vm.elegirCategoria(null) },
                        label = { Text("Todas") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
                items(categorias, key = { it.id }) { c ->
                    FilterChip(
                        selected = categoriaIdFiltro == c.id,
                        onClick = { vm.elegirCategoria(c.id) },
                        label = { Text(c.nombre) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            if (tickets.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "No hay tickets aquí",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(top = 4.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(tickets, key = { it.ticket.id }) { t ->
                        TicketCard(
                            titulo = t.ticket.titulo,
                            subtitulo = "${numeroTicket(t.ticket.id)} · ${t.cliente.nombre}",
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
