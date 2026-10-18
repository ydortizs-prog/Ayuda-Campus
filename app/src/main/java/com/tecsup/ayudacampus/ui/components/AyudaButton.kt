package com.tecsup.ayudacampus.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tecsup.ayudacampus.ui.theme.PrioridadAlta

enum class EstiloBoton { RELLENO, CONTORNO, PELIGRO }

@Composable
fun AyudaButton(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    estilo: EstiloBoton = EstiloBoton.RELLENO
) {
    val forma = RoundedCornerShape(14.dp)
    val medidas = modifier
        .fillMaxWidth()
        .height(52.dp)

    when (estilo) {
        EstiloBoton.RELLENO -> Button(
            onClick = onClick,
            modifier = medidas,
            enabled = enabled,
            shape = forma
        ) {
            Text(texto, fontWeight = FontWeight.SemiBold)
        }

        EstiloBoton.CONTORNO -> OutlinedButton(
            onClick = onClick,
            modifier = medidas,
            enabled = enabled,
            shape = forma,
            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
        ) {
            Text(texto, fontWeight = FontWeight.SemiBold)
        }

        EstiloBoton.PELIGRO -> OutlinedButton(
            onClick = onClick,
            modifier = medidas,
            enabled = enabled,
            shape = forma,
            border = BorderStroke(1.5.dp, PrioridadAlta),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = PrioridadAlta)
        ) {
            Text(texto, fontWeight = FontWeight.SemiBold)
        }
    }
}