package com.tecsup.ayudacampus.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Sell
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable


@Composable
fun BarraSoporte(
    seleccion: Int,
    onPanel: () -> Unit,
    onTickets: () -> Unit,
    onCategorias: () -> Unit,
    onAjustes: () -> Unit
) {
    val colores = NavigationBarItemDefaults.colors(
        selectedIconColor = MaterialTheme.colorScheme.primary,
        selectedTextColor = MaterialTheme.colorScheme.primary,
        indicatorColor = MaterialTheme.colorScheme.primaryContainer
    )

    NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest) {
        NavigationBarItem(
            selected = seleccion == 0,
            onClick = onPanel,
            icon = { Icon(Icons.Outlined.BarChart, contentDescription = null) },
            label = { Text("Panel") },
            colors = colores
        )
        NavigationBarItem(
            selected = seleccion == 1,
            onClick = onTickets,
            icon = { Icon(Icons.AutoMirrored.Outlined.List, contentDescription = null) },
            label = { Text("Tickets") },
            colors = colores
        )
        NavigationBarItem(
            selected = seleccion == 2,
            onClick = onCategorias,
            icon = { Icon(Icons.Outlined.Sell, contentDescription = null) },
            label = { Text("Categorías") },
            colors = colores
        )
        NavigationBarItem(
            selected = seleccion == 3,
            onClick = onAjustes,
            icon = { Icon(Icons.Outlined.Settings, contentDescription = null) },
            label = { Text("Ajustes") },
            colors = colores
        )
    }
}