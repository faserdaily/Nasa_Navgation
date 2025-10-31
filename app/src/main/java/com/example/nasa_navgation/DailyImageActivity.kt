package com.example.nasa_navgation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.nasa_navgation.ui.DailyImageScreen
import com.example.nasa_navgation.ui.theme.Nasa_NavgationTheme

/**
 * 每日推薦圖片 Activity
 */
class DailyImageActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Nasa_NavgationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DailyImageScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

