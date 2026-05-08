package com.example.disneyapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.disneyapp.core.presentation.splash.DisneySplashGate
import com.example.disneyapp.ui.theme.DisneyAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            installSplashScreen()
        } else {
            setTheme(R.style.Theme_DisneyApp)
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DisneyAppTheme {
                DisneySplashGate {
                    DisneyAppRoot()
                }
            }
        }
    }
}
