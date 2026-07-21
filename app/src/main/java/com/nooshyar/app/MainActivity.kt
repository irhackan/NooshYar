package com.nooshyar.app

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.nooshyar.app.presentation.navigation.NooshYarNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val notificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* handled */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            Surface(modifier = Modifier.fillMaxSize()) {
                NooshYarNavHost()
            }
        }
    }
}
