package com.tecsup.ayudacampus.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.tecsup.ayudacampus.model.Rol

private val EsquemaCliente = lightColorScheme(
    primary = CianPrimario,
    onPrimary = Color.White,
    primaryContainer = CianContenedor,
    background = CianFondo,
    surface = CianFondo
)

private val EsquemaSoporte = lightColorScheme(
    primary = VioletaPrimario,
    onPrimary = Color.White,
    primaryContainer = VioletaContenedor,
    background = VioletaFondo,
    surface = VioletaFondo
)

@Composable
fun AyudaTheme(
    rol: Rol,
    modoOscuro: Boolean = false,
    content: @Composable () -> Unit
) {
    val colores = when {
        modoOscuro && rol == Rol.ADMIN -> darkColorScheme(primary = VioletaPrimario)
        modoOscuro -> darkColorScheme(primary = CianPrimario)
        rol == Rol.ADMIN -> EsquemaSoporte
        else -> EsquemaCliente
    }

    MaterialTheme(
        colorScheme = colores,
        typography = Typography,
        content = content
    )
}