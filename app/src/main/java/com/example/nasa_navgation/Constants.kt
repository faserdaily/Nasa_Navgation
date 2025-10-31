package com.example.nasa_navgation

/**
 * 應用程式常數類別
 * 集中管理所有常數值，降低耦合度
 */
object Constants {
    /**
     * 圖片資源常數
     */
    object Images {
        var NASA_LOGO = R.drawable.nasa_logo
        const val NASA_LOGO_DESCRIPTION = "NASA Logo"
    }
    
    /**
     * UI 常數
     */
    object UI {
        const val IMAGE_WIDTH_RATIO = 0.86f  // 圖片寬度為螢幕寬度的 86%
        const val IMAGE_ASPECT_RATIO = 1f    // 長寬比 1:1
    }
    
    /**
     * 文字常數
     */
    object Text {
        const val APP_NAME = "NASA_Navigator"
    }
    
    /**
     * API 相關常數
     */
    object API {
        const val NASA_API_KEY = "pNEoNsz9bCmAsno9YvcJGekCPgNhrWA5GTQEk4NG"
    }
}

