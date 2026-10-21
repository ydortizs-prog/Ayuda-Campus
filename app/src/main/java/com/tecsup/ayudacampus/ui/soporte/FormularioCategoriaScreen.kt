package com.tecsup.ayudacampus.ui.soporte

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.ayudacampus.data.local.entity.Categoria
import com.tecsup.ayudacampus.ui.components.AppTopBar
import com.tecsup.ayudacampus.ui.components.AyudaButton
import com.tecsup.ayudacampus.ui.components.AyudaTextField
import com.tecsup.ayudacampus.ui.components.EstiloBoton
import com.tecsup.ayudacampus.ui.components.iconoCategoria
import com.tecsup.ayudacampus.viewmodel.CategoriasViewModel

private val ICONOS = listOf("pc", "code", "wifi", "key", "printer", "tag")

@Composable
fun FormularioCategoriaScreen(
    vm: CategoriasViewModel,
    categoriaId: Int?,
    onVolver: () -> Unit
) {
    val categorias by vm.categorias.collectAsState()
    val mensaje by vm.mensaje.collectAsState()
    val existente = categoriaId?.let { id -> categorias.find { it.id == id } }

    var nombre by remember { mutableStateOf("") }
    var icono by remember { mutableStateOf("tag") }
    var activa by remember { mutableStateOf(true) }
    var errorNombre by remember { mutableStateOf<String?>(null) }


    LaunchedEffect(existente?.id) {
        existente?.let {
            nombre = it.nombre
            icono = it.icono
            activa = it.activa
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppTopBar(
                titulo = if (categoriaId == null) "Nueva categoría" else "Editar categoría",
                onAtras = onVolver
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            AyudaTextField(
                valor = nombre,
                onValorChange = {
                    nombre = it
                    errorNombre = null
                },
                etiqueta = "Nombre de la categoría",
                error = errorNombre
            )


            Text("Ícono", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            ICONOS.chunked(3).forEach { fila ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    fila.forEach { clave ->
                        val seleccionado = clave == icono
                        val forma = RoundedCornerShape(16.dp)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(64.dp)
                                .clip(forma)
                                .background(
                                    if (seleccionado) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surfaceContainerLowest
                                )
                                .border(
                                    BorderStroke(
                                        if (seleccionado) 2.dp else 1.dp,
                                        if (seleccionado) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.outlineVariant
                                    ),
                                    forma
                                )
                                .clickable { icono = clave },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = iconoCategoria(clave),
                                contentDescription = clave,
                                tint = if (seleccionado) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }


            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Categoría activa", fontWeight = FontWeight.Bold)
                        Text(
                            "Los estudiantes podrán elegirla",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(checked = activa, onCheckedChange = { activa = it })
                }
            }


            Text("Vista previa", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                )
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                MaterialTheme.colorScheme.primaryContainer,
                                RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = iconoCategoria(icono),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Column {
                        Text(
                            text = nombre.ifBlank { "Nombre de la categoría" },
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (activa) "Activa" else "Inactiva",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }


            AyudaButton(
                texto = "Guardar categoría",
                onClick = {
                    if (nombre.isBlank()) {
                        errorNombre = "El nombre es obligatorio"
                    } else {
                        vm.guardar(
                            Categoria(
                                id = existente?.id ?: 0,
                                nombre = nombre.trim(),
                                icono = icono,
                                activa = activa
                            ),
                            onVolver
                        )
                    }
                }
            )
            AyudaButton(
                texto = "Cancelar",
                onClick = onVolver,
                estilo = EstiloBoton.CONTORNO
            )


            if (existente != null) {
                AyudaButton(
                    texto = "Eliminar categoría",
                    onClick = {
                        vm.limpiarMensaje()
                        vm.eliminar(existente, onVolver)
                    },
                    estilo = EstiloBoton.PELIGRO
                )
                if (mensaje != null) {
                    Text(
                        text = mensaje ?: "",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
