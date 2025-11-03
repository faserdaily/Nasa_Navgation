package com.example.nasa_navgation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.nasa_navgation.ui.screen.HomeScreen
import com.example.nasa_navgation.ui.theme.Nasa_NavgationTheme

// 主 Activity
// 這是應用程式的入口點，繼承自 ComponentActivity，
// 使用 Jetpack Compose 構建使用者介面。
// 功能說明：
// 1. 顯示應用程式首頁（HomeScreen）
// 2. 提供導航到每日推薦圖片頁面的入口（通過點擊 NASA Logo）
class MainActivity : ComponentActivity() {
    // Activity 生命週期方法：當 Activity 被創建時調用
    // @param savedInstanceState 如果 Activity 被系統終止後重新創建，
    //                           這個 Bundle 會包含之前保存的狀態資料
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 啟用 Edge-to-Edge 模式，讓內容延伸到系統欄位下方
        // 提供更現代的視覺效果
        enableEdgeToEdge()
        
        // 設置 Compose 內容
        setContent {
            // 應用自定義主題
            Nasa_NavgationTheme {
                // Scaffold 提供基本的 Material Design 布局結構
                // innerPadding 會自動處理系統欄位（狀態欄、導航欄）的內邊距
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // 顯示首頁畫面，並應用系統欄位的內邊距
                    HomeScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
