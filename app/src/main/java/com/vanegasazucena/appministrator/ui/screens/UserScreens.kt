package com.vanegasazucena.appministrator.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vanegasazucena.appministrator.data.local.UserEntity
import com.vanegasazucena.appministrator.ui.components.ConfirmActionDialog
import com.vanegasazucena.appministrator.ui.components.StatusBadge
import com.vanegasazucena.appministrator.ui.theme.TextMaroon
import com.vanegasazucena.appministrator.ui.theme.WinePrimary
import com.vanegasazucena.appministrator.ui.viewmodel.AppViewModel

@Composable
fun UserListScreen(
    viewModel: AppViewModel,
    onUserClick: (UserEntity) -> Unit
) {
    val users by viewModel.filteredUsers.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Lista de usuarios",
                color = TextMaroon,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        items(users) { user ->
            UserCard(
                user = user,
                onClick = { onUserClick(user) }
            )
        }
    }
}

@Composable
fun UserCard(
    user: UserEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = user.username,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black
            )
            val badgeLabel = if (user.role.equals("ADMIN", ignoreCase = true)) "Administrador" else "Común"
            StatusBadge(status = badgeLabel)
        }
    }
}

@Composable
fun UserFormScreen(
    viewModel: AppViewModel,
    existingUser: UserEntity? = null,
    onNavigateBack: () -> Unit
) {
    var username by remember { mutableStateOf(existingUser?.username ?: "") }
    var password by remember { mutableStateOf(existingUser?.password ?: "") }
    var role by remember { mutableStateOf(existingUser?.role ?: "COMMON") }

    var showDeleteConfirm by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Crear Usuario",
            color = TextMaroon,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Username:",
                        color = TextMaroon,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(80.dp)
                    )
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        enabled = existingUser == null,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = Color.Gray
                        ),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Password:",
                        color = TextMaroon,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(80.dp)
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = Color.Gray
                        ),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Tipo de Usuario:",
                    color = TextMaroon,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { role = "ADMIN" }
                            ) {
                                RadioButton(
                                    selected = (role == "ADMIN"),
                                    onClick = { role = "ADMIN" },
                                    colors = RadioButtonDefaults.colors(selectedColor = TextMaroon)
                                )
                                Text("Administrador", color = TextMaroon, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { role = "COMMON" }
                            ) {
                                RadioButton(
                                    selected = (role == "COMMON"),
                                    onClick = { role = "COMMON" },
                                    colors = RadioButtonDefaults.colors(selectedColor = TextMaroon)
                                )
                                Text("Comun", color = TextMaroon, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onNavigateBack,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray, contentColor = Color.Black),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.width(110.dp)
                        ) {
                            Text("Cancelar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        if (existingUser != null) {
                            Button(
                                onClick = { showDeleteConfirm = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC3545), contentColor = Color.White),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.width(110.dp)
                            ) {
                                Text("Eliminar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Button(
                            onClick = {
                                if (username.isNotBlank() && password.isNotBlank()) {
                                    viewModel.saveUser(username.trim(), password, role, onNavigateBack)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = WinePrimary, contentColor = Color.White),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.width(110.dp)
                        ) {
                            Text("Aceptar >", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (showDeleteConfirm && existingUser != null) {
        ConfirmActionDialog(
            title = "¿Esta seguro que desea eliminar a ${existingUser.username}?",
            confirmText = "Eliminar",
            confirmButtonColor = Color(0xFFDC3545),
            onConfirm = {
                showDeleteConfirm = false
                viewModel.deleteUser(existingUser.username, onNavigateBack)
            },
            onDismiss = { showDeleteConfirm = false }
        )
    }
}
