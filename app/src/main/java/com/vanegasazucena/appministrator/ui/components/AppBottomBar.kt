package com.vanegasazucena.appministrator.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vanegasazucena.appministrator.ui.theme.TextMuted
import com.vanegasazucena.appministrator.ui.theme.WinePrimary

enum class BottomTab {
    FILTRO, BUSCAR, INICIO, NUEVO, SALIR
}

@Composable
fun AppBottomBar(
    selectedTab: BottomTab = BottomTab.INICIO,
    onTabSelected: (BottomTab) -> Unit
) {
    Surface(
        shadowElevation = 8.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                icon = Icons.Default.Tune,
                label = "Filtro",
                isSelected = selectedTab == BottomTab.FILTRO,
                onClick = { onTabSelected(BottomTab.FILTRO) }
            )
            BottomNavItem(
                icon = Icons.Default.Search,
                label = "Buscar",
                isSelected = selectedTab == BottomTab.BUSCAR,
                onClick = { onTabSelected(BottomTab.BUSCAR) }
            )
            BottomNavItem(
                icon = Icons.Default.Home,
                label = "Inicio",
                isSelected = selectedTab == BottomTab.INICIO,
                onClick = { onTabSelected(BottomTab.INICIO) }
            )
            BottomNavItem(
                icon = Icons.Default.AddCircleOutline,
                label = "Nuevo",
                isSelected = selectedTab == BottomTab.NUEVO,
                onClick = { onTabSelected(BottomTab.NUEVO) }
            )
            BottomNavItem(
                icon = Icons.AutoMirrored.Filled.ExitToApp,
                label = "Salir",
                isSelected = selectedTab == BottomTab.SALIR,
                onClick = { onTabSelected(BottomTab.SALIR) }
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val color = if (isSelected) WinePrimary else TextMuted

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            color = color,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
