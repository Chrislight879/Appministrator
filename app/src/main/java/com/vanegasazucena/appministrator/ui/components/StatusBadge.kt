package com.vanegasazucena.appministrator.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vanegasazucena.appministrator.ui.theme.StatusComunBg
import com.vanegasazucena.appministrator.ui.theme.StatusComunText
import com.vanegasazucena.appministrator.ui.theme.StatusEntregadoBg
import com.vanegasazucena.appministrator.ui.theme.StatusEntregadoText
import com.vanegasazucena.appministrator.ui.theme.StatusPagadoBg
import com.vanegasazucena.appministrator.ui.theme.StatusPagadoText
import com.vanegasazucena.appministrator.ui.theme.StatusPendienteBg
import com.vanegasazucena.appministrator.ui.theme.StatusPendienteText

@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (status.lowercase()) {
        "pagado" -> StatusPagadoBg to StatusPagadoText
        "entregado", "completado" -> StatusEntregadoBg to StatusEntregadoText
        "común", "comun" -> StatusComunBg to StatusComunText
        else -> StatusPendienteBg to StatusPendienteText
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = status,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
