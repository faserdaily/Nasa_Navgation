package com.example.nasa_navgation

import android.content.Context
import android.util.Log
import android.widget.Toast

// Logger 工具類別
// 提供簡單的 Logcat 和 Toast 輸出功能
// 使用 Lonoff 和 Tonoff 參數控制是否顯示
object Logger {
    // 預設標籤
    private const val DEFAULT_TAG = "NasaApp"
    
    /**
     * 輸出 Logcat 和 Toast
     * @param message 想要查找的訊息（可接受任何型別，會自動轉換為字串）
     * @param context Toast 需要 Context（可為 null，如果為 null 則嘗試自動取得 Application Context）
     * @param Lonoff Logcat 開關（true = 開啟，false = 關閉，預設為 true）
     * @param Tonoff Toast 開關（true = 開啟，false = 關閉，預設為 true）
     * @param tag Logcat 標籤（預設為 "NasaApp"）
     */
    fun log(
        message: Any?,
        context: Context? = null,
        Lonoff: Boolean = true,
        Tonoff: Boolean = true,
        tag: String = DEFAULT_TAG
    ) {
        // 將訊息轉換為字串（null 會顯示為 "null"）
        val messageStr = message?.toString() ?: "null"
        
        // 輸出到 Logcat
        if (Lonoff) {
            Log.d(tag, messageStr)
        }
        
        // 顯示 Toast
        if (Tonoff) {
            // 優先使用傳入的 context，如果沒有則嘗試取得 Application Context
            val toastContext = context ?: NasaApplication.getContext()
            if (toastContext != null) {
                Toast.makeText(toastContext, messageStr, Toast.LENGTH_SHORT).show()
            } else {
                // Toast's Context 為 null，輸出警告到 Logcat
                Log.w(tag, "Toast's Context 為 null")
            }
        }
    }
}
