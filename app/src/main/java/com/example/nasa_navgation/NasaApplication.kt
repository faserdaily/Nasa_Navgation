package com.example.nasa_navgation

import android.app.Application

// 應用程式的 Application 類別
// 用於保存全局 Context，方便在整個應用程式中使用
class NasaApplication : Application() {
    // 保存 Application Context 的靜態變數
    // 使用 lateinit 因為在 onCreate 之前可能為 null
    companion object {
        @Volatile
        private var instance: NasaApplication? = null
        
        /**
         * 獲取 Application Context
         * @return Application Context，如果還沒初始化則返回 null
         */
        fun getContext(): android.content.Context? {
            return instance?.applicationContext
        }
    }
    
    // Application 生命週期方法：當應用程式啟動時調用
    override fun onCreate() {
        super.onCreate()
        // 保存實例，這樣就可以在 Logger 中取得 Context
        instance = this
    }
}

