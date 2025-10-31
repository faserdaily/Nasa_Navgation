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

// 每日推薦圖片 Activity
// 這個 Activity 顯示 NASA 的每日推薦天文圖片（Astronomy Picture of the Day, APOD）。
// 主要功能：
// 1. 從 NASA API 獲取今日的天文圖片
// 2. 顯示圖片標題、日期、圖片和說明文字
// 3. 如果今天沒有圖片，會自動嘗試獲取最近的圖片（例如昨天的圖片）
// 架構說明：
// - 使用 MVVM 架構模式
// - UI 層：DailyImageScreen Composable
// - 業務邏輯層：DailyImageViewModel
// - 資料層：NasaApiHelper（API 請求）
class DailyImageActivity : ComponentActivity() {
    // Activity 生命週期方法：當 Activity 被創建時調用
    // @param savedInstanceState 保存的狀態資料（如果有）
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 啟用 Edge-to-Edge 模式
        enableEdgeToEdge()
        
        // 設置 Compose UI
        setContent {
            // 應用主題
            Nasa_NavgationTheme {
                // 使用 Scaffold 提供基本的 Material Design 布局
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // 顯示每日圖片畫面
                    // DailyImageScreen 內部會自動創建並使用 DailyImageViewModel
                    DailyImageScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

