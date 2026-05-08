package com.example.disneycast

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.disneycast.core.presentation.splash.DisneySplashGate
import com.example.disneycast.ui.theme.DisneyCastTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            installSplashScreen()
        } else {
            setTheme(R.style.Theme_DisneyCast)
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DisneyCastTheme {
                DisneySplashGate {
                    DisneyCastRoot()
                }
            }
        }
    }
}
