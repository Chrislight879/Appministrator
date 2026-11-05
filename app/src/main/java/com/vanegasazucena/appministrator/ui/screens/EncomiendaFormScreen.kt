package com.vanegasazucena.appministrator.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vanegasazucena.appministrator.data.local.EncomiendaEntity
import com.vanegasazucena.appministrator.ui.components.ConfirmActionDialog
import com.vanegasazucena.appministrator.ui.theme.TextMaroon
import com.vanegasazucena.appministrator.ui.theme.WinePrimary
import com.vanegasazucena.appministrator.ui.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EncomiendaFormScreen(
    viewModel: AppViewModel,
    existingEncomienda: EncomiendaEntity? = null,
    onNavigateBack: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val currentDateStr = remember {
        SimpleDateFormat("dd 'de' MMMM yyyy", Locale("es", "ES")).format(Date())
    }

    var emisorNombre by remember { mutableStateOf(existingEncomienda?.emisorNombre ?: "") }
    var emisorDui by remember { mutableStateOf(existingEncomienda?.emisorDui ?: "") }

    var remitenteNombre by remember { mutableStateOf(existingEncomienda?.remitenteNombre ?: "") }
    var remitenteDui by remember { mutableStateOf(existingEncomienda?.remitenteDui ?: "") }

    var creadoPor by remember { mutableStateOf(existingEncomienda?.creadoPor ?: (currentUser?.username ?: "PeterParker")) }
    var codigo by remember { mutableStateOf(existingEncomienda?.codigo ?: "ENC-00125") }

    var estado by remember { mutableStateOf(existingEncomienda?.estado ?: "PAGADO") }
    var tamano by remember { mutableStateOf(existingEncomienda?.tamano ?: "GRANDE") }

    val calculatedPrice = when (tamano) {
        "LIVIANO" -> 3.00
        "MEDIANO" -> 5.00
        else -> 8.00
    }

    var showConfirmDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        item {
            // Header Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = existingEncomienda?.fecha ?: currentDateStr,
                    color = TextMaroon,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Card 1: Información del Emisor
            FormSectionTitle("Informacion del Emisor")
            Spacer(modifier = Modifier.height(6.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    FormLabelInput(
                        label = "Nombre:",
                        value = emisorNombre,
                        onValueChange = { emisorNombre = it }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    FormLabelInput(
                        label = "DUI:",
                        value = emisorDui,
                        onValueChange = { emisorDui = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card 2: Información del Remitente
            FormSectionTitle("Informacion del Remitente")
            Spacer(modifier = Modifier.height(6.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    FormLabelInput(
                        label = "Nombre:",
                        value = remitenteNombre,
                        onValueChange = { remitenteNombre = it }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    FormLabelInput(
                        label = "DUI:",
                        value = remitenteDui,
                        onValueChange = { remitenteDui = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card 3: Información del Paquete
            FormSectionTitle("Informacion del Paquete")
            Spacer(modifier = Modifier.height(6.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    FormLabelInput(
                        label = "Creado por:",
                        value = creadoPor,
                        onValueChange = { creadoPor = it }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    FormLabelInput(
                        label = "Codigo:",
                        value = codigo,
                        onValueChange = { codigo = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Radio Groups Row: Estado & Tamaño
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Estado Box
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Estado de Encomienda",
                                color = TextMaroon,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                modifier = Modifier.border(1.dp, Color.Gray, RoundedCornerShape(16.dp))
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    RadioOption("Pagado", estado == "PAGADO") { estado = "PAGADO" }
                                    RadioOption("Pendiente", estado == "PENDIENTE") { estado = "PENDIENTE" }
                                    RadioOption("Entregado", estado == "ENTREGADO") { estado = "ENTREGADO" }
                                }
                            }
                        }

                        // Tamaño Box
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Tamaño de Encomienda",
                                color = TextMaroon,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                modifier = Modifier.border(1.dp, Color.Gray, RoundedCornerShape(16.dp))
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    RadioOption("Liviano", tamano == "LIVIANO") { tamano = "LIVIANO" }
                                    RadioOption("Mediano", tamano == "MEDIANO") { tamano = "MEDIANO" }
                                    RadioOption("Grande", tamano == "GRANDE") { tamano = "GRANDE" }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Total a pagar Big Display
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Total a pagar: $${String.format("%.2f", calculatedPrice)}",
                            color = TextMaroon,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = onNavigateBack,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Cancelar", color = Color.White)
                }

                Button(
                    onClick = { showConfirmDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = WinePrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Aceptar >", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showConfirmDialog) {
        ConfirmActionDialog(
            title = "¿Esta seguro que desea aplicar los cambios?",
            confirmText = "Aplicar >",
            onConfirm = {
                showConfirmDialog = false
                viewModel.saveEncomienda(
                    id = existingEncomienda?.id,
                    emisorNombre = emisorNombre,
                    emisorDui = emisorDui,
                    remitenteNombre = remitenteNombre,
                    remitenteDui = remitenteDui,
                    estado = estado,
                    tamano = tamano,
                    precio = calculatedPrice,
                    fecha = existingEncomienda?.fecha ?: currentDateStr,
                    onComplete = onNavigateBack
                )
            },
            onDismiss = { showConfirmDialog = false }
        )
    }
}

@Composable
private fun FormSectionTitle(title: String) {
    Text(
        text = title,
        color = TextMaroon,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun FormLabelInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            color = TextMaroon,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(70.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Gray
            ),
            singleLine = true
        )
    }
}

@Composable
private fun RadioOption(
    label: String,
    selected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .padding(vertical = 2.dp)
    ) {
        RadioButton(
            selected = selected,
            onClick = onSelect,
            colors = RadioButtonDefaults.colors(selectedColor = TextMaroon)
        )
        Text(
            text = label,
            color = TextMaroon,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
