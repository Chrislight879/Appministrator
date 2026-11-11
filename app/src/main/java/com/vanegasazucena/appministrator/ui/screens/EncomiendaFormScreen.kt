package com.vanegasazucena.appministrator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

    val isDelivered = remember(existingEncomienda) {
        existingEncomienda?.estado.equals("ENTREGADO", ignoreCase = true)
    }

    var emisorNombre by remember { mutableStateOf(existingEncomienda?.emisorNombre ?: "") }
    var emisorDui by remember { mutableStateOf(existingEncomienda?.emisorDui ?: "") }

    var remitenteNombre by remember { mutableStateOf(existingEncomienda?.remitenteNombre ?: "") }
    var remitenteDui by remember { mutableStateOf(existingEncomienda?.remitenteDui ?: "") }

    var creadoPor by remember { mutableStateOf(existingEncomienda?.creadoPor ?: (currentUser?.username ?: "PeterParker")) }
    var codigo by remember { mutableStateOf(existingEncomienda?.codigo ?: "") }

    var estado by remember { mutableStateOf(existingEncomienda?.estado ?: "PAGADO") }
    var tamano by remember { mutableStateOf(existingEncomienda?.tamano ?: "GRANDE") }

    val calculatedPrice = when (tamano) {
        "LIVIANO" -> 3.00
        "MEDIANO" -> 5.00
        else -> 8.00
    }

    var showConfirmDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        item {
            // Header Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isDelivered) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Bloqueado",
                            tint = Color(0xFFDC3545),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "ENTREGADO (Edición Bloqueada)",
                            color = Color(0xFFDC3545),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                Text(
                    text = existingEncomienda?.fecha ?: currentDateStr,
                    color = TextMaroon,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (isDelivered) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF8D7DA))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "🔒 Esta encomienda fue ENTREGADA. Los datos están protegidos contra edición. Solo se permite la opción de Eliminar.",
                        color = Color(0xFF842029),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
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
                        enabled = !isDelivered,
                        onValueChange = { emisorNombre = it }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    FormLabelInput(
                        label = "DUI:",
                        value = emisorDui,
                        enabled = !isDelivered,
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
                        enabled = !isDelivered,
                        onValueChange = { remitenteNombre = it }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    FormLabelInput(
                        label = "DUI:",
                        value = remitenteDui,
                        enabled = !isDelivered,
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
                        enabled = !isDelivered,
                        onValueChange = { creadoPor = it }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    FormLabelInput(
                        label = "Codigo:",
                        value = codigo,
                        enabled = false, // Auto-generated code
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
                                    RadioOption("Pagado", estado == "PAGADO", enabled = !isDelivered) { estado = "PAGADO" }
                                    RadioOption("Pendiente", estado == "PENDIENTE", enabled = !isDelivered) { estado = "PENDIENTE" }
                                    RadioOption("Entregado", estado == "ENTREGADO", enabled = !isDelivered) { estado = "ENTREGADO" }
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
                                    RadioOption("Liviano", tamano == "LIVIANO", enabled = !isDelivered) { tamano = "LIVIANO" }
                                    RadioOption("Mediano", tamano == "MEDIANO", enabled = !isDelivered) { tamano = "MEDIANO" }
                                    RadioOption("Grande", tamano == "GRANDE", enabled = !isDelivered) { tamano = "GRANDE" }
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

                if (existingEncomienda != null) {
                    Button(
                        onClick = { showDeleteConfirmDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC3545)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Eliminar", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                if (!isDelivered) {
                    Button(
                        onClick = { showConfirmDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = WinePrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Aceptar >", color = Color.White, fontWeight = FontWeight.Bold)
                    }
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

    if (showDeleteConfirmDialog && existingEncomienda != null) {
        ConfirmActionDialog(
            title = "¿Esta seguro que desea eliminar la encomienda ${existingEncomienda.id}?",
            confirmText = "Eliminar",
            confirmButtonColor = Color(0xFFDC3545),
            onConfirm = {
                showDeleteConfirmDialog = false
                viewModel.deleteEncomienda(existingEncomienda) {
                    onNavigateBack()
                }
            },
            onDismiss = { showDeleteConfirmDialog = false }
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
    enabled: Boolean = true,
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
            enabled = enabled,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Gray,
                disabledBorderColor = Color.LightGray,
                disabledTextColor = Color.DarkGray
            ),
            singleLine = true
        )
    }
}

@Composable
private fun RadioOption(
    label: String,
    selected: Boolean,
    enabled: Boolean = true,
    onSelect: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onSelect)
            .padding(vertical = 2.dp)
    ) {
        RadioButton(
            selected = selected,
            onClick = onSelect,
            enabled = enabled,
            colors = RadioButtonDefaults.colors(selectedColor = TextMaroon)
        )
        Text(
            text = label,
            color = if (enabled) TextMaroon else Color.Gray,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
