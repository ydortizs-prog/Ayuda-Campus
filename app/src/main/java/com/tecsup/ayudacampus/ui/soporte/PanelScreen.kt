package com.tecsup.ayudacampus.ui.soporte

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.ayudacampus.ui.components.BarraSoporte
import com.tecsup.ayudacampus.ui.components.graficos.GraficoBarras
import com.tecsup.ayudacampus.ui.components.graficos.GraficoDona
import com.tecsup.ayudacampus.ui.theme.EstadoAbierto
import com.tecsup.ayudacampus.ui.theme.EstadoEnProceso
import com.tecsup.ayudacampus.ui.theme.EstadoResuelto
import com.tecsup.ayudacampus.ui.theme.PrioridadAlta
import com.tecsup.ayudacampus.viewmodel.PanelViewModel

@Composable
fun PanelScreen(
    vm: PanelViewModel,
    onVerAltas: () -> Unit,
    onTickets: () -> Unit,
    onCategorias: () -> Unit,
    onAjustes: () -> Unit
) {
    val abiertos by vm.abiertos.collectAsState()
    val enProceso by vm.enProceso.collectAsState()
    val resueltos by vm.resueltos.collectAsState()
    val porEstado by vm.porEstado.collectAsState()
    val porCategoria by vm.porCategoria.collectAsState()
    val altas by vm.altasSinAtender.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            BarraSoporte(
                seleccion = 0,
                onPanel = { },
                onTickets = onTickets,
                onCategorias = onCategorias,
                onAjustes = onAjustes
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Panel de soporte", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "Mesa de ayuda TI · Hoy",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(Icons.Filled.Notifications, contentDescription = "Notificaciones")
            }


            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Contador("Abiertos", abiertos, EstadoAbierto, Modifier.weight(1f))
                Contador("En proceso", enProceso, EstadoEnProceso, Modifier.weight(1f))
                Contador("Resueltos", resueltos, EstadoResuelto, Modifier.weight(1f))
            }


            Text("Tickets por estado", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            TarjetaBlanca {
                GraficoDona(datos = porEstado)
            }


            Text("Tickets por categoría", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            TarjetaBlanca {
                GraficoBarras(datos = porCategoria)
            }


            if (altas > 0) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = PrioridadAlta.copy(alpha = 0.12f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Filled.Warning, contentDescription = null, tint = PrioridadAlta)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (altas == 1) "1 ticket de prioridad Alta"
                                else "$altas tickets de prioridad Alta",
                                fontWeight = FontWeight.Bold,
                                color = PrioridadAlta
                            )
                            Text("sin atender hace más de 1 hora", fontSize = 12.sp)
                        }
                        Button(
                            onClick = onVerAltas,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrioridadAlta,
                                contentColor = Color.White
                            )
                        ) {
                            Text("Ver")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Contador(etiqueta: String, valor: Int, color: Color, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.18f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("$valor", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = color)
            Text(etiqueta, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = color)
        }
    }
}

@Composable
private fun TarjetaBlanca(contenido: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            contenido()
        }
    }
}