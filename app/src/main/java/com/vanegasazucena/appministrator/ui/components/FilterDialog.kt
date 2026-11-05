package com.vanegasazucena.appministrator.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.vanegasazucena.appministrator.ui.theme.TextMaroon
import com.vanegasazucena.appministrator.ui.theme.WinePrimary

data class FilterState(
    val fechaDesde: String = "20/10/2026",
    val fechaHasta: String = "20/10/2026",
    val pendientes: Boolean = false,
    val entregadas: Boolean = false,
    val pagadas: Boolean = false,
    val creadasPorUser: Boolean = false
)

@Composable
fun FilterDialog(
    initialState: FilterState = FilterState(),
    currentUsername: String = "PeterParker",
    onApply: (FilterState) -> Unit,
    onDismiss: () -> Unit
) {
    var state by remember { mutableStateOf(initialState) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Por Rango de Fechas:",
                        color = TextMaroon,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = TextMaroon
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Desde", color = TextMaroon, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(6.dp))
                    OutlinedTextField(
                        value = state.fechaDesde,
                        onValueChange = { state = state.copy(fechaDesde = it) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TextMaroon,
                            unfocusedBorderColor = Color.Gray
                        ),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Hasta", color = TextMaroon, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(6.dp))
                    OutlinedTextField(
                        value = state.fechaHasta,
                        onValueChange = { state = state.copy(fechaHasta = it) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TextMaroon,
                            unfocusedBorderColor = Color.Gray
                        ),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Por Etiquetas:",
                    color = TextMaroon,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                FilterCheckboxRow(
                    label = "Pendientes de Pago",
                    checked = state.pendientes,
                    onCheckedChange = { state = state.copy(pendientes = it) }
                )
                FilterCheckboxRow(
                    label = "Entregadas",
                    checked = state.entregadas,
                    onCheckedChange = { state = state.copy(entregadas = it) }
                )
                FilterCheckboxRow(
                    label = "Pagadas",
                    checked = state.pagadas,
                    onCheckedChange = { state = state.copy(pagadas = it) }
                )
                FilterCheckboxRow(
                    label = "Creadas por $currentUsername",
                    checked = state.creadasPorUser,
                    onCheckedChange = { state = state.copy(creadasPorUser = it) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = { onApply(state) },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WinePrimary,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Aplicar >", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterCheckboxRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = TextMaroon)
        )
        Text(
            text = label,
            color = TextMaroon,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
