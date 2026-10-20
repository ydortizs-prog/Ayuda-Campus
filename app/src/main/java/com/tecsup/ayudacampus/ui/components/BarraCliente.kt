package com.tecsup.ayudacampus.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable


@Composable
fun BarraCliente(
    seleccion: Int,
    onTickets: () -> Unit,
    onNuevo: () -> Unit,
    onPerfil: () -> Unit
) {
    val colores = NavigationBarItemDefaults.colors(
        selectedIconColor = MaterialTheme.colorScheme.primary,
        selectedTextColor = MaterialTheme.colorScheme.primary,
        indicatorColor = MaterialTheme.colorScheme.primaryContainer
    )

    NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest) {
        NavigationBarItem(
            selected = seleccion == 0,
            onClick = onTickets,
            icon = { Icon(Icons.AutoMirrored.Outlined.List, contentDescription = null) },
            label = { Text("Tickets") },
            colors = colores
        )
        NavigationBarItem(
            selected = seleccion == 1,
            onClick = onNuevo,
            icon = { Icon(Icons.Outlined.Add, contentDescription = null) },
            label = { Text("Nuevo") },
            colors = colores
        )
        NavigationBarItem(
            selected = seleccion == 2,
            onClick = onPerfil,
            icon = { Icon(Icons.Outlined.Person, contentDescription = null) },
            label = { Text("Perfil") },
            colors = colores
        )
    }
}

