package com.agar.commander

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.agar.commander.ui.AgarCommanderApp
import com.agar.commander.ui.theme.AgarCommanderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)

        setContent {
            AgarCommanderTheme {
                AgarCommanderApp()
            }
        }
    }
}
