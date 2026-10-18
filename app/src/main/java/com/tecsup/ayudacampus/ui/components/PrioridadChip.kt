package com.tecsup.ayudacampus.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tecsup.ayudacampus.model.Prioridad
import com.tecsup.ayudacampus.ui.theme.PrioridadAlta
import com.tecsup.ayudacampus.ui.theme.PrioridadBaja
import com.tecsup.ayudacampus.ui.theme.PrioridadMedia

@Composable
fun PrioridadChip(prioridad: Prioridad, modifier: Modifier = Modifier) {
    val (texto, color) = when (prioridad) {
        Prioridad.ALTA -> "Alta" to PrioridadAlta
        Prioridad.MEDIA -> "Media" to PrioridadMedia
        Prioridad.BAJA -> "Baja" to PrioridadBaja
    }
    EtiquetaChip(texto, color, modifier)
}

