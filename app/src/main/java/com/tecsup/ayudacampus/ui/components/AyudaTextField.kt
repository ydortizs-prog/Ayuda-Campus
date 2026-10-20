package com.tecsup.ayudacampus.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme

@Composable
fun AyudaTextField(
    valor: String,
    onValorChange: (String) -> Unit,
    etiqueta: String,
    modifier: Modifier = Modifier,
    icono: ImageVector? = null,
    error: String? = null,
    esClave: Boolean = false,
    singleLine: Boolean = true,
    minLineas: Int = 1
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorChange,
        label = { Text(etiqueta) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        singleLine = singleLine,
        minLines = minLineas,
        isError = error != null,
        leadingIcon = icono?.let {
            { Icon(it, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
        },
        visualTransformation = if (esClave) PasswordVisualTransformation() else VisualTransformation.None,
        supportingText = error?.let { { Text(it) } }
    )
}