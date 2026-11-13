package com.vanegasazucena.appministrator.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.vanegasazucena.appministrator.data.local.UserEntity
import com.vanegasazucena.appministrator.ui.components.AppBottomBar
import com.vanegasazucena.appministrator.ui.components.BottomTab
import com.vanegasazucena.appministrator.ui.components.ConfirmActionDialog
import com.vanegasazucena.appministrator.ui.components.FilterDialog
import com.vanegasazucena.appministrator.ui.components.SearchEncomiendaDialog
import com.vanegasazucena.appministrator.ui.components.SearchUserDialog
import com.vanegasazucena.appministrator.ui.screens.AuditLogScreen
import com.vanegasazucena.appministrator.ui.screens.DashboardScreen
import com.vanegasazucena.appministrator.ui.screens.EncomiendaFormScreen
import com.vanegasazucena.appministrator.ui.screens.EncomiendaListScreen
import com.vanegasazucena.appministrator.ui.screens.LoginScreen
import com.vanegasazucena.appministrator.ui.screens.UserFormScreen
import com.vanegasazucena.appministrator.ui.screens.UserListScreen
import com.vanegasazucena.appministrator.ui.theme.BackgroundGray
import com.vanegasazucena.appministrator.ui.theme.WinePrimary
import com.vanegasazucena.appministrator.ui.viewmodel.AppViewModel

enum class ScreenState {
    INICIO, ENCOMIENDAS_LISTA, CREAR_ENCOMIENDA, EDITAR_ENCOMIENDA, CREAR_USUARIO, EDITAR_USUARIO, USUARIOS_LISTA, LOGS_AUDITORIA
}

@Composable
fun MainScreen(viewModel: AppViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val isCheckingSession by viewModel.isCheckingSession.collectAsState()

    when {
        isCheckingSession -> {
            // Pantalla de carga mientras se verifica la sesión guardada
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundGray),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = WinePrimary)
            }
        }
        currentUser == null -> {
            LoginScreen(viewModel = viewModel) { }
        }
        else -> {
            MainContent(
                viewModel = viewModel,
                currentUser = currentUser!!
            )
        }
    }
}

@Composable
private fun MainContent(
    viewModel: AppViewModel,
    currentUser: UserEntity
) {
    val isAdmin = currentUser.role.equals("ADMIN", ignoreCase = true)

    var currentScreen by remember { mutableStateOf(ScreenState.INICIO) }
    var selectedTab by remember { mutableStateOf(BottomTab.INICIO) }

    var selectedEncomienda by remember { mutableStateOf<EncomiendaEntity?>(null) }
    var selectedUser by remember { mutableStateOf<UserEntity?>(null) }

    var showFilterDialog by remember { mutableStateOf(value = false) }
    var showSearchEncomiendaDialog by remember { mutableStateOf(value = false) }
    var showSearchUserDialog by remember { mutableStateOf(value = false) }
    var showExitDialog by remember { mutableStateOf(value = false) }

    var adminTabSelection by remember { mutableIntStateOf(0) } // 0 = Dashboard/List, 1 = Usuarios, 2 = Auditoría

    Scaffold(
        bottomBar = {
            AppBottomBar(
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    selectedTab = tab
                    when (tab) {
                        BottomTab.FILTRO -> showFilterDialog = true
                        BottomTab.BUSCAR -> {
                            if (isAdmin && (currentScreen == ScreenState.USUARIOS_LISTA)) {
                                showSearchUserDialog = true
                            } else {
                                showSearchEncomiendaDialog = true
                            }
                        }
                        BottomTab.INICIO -> {
                            currentScreen = ScreenState.INICIO
                        }
                        BottomTab.NUEVO -> {
                            if (isAdmin) {
                                selectedUser = null
                                currentScreen = ScreenState.CREAR_USUARIO
                            } else {
                                selectedEncomienda = null
                                currentScreen = ScreenState.CREAR_ENCOMIENDA
                            }
                        }
                        BottomTab.SALIR -> showExitDialog = true
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BackgroundGray)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Navigation Bar for ADMIN or COMMON toggle if in INICIO
                if (currentScreen == ScreenState.INICIO) {
                    TabRow(
                        selectedTabIndex = if (isAdmin) adminTabSelection else 0,
                        containerColor = Color.White,
                        contentColor = WinePrimary,
                        indicator = { tabPositions ->
                            if (tabPositions.isNotEmpty()) {
                                TabRowDefaults.SecondaryIndicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[if (isAdmin) adminTabSelection else 0]),
                                    color = WinePrimary
                                )
                            }
                        }
                    ) {
                        Tab(
                            selected = (if (isAdmin) adminTabSelection else 0) == 0,
                            onClick = { adminTabSelection = 0 },
                            text = {
                                Text(
                                    text = "Dashboard",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        )
                        Tab(
                            selected = (if (isAdmin) adminTabSelection else 0) == 1,
                            onClick = { adminTabSelection = 1 },
                            text = {
                                Text(
                                    text = if (isAdmin) "Usuarios" else "Encomiendas",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        )
                        if (isAdmin) {
                            Tab(
                                selected = adminTabSelection == 2,
                                onClick = { adminTabSelection = 2 },
                                text = {
                                    Text(
                                        text = "Auditoría / Logs",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            )
                        }
                    }
                }

                // Main body view selection
                when (currentScreen) {
                    ScreenState.INICIO -> {
                        if (isAdmin && adminTabSelection == 1) {
                            UserListScreen(
                                viewModel = viewModel,
                                onUserClick = { userItem ->
                                    selectedUser = userItem
                                    currentScreen = ScreenState.EDITAR_USUARIO
                                }
                            )
                        } else if (isAdmin && adminTabSelection == 2) {
                            AuditLogScreen(viewModel = viewModel)
                        } else if (!isAdmin && adminTabSelection == 1) {
                            EncomiendaListScreen(
                                viewModel = viewModel,
                                onEncomiendaClick = { encomienda ->
                                    selectedEncomienda = encomienda
                                    currentScreen = ScreenState.EDITAR_ENCOMIENDA
                                }
                            )
                        } else {
                            DashboardScreen(
                                viewModel = viewModel,
                                onEncomiendaClick = { encomienda ->
                                    selectedEncomienda = encomienda
                                    currentScreen = ScreenState.EDITAR_ENCOMIENDA
                                }
                            )
                        }
                    }

                    ScreenState.ENCOMIENDAS_LISTA -> {
                        EncomiendaListScreen(
                            viewModel = viewModel,
                            onEncomiendaClick = { encomienda ->
                                selectedEncomienda = encomienda
                                currentScreen = ScreenState.EDITAR_ENCOMIENDA
                            }
                        )
                    }

                    ScreenState.CREAR_ENCOMIENDA -> {
                        EncomiendaFormScreen(
                            viewModel = viewModel,
                            existingEncomienda = null,
                            onNavigateBack = {
                                currentScreen = ScreenState.INICIO
                                selectedTab = BottomTab.INICIO
                            }
                        )
                    }

                    ScreenState.EDITAR_ENCOMIENDA -> {
                        EncomiendaFormScreen(
                            viewModel = viewModel,
                            existingEncomienda = selectedEncomienda,
                            onNavigateBack = {
                                currentScreen = ScreenState.INICIO
                                selectedTab = BottomTab.INICIO
                            }
                        )
                    }

                    ScreenState.USUARIOS_LISTA -> {
                        UserListScreen(
                            viewModel = viewModel,
                            onUserClick = { userItem ->
                                selectedUser = userItem
                                currentScreen = ScreenState.EDITAR_USUARIO
                            }
                        )
                    }

                    ScreenState.CREAR_USUARIO -> {
                        UserFormScreen(
                            viewModel = viewModel,
                            existingUser = null,
                            onNavigateBack = {
                                currentScreen = ScreenState.INICIO
                                selectedTab = BottomTab.INICIO
                            }
                        )
                    }

                    ScreenState.EDITAR_USUARIO -> {
                        UserFormScreen(
                            viewModel = viewModel,
                            existingUser = selectedUser,
                            onNavigateBack = {
                                currentScreen = ScreenState.INICIO
                                selectedTab = BottomTab.INICIO
                            }
                        )
                    }

                    ScreenState.LOGS_AUDITORIA -> {
                        AuditLogScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }

    // Dialogs Management
    if (showFilterDialog) {
        FilterDialog(
            currentUsername = currentUser.username,
            onApply = { filterState ->
                viewModel.setFilter(filterState)
                showFilterDialog = false
                selectedTab = BottomTab.INICIO
                currentScreen = ScreenState.ENCOMIENDAS_LISTA
            },
            onDismiss = {
                showFilterDialog = false
                selectedTab = BottomTab.INICIO
            }
        )
    }

    if (showSearchEncomiendaDialog) {
        SearchEncomiendaDialog(
            onSearch = { query, criteria ->
                viewModel.setSearchEncomiendas(query, criteria)
                showSearchEncomiendaDialog = false
                selectedTab = BottomTab.INICIO
                currentScreen = ScreenState.ENCOMIENDAS_LISTA
            },
            onDismiss = {
                showSearchEncomiendaDialog = false
                selectedTab = BottomTab.INICIO
            }
        )
    }

    if (showSearchUserDialog) {
        SearchUserDialog(
            onSearch = { query, criteria ->
                viewModel.setSearchUsers(query, criteria)
                showSearchUserDialog = false
                selectedTab = BottomTab.INICIO
                currentScreen = ScreenState.USUARIOS_LISTA
            },
            onDismiss = {
                showSearchUserDialog = false
                selectedTab = BottomTab.INICIO
            }
        )
    }

    if (showExitDialog) {
        ConfirmActionDialog(
            title = "¿Esta seguro que desea salir de la aplicacion?",
            confirmText = "Salir >",
            onConfirm = {
                showExitDialog = false
                viewModel.logout()
            },
            onDismiss = {
                showExitDialog = false
                selectedTab = BottomTab.INICIO
            }
        )
    }
}
