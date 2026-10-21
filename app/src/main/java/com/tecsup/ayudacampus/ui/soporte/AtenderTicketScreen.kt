package com.tecsup.ayudacampus.ui.soporte

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.tecsup.ayudacampus.ui.components.AppTopBar
import com.tecsup.ayudacampus.ui.components.AyudaButton
import com.tecsup.ayudacampus.ui.components.BarraSoporte
import com.tecsup.ayudacampus.ui.components.BurbujaComentario
import com.tecsup.ayudacampus.ui.theme.EstadoAbierto
import com.tecsup.ayudacampus.ui.theme.EstadoCerrado
import com.tecsup.ayudacampus.ui.theme.EstadoEnProceso
import com.tecsup.ayudacampus.ui.theme.EstadoResuelto
import com.tecsup.ayudacampus.ui.theme.PrioridadAlta
import com.tecsup.ayudacampus.ui.theme.PrioridadBaja
import com.tecsup.ayudacampus.ui.theme.PrioridadMedia
import com.tecsup.ayudacampus.viewmodel.AtenderViewModel

@Composable
fun AtenderTicketScreen(
    vm: AtenderViewModel,
    usuario: Usuario,
    onVolver: () -> Unit,
    onPanel: () -> Unit,
    onCategorias: () -> Unit,
    onAjustes: () -> Unit
) {
    val detalle by vm.ticket.collectAsState()
    val estadoElegido by vm.estadoElegido.collectAsState()
    val prioridadElegida by vm.prioridadElegida.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppTopBar(
                titulo = detalle?.let { "Atender ticket ${numeroTicket(it.ticket.id)}" } ?: "Atender ticket",
                onAtras = onVolver
            )
        },
        bottomBar = {
            BarraSoporte(
                seleccion = 1,
                onPanel = onPanel,
                onTickets = onVolver,
                onCategorias = onCategorias,
                onAjustes = onAjustes
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
                ContenidoAtender(
                    d = d,
                    usuario = usuario,
                    vm = vm,
                    estadoElegido = estadoElegido,
                    prioridadElegida = prioridadElegida,
                    onGuardado = onVolver
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.ContenidoAtender(
    d: TicketConDetalle,
    usuario: Usuario,
    vm: AtenderViewModel,
    estadoElegido: EstadoTicket?,
    prioridadElegida: Prioridad?,
    onGuardado: () -> Unit
) {
    val t = d.ticket
    val estadoActual = EstadoTicket.valueOf(t.estado)
    val cerrado = estadoActual == EstadoTicket.CERRADO
    val estadoSel = estadoElegido ?: estadoActual
    val prioridadSel = prioridadElegida ?: Prioridad.valueOf(t.prioridad)
    val comentarios = d.comentarios.sortedBy { it.fecha }
    val listState = rememberLazyListState()

    var texto by remember { mutableStateOf("") }
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
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(t.titulo, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "${d.cliente.nombre} · ${t.ubicacion} · ${d.categoria.nombre}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(t.descripcion, fontSize = 14.sp)
                }
            }
        }


        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Estado", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    val opciones = listOf(
                        Triple("Abierto", EstadoTicket.ABIERTO, EstadoAbierto),
                        Triple("En proceso", EstadoTicket.EN_PROCESO, EstadoEnProceso),
                        Triple("Resuelto", EstadoTicket.RESUELTO, EstadoResuelto),
                        Triple("Cerrado", EstadoTicket.CERRADO, EstadoCerrado)
                    )
                    opciones.forEach { (nombre, estado, color) ->
                        Pildora(
                            texto = nombre,
                            seleccionada = estadoSel == estado,
                            color = color,

                            habilitada = estado != EstadoTicket.CERRADO && !cerrado,
                            modifier = Modifier.weight(1f),
                            onClick = { vm.cambiarEstado(estado) }
                        )
                    }
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Prioridad", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    val opciones = listOf(
                        Triple("Baja", Prioridad.BAJA, PrioridadBaja),
                        Triple("Media", Prioridad.MEDIA, PrioridadMedia),
                        Triple("Alta", Prioridad.ALTA, PrioridadAlta)
                    )
                    opciones.forEach { (nombre, prioridad, color) ->
                        Pildora(
                            texto = nombre,
                            seleccionada = prioridadSel == prioridad,
                            color = color,
                            habilitada = !cerrado,
                            modifier = Modifier.weight(1f),
                            onClick = { vm.cambiarPrioridad(prioridad) }
                        )
                    }
                }
            }
        }


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
    }


    if (cerrado) {
        Text(
            text = "Este ticket está cerrado y no se puede modificar.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 13.sp,
            modifier = Modifier.padding(16.dp)
        )
    } else {
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
                placeholder = { Text("Responder al cliente...") },
                singleLine = true,
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier.weight(1f)
            )
            FilledIconButton(
                onClick = {
                    vm.responder(texto)
                    texto = ""
                },
                enabled = texto.isNotBlank(),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar respuesta")
            }
        }
        Box(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)) {
            AyudaButton(
                texto = "Guardar cambios",
                onClick = { vm.guardar(onGuardado) }
            )
        }
    }
}


@Composable
private fun Pildora(
    texto: String,
    seleccionada: Boolean,
    color: Color,
    habilitada: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val forma = RoundedCornerShape(50)
    val fondo = when {
        seleccionada -> color
        !habilitada -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        else -> MaterialTheme.colorScheme.surfaceContainerLowest
    }
    val colorTexto = when {
        seleccionada -> Color.White
        !habilitada -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        else -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = modifier
            .clip(forma)
            .background(fondo)
            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant), forma)
            .then(if (habilitada) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = colorTexto,
            maxLines = 1
        )
    }
}