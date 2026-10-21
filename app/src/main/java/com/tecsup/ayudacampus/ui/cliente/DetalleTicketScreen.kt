package com.tecsup.ayudacampus.ui.cliente

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.ayudacampus.data.local.entity.Usuario
import com.tecsup.ayudacampus.data.local.relation.TicketConDetalle
import com.tecsup.ayudacampus.model.EstadoTicket
import com.tecsup.ayudacampus.model.Prioridad
import com.tecsup.ayudacampus.model.Rol
import com.tecsup.ayudacampus.model.marcaDeTiempo
import com.tecsup.ayudacampus.model.numeroTicket
import com.tecsup.ayudacampus.model.tiempoRelativo
import com.tecsup.ayudacampus.ui.components.AppTopBar
import com.tecsup.ayudacampus.ui.components.AyudaButton
import com.tecsup.ayudacampus.ui.components.AyudaTextField
import com.tecsup.ayudacampus.ui.components.BarraCliente
import com.tecsup.ayudacampus.ui.components.BurbujaComentario
import com.tecsup.ayudacampus.ui.components.EstadoChip
import com.tecsup.ayudacampus.ui.components.EstiloBoton
import com.tecsup.ayudacampus.ui.components.Estrellas
import com.tecsup.ayudacampus.ui.components.PrioridadChip
import com.tecsup.ayudacampus.ui.components.StepperTicket
import com.tecsup.ayudacampus.ui.components.iconoCategoria
import com.tecsup.ayudacampus.ui.theme.EstadoResuelto
import com.tecsup.ayudacampus.ui.theme.PrioridadAlta
import com.tecsup.ayudacampus.ui.theme.PrioridadBaja
import com.tecsup.ayudacampus.ui.theme.PrioridadMedia
import com.tecsup.ayudacampus.viewmodel.TicketDetalleViewModel

@Composable
fun DetalleTicketScreen(
    vm: TicketDetalleViewModel,
    usuario: Usuario,
    onVolver: () -> Unit,
    onNuevo: () -> Unit,
    onPerfil: () -> Unit
) {
    val detalle by vm.ticket.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppTopBar(
                titulo = detalle?.let { "Ticket ${numeroTicket(it.ticket.id)}" } ?: "Ticket",
                onAtras = onVolver
            )
        },
        bottomBar = {
            BarraCliente(
                seleccion = 0,
                onTickets = onVolver,
                onNuevo = onNuevo,
                onPerfil = onPerfil
            )
        }
    ) { padding ->
        val d = detalle

        Column(
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding)
                .imePadding()
        ) {
            if (d == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                ContenidoDetalle(d = d, usuario = usuario, vm = vm)
            }
        }
    }
}

@Composable
private fun ColumnScope.ContenidoDetalle(
    d: TicketConDetalle,
    usuario: Usuario,
    vm: TicketDetalleViewModel
) {
    val t = d.ticket
    val estado = EstadoTicket.valueOf(t.estado)
    val sla by vm.slaTexto.collectAsState()
    val comentarios = d.comentarios.sortedBy { it.fecha }
    val listState = rememberLazyListState()

    var texto by remember { mutableStateOf("") }
    var calificacion by remember { mutableIntStateOf(0) }
    var comentarioFinal by remember { mutableStateOf("") }
    var cantidadAnterior by remember { mutableIntStateOf(comentarios.size) }


    LaunchedEffect(comentarios.size) {
        if (comentarios.size > cantidadAnterior) {
            val total = listState.layoutInfo.totalItemsCount
            if (total > 0) listState.animateScrollToItem(total - 1)
        }
        cantidadAnterior = comentarios.size
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .weight(1f)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(t.titulo, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        EstadoChip(estado)
                        PrioridadChip(Prioridad.valueOf(t.prioridad))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = iconoCategoria(d.categoria.icono),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${d.categoria.nombre} · ${t.ubicacion} · ${tiempoRelativo(t.fecha)}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        sla?.let { textoSla ->
            item {
                val prioridad = try { Prioridad.valueOf(t.prioridad) } catch (_: Exception) { Prioridad.BAJA }
                val prioridadColor = when (prioridad) {
                    Prioridad.ALTA -> PrioridadAlta
                    Prioridad.MEDIA -> PrioridadMedia
                    Prioridad.BAJA -> PrioridadBaja
                }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = prioridadColor.copy(alpha = 0.12f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = null,
                            tint = prioridadColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = textoSla,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = prioridadColor
                        )
                    }
                }
            }
        }


        item { StepperTicket(estadoActual = estado) }


        item { Text("Conversación", fontSize = 18.sp, fontWeight = FontWeight.Bold) }

        if (comentarios.isEmpty()) {
            item {
                Text(
                    "Aún no hay comentarios",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        items(comentarios, key = { it.id }) { c ->
            val propio = c.autorId == usuario.id
            BurbujaComentario(
                autor = if (propio) "Tú" else c.autorNombre,
                hora = marcaDeTiempo(c.fecha),
                texto = c.texto,
                propio = propio,
                autorEsSoporte = c.autorRol == Rol.ADMIN.name
            )
        }


        if (estado == EstadoTicket.RESUELTO) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = EstadoResuelto.copy(alpha = 0.15f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Filled.Check, contentDescription = null, tint = EstadoResuelto)
                        Column {
                            Text(
                                "Soporte marcó este ticket como resuelto.",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text("¿El problema se solucionó?", fontSize = 13.sp)
                        }
                    }
                }
            }
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Califica la atención", fontWeight = FontWeight.Bold)
                        Estrellas(valor = calificacion, onCambio = { calificacion = it })
                        Text(
                            text = if (calificacion == 0) "Toca una estrella" else "$calificacion de 5 estrellas",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            item {
                AyudaTextField(
                    valor = comentarioFinal,
                    onValorChange = { comentarioFinal = it },
                    etiqueta = "Comentario final (opcional)",
                    singleLine = false,
                    minLineas = 2
                )
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    AyudaButton(
                        texto = "Confirmar y cerrar",
                        onClick = { vm.cerrar(calificacion, comentarioFinal) },
                        enabled = calificacion > 0
                    )
                    AyudaButton(
                        texto = "Reabrir ticket",
                        onClick = { vm.reabrir() },
                        estilo = EstiloBoton.PELIGRO
                    )
                }
            }
        }


        if (estado == EstadoTicket.CERRADO) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Ticket cerrado", fontWeight = FontWeight.Bold)
                        Estrellas(valor = t.calificacion ?: 0, tamano = 28.dp)
                        Text(
                            text = "Calificación recibida: ${t.calificacion ?: 0} de 5",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        t.fechaCierre?.let {
                            Text(
                                text = "Cerrado ${tiempoRelativo(it).lowercase()}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }


    if (estado == EstadoTicket.ABIERTO || estado == EstadoTicket.EN_PROCESO) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = texto,
                onValueChange = { texto = it },
                placeholder = { Text("Escribe un comentario...") },
                singleLine = true,
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier.weight(1f)
            )
            FilledIconButton(
                onClick = {
                    vm.comentar(texto)
                    texto = ""
                },
                enabled = texto.isNotBlank(),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar comentario")
            }
        }
    }
}

