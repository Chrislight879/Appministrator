package com.vanegasazucena.appministrator.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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

enum class SearchEncomiendaCriteria(val label: String) {
    REMITENTE("Remitente"),
    DESTINATARIO("Destinatario"),
    CODIGO("Codigo de generacion"),
    CREADOR("Usuario creador")
}

@Composable
fun SearchEncomiendaDialog(
    onSearch: (query: String, criteria: SearchEncomiendaCriteria) -> Unit,
    onDismiss: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    var selectedCriteria by remember { mutableStateOf(SearchEncomiendaCriteria.REMITENTE) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Buscar:",
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

                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TextMaroon,
                        unfocusedBorderColor = Color.Gray
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Buscar Por:",
                    color = TextMaroon,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                SearchEncomiendaCriteria.values().forEach { criteria ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedCriteria = criteria }
                            .padding(vertical = 2.dp)
                    ) {
                        RadioButton(
                            selected = (selectedCriteria == criteria),
                            onClick = { selectedCriteria = criteria },
                            colors = RadioButtonDefaults.colors(selectedColor = TextMaroon)
                        )
                        Text(
                            text = criteria.label,
                            color = TextMaroon,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = { onSearch(query, selectedCriteria) },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WinePrimary,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Buscar >", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

enum class SearchUserCriteria(val label: String) {
    ADMINISTRADORES("Administradores"),
    COMUNES("Comunes")
}

@Composable
fun SearchUserDialog(
    onSearch: (query: String, criteria: SearchUserCriteria?) -> Unit,
    onDismiss: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    var selectedCriteria by remember { mutableStateOf<SearchUserCriteria?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Buscar:",
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

                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TextMaroon,
                        unfocusedBorderColor = Color.Gray
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Buscar en:",
                    color = TextMaroon,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                SearchUserCriteria.values().forEach { criteria ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedCriteria = criteria }
                            .padding(vertical = 2.dp)
                    ) {
                        RadioButton(
                            selected = (selectedCriteria == criteria),
                            onClick = { selectedCriteria = criteria },
                            colors = RadioButtonDefaults.colors(selectedColor = TextMaroon)
                        )
                        Text(
                            text = criteria.label,
                            color = TextMaroon,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = { onSearch(query, selectedCriteria) },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WinePrimary,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Buscar >", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
