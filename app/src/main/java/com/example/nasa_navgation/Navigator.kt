package com.example.nasa_navgation

import android.content.Context
import android.content.Intent

// 導航管理類
// 採用單例模式（object），集中管理應用程式內的所有頁面導航功能。
// 設計目的：
// 1. 統一管理導航邏輯，避免在 UI 層直接創建 Intent
// 2. 降低耦合度：UI 元件只需調用導航方法，不需要知道目標 Activity 的詳細資訊
// 3. 易於擴展：未來新增頁面時，只需在此添加對應的導航方法
// 4. 易於測試：可以輕鬆模擬導航行為
object Navigator {
    // 導航到每日推薦圖片頁面
    // @param context Android Context，用於啟動新的 Activity
    //                可以是 Activity、Application 或其他 Context 實例
    fun navigateToDailyImage(context: Context) {
        // 創建 Intent，指定目標 Activity 為 DailyImageActivity
        val intent = Intent(context, DailyImageActivity::class.java)
        
        // 啟動目標 Activity
        context.startActivity(intent)
    }
    
    // 未來擴展範例：
    // fun navigateToSettings(context: Context) { ... }
    // fun navigateToAbout(context: Context) { ... }
    // fun navigateToHistory(context: Context, date: String) { ... }
}

