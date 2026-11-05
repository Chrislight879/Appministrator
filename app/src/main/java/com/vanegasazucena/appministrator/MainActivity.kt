package com.vanegasazucena.appministrator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vanegasazucena.appministrator.ui.MainScreen
import com.vanegasazucena.appministrator.ui.theme.AppministratorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppministratorTheme {
                MainScreen(viewModel = viewModel())
            }
        }
    }
}
