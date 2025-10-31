package com.example.nasa_navgation

import android.content.Context
import android.content.Intent

/**
 * 導航管理類
 * 集中管理應用程式內的所有導航功能
 */
object Navigator {
    /**
     * 導航到每日推薦圖片頁面
     */
    fun navigateToDailyImage(context: Context) {
        val intent = Intent(context, DailyImageActivity::class.java)
        context.startActivity(intent)
    }
    
    // 未來可以在這裡添加更多導航方法
    // 例如：導航到設定頁面、關於頁面等
}

