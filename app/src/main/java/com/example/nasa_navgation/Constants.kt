package com.example.nasa_navgation

// 應用程式常數類別
// 此類別採用單例模式（object），集中管理應用程式中所有的常數值。
// 這樣做的好處：
// 1. 降低耦合度：避免在多處硬編碼相同的值
// 2. 易於維護：修改數值時只需在一個地方進行
// 3. 結構清晰：使用巢狀 object 分類管理不同類型的常數
// 4. 類型安全：使用 const val 確保編譯時常數
object Constants {
    // 圖片資源常數
    object Images {
        var NASA_LOGO = R.drawable.nasa_logo

        const val NASA_LOGO_DESCRIPTION = "NASA Logo"
    }
    
    // UI 相關常數
    // 管理使用者介面的尺寸、比例等常數值
    object UI {
        // 圖片寬度比例：圖片寬度為螢幕寬度的 86%
        const val IMAGE_WIDTH_RATIO = 0.98f
        
        // 圖片長寬比：1:1，即正方形
        const val IMAGE_ASPECT_RATIO = 1f
    }
    
    // 文字常數
    object Text {
        // 應用程式名稱
        const val APP_NAME = "NASA_Navigator"
        
        // UI 提示文字
        const val HIT_IMAGE_TO_CONTINUE = "Hit Image to Continue"
        
        // 錯誤訊息
        const val LOAD_FAILED = "載入失敗"
        const val CANNOT_PARSE_RESPONSE = "無法解析回應資料"
        const val UNKNOWN_ERROR_OCCURRED = "發生未知錯誤"
        const val UNKNOWN_ERROR = "未知錯誤"
        const val JSON_PARSE_ERROR = "JSON 解析錯誤"
        const val NETWORK_REQUEST_FAILED = "網路請求失敗"
        const val HTTP_ERROR = "HTTP 錯誤"
        const val CANNOT_EXECUTE_REQUEST = "無法執行請求"
        const val NO_IMAGE_FOR_DATE = "該日期沒有圖片"
    }
    
    // API 相關常數
    object API {
        // NASA API 金鑰，用於存取 NASA Astronomy Picture of the Day (APOD) API
        const val NASA_API_KEY = "pNEoNsz9bCmAsno9YvcJGekCPgNhrWA5GTQEk4NG"
        
        // NASA API 基礎 URL
        const val BASE_URL = "https://api.nasa.gov/planetary/apod"
        
        // 日期格式
        const val DATE_FORMAT = "yyyy-MM-dd"
    }
    
    // Logger 相關常數
    object Logger {
        // 預設標籤
        const val DEFAULT_TAG = "NasaApp"
        
        // 訊息
        const val NULL_STRING = "null"
        const val TOAST_CONTEXT_NULL = "Toast's Context 為 null"
    }
    
    // UI 數值常數
    object UIValues {
        // 字體大小
        const val TEXT_SIZE_SMALL = 14
        const val TEXT_SIZE_MEDIUM = 16
        
        // 圖片長寬比
        const val IMAGE_ASPECT_RATIO_4_3 = 4f / 3f
    }
}

