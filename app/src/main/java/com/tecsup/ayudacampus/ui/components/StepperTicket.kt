package com.tecsup.ayudacampus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.ayudacampus.model.EstadoTicket
import com.tecsup.ayudacampus.ui.theme.EstadoResuelto

@Composable
fun StepperTicket(estadoActual: EstadoTicket, modifier: Modifier = Modifier) {
    val etiquetas = listOf("Abierto", "En proceso", "Resuelto", "Cerrado")
    val actual = estadoActual.ordinal   // ABIERTO=0, EN_PROCESO=1, RESUELTO=2, CERRADO=3
    val colorActivo = MaterialTheme.colorScheme.primary
    val colorGris = MaterialTheme.colorScheme.outlineVariant

    Row(modifier = modifier.fillMaxWidth()) {
        etiquetas.forEachIndexed { i, etiqueta ->
            val completado = i < actual || estadoActual == EstadoTicket.CERRADO
            val esActual = i == actual && estadoActual != EstadoTicket.CERRADO

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Línea izquierda
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(2.dp)
                            .background(
                                when {
                                    i == 0 -> Color.Transparent
                                    i <= actual -> colorActivo
                                    else -> colorGris
                                }
                            )
                    )

                    // Círculo del paso
                    when {
                        completado -> Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(EstadoResuelto),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        esActual -> Box(
                            modifier = Modifier
                                .size(22.dp)
                                .border(2.dp, colorActivo, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(colorActivo)
                            )
                        }

                        else -> Box(
                            modifier = Modifier
                                .size(22.dp)
                                .border(2.dp, colorGris, CircleShape)
                        )
                    }

                    // Línea derecha
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(2.dp)
                            .background(
                                when {
                                    i == etiquetas.lastIndex -> Color.Transparent
                                    i < actual -> colorActivo
                                    else -> colorGris
                                }
                            )
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = etiqueta,
                    fontSize = 11.sp,
                    fontWeight = if (esActual) FontWeight.Bold else FontWeight.Normal,
                    color = if (esActual) colorActivo else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
