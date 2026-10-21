package com.tecsup.ayudacampus.ui.cliente

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.ayudacampus.model.Prioridad
import com.tecsup.ayudacampus.ui.components.AppTopBar
import com.tecsup.ayudacampus.ui.components.AyudaButton
import com.tecsup.ayudacampus.ui.components.AyudaTextField
import com.tecsup.ayudacampus.ui.components.BarraCliente
import com.tecsup.ayudacampus.ui.components.iconoCategoria
import com.tecsup.ayudacampus.ui.theme.PrioridadAlta
import com.tecsup.ayudacampus.ui.theme.PrioridadBaja
import com.tecsup.ayudacampus.ui.theme.PrioridadMedia
import com.tecsup.ayudacampus.viewmodel.NuevoTicketViewModel
import androidx.compose.material.icons.filled.Warning

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NuevoTicketScreen(
    vm: NuevoTicketViewModel,
    onVolver: () -> Unit,
    onCreado: () -> Unit,
    onPerfil: () -> Unit
) {
    val categorias by vm.categorias.collectAsState()
    val form by vm.form.collectAsState()
    val errores by vm.errores.collectAsState()
    val creado by vm.creado.collectAsState()
    val mesaAbierta by vm.mesaAbierta.collectAsState()


    LaunchedEffect(creado) {
        if (creado) onCreado()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { AppTopBar(titulo = "Nuevo ticket", onAtras = onVolver) },
        bottomBar = {
            BarraCliente(
                seleccion = 1,
                onTickets = onVolver,
                onNuevo = { },
                onPerfil = onPerfil
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (!mesaAbierta) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = PrioridadAlta.copy(alpha = 0.12f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Filled.Warning, contentDescription = null, tint = PrioridadAlta)
                        Column {
                            Text("Mesa de ayuda cerrada", fontWeight = FontWeight.Bold, color = PrioridadAlta)
                            Text("Por ahora no se reciben tickets nuevos.", fontSize = 13.sp)
                        }
                    }
                }
            }
            Text("Categoría", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categorias.forEach { c ->
                    FilterChip(
                        selected = form.categoriaId == c.id,
                        onClick = { vm.elegirCategoria(c.id) },
                        label = { Text(c.nombre) },
                        leadingIcon = {
                            Icon(
                                imageVector = iconoCategoria(c.icono),
                                contentDescription = null,
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }
            if (errores.categoria != null) {
                Text(
                    text = errores.categoria ?: "",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp
                )
            }


            AyudaTextField(
                valor = form.titulo,
                onValorChange = vm::cambiarTitulo,
                etiqueta = "Título",
                error = errores.titulo
            )
            AyudaTextField(
                valor = form.ubicacion,
                onValorChange = vm::cambiarUbicacion,
                etiqueta = "Ubicación",
                icono = Icons.Outlined.LocationOn
            )

            Text("Prioridad", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OpcionPrioridad("Baja", form.prioridad == Prioridad.BAJA, PrioridadBaja,
                    Modifier.weight(1f)) { vm.elegirPrioridad(Prioridad.BAJA) }
                OpcionPrioridad("Media", form.prioridad == Prioridad.MEDIA, PrioridadMedia,
                    Modifier.weight(1f)) { vm.elegirPrioridad(Prioridad.MEDIA) }
                OpcionPrioridad("Alta", form.prioridad == Prioridad.ALTA, PrioridadAlta,
                    Modifier.weight(1f)) { vm.elegirPrioridad(Prioridad.ALTA) }
            }


            AyudaTextField(
                valor = form.descripcion,
                onValorChange = vm::cambiarDescripcion,
                etiqueta = "Descripción",
                singleLine = false,
                minLineas = 4,
                error = errores.descripcion
            )


            val estimado = when (form.prioridad) {
                Prioridad.ALTA -> "4 horas"
                Prioridad.MEDIA -> "24 horas"
                Prioridad.BAJA -> "48 horas"
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        Icons.Outlined.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text("Tiempo de respuesta estimado: $estimado", fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            AyudaButton(texto = "Enviar ticket", onClick = vm::enviar, enabled = mesaAbierta)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}


@Composable
private fun OpcionPrioridad(
    texto: String,
    seleccionada: Boolean,
    color: Color,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val forma = RoundedCornerShape(14.dp)
    Box(
        modifier = modifier
            .clip(forma)
            .background(if (seleccionada) color else MaterialTheme.colorScheme.surfaceContainerLowest)
            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant), forma)
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            fontWeight = FontWeight.SemiBold,
            color = if (seleccionada) Color.White else MaterialTheme.colorScheme.onSurface
        )
    }
}
