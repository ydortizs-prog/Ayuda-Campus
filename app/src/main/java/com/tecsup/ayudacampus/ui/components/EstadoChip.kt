package com.tecsup.ayudacampus.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.ayudacampus.model.EstadoTicket
import com.tecsup.ayudacampus.ui.theme.EstadoAbierto
import com.tecsup.ayudacampus.ui.theme.EstadoCerrado
import com.tecsup.ayudacampus.ui.theme.EstadoEnProceso
import com.tecsup.ayudacampus.ui.theme.EstadoResuelto


@Composable
internal fun EtiquetaChip(texto: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(50),
        color = color.copy(alpha = 0.15f),
        modifier = modifier
    ) {
        Text(
            text = texto,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun EstadoChip(estado: EstadoTicket, modifier: Modifier = Modifier) {
    val (texto, color) = when (estado) {
        EstadoTicket.ABIERTO -> "Abierto" to EstadoAbierto
        EstadoTicket.EN_PROCESO -> "En proceso" to EstadoEnProceso
        EstadoTicket.RESUELTO -> "Resuelto" to EstadoResuelto
        EstadoTicket.CERRADO -> "Cerrado" to EstadoCerrado
    }
    EtiquetaChip(texto, color, modifier)
}