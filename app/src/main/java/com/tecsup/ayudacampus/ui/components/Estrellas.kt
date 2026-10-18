package com.tecsup.ayudacampus.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tecsup.ayudacampus.ui.theme.EstadoAbierto

@Composable
fun Estrellas(
    valor: Int,
    modifier: Modifier = Modifier,
    onCambio: ((Int) -> Unit)? = null,
    tamano: Dp = 40.dp
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (i in 1..5) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "$i estrellas",
                tint = if (i <= valor) EstadoAbierto else MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier
                    .size(tamano)
                    .then(
                        if (onCambio != null) Modifier.clickable { onCambio(i) } else Modifier
                    )
            )
        }
    }
}

