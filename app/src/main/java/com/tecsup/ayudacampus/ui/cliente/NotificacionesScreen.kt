
package com.tecsup.ayudacampus.ui.cliente

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tecsup.ayudacampus.viewmodel.NotificacionesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificacionesScreen(
    vm: NotificacionesViewModel,
    onVolver: () -> Unit,
    onTicket: (Int) -> Unit
) {
    val notificaciones by vm.notificaciones.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notificaciones") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { padding ->

        if (notificaciones.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No tienes notificaciones todavía")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    notificaciones,
                    key = { it.id }
                ) { notificacion ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                vm.marcarLeida(notificacion.id)
                                onTicket(notificacion.ticketId)
                            },
                        colors = CardDefaults.cardColors(
                            containerColor =
                                if (!notificacion.leida)
                                    MaterialTheme.colorScheme.primaryContainer
                                else
                                    MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                Icons.Filled.Notifications,
                                contentDescription = null
                            )

                            Column {
                                Text(
                                    text = notificacion.titulo,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Spacer(Modifier.height(4.dp))

                                Text(
                                    text = notificacion.mensaje,
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                if (!notificacion.leida) {
                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        text = "Nueva",
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
