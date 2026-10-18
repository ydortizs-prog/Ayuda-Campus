package com.tecsup.ayudacampus.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.ayudacampus.ui.theme.VioletaContenedor

@Composable
fun BurbujaComentario(
    autor: String,
    hora: String,
    texto: String,
    propio: Boolean,
    autorEsSoporte: Boolean,
    modifier: Modifier = Modifier
) {
    val colorFondo = when {
        propio -> MaterialTheme.colorScheme.primary
        autorEsSoporte -> VioletaContenedor
        else -> MaterialTheme.colorScheme.surfaceContainerLowest
    }
    val colorTexto = if (propio) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (propio) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = colorFondo,
            contentColor = colorTexto,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                Text(text = "$autor · $hora", fontSize = 11.sp)
                Text(text = texto)
            }
        }
    }
}

