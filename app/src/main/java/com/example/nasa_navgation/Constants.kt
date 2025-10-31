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
    // 管理應用程式中所有圖片資源的引用
    object Images {
        // NASA 標誌圖片資源 ID
        var NASA_LOGO = R.drawable.nasa_logo
        
        // NASA 標誌的無障礙描述文字，用於螢幕閱讀器
        const val NASA_LOGO_DESCRIPTION = "NASA Logo"
    }
    
    // UI 相關常數
    // 管理使用者介面的尺寸、比例等常數值
    object UI {
        // 圖片寬度比例：圖片寬度為螢幕寬度的 86%
        const val IMAGE_WIDTH_RATIO = 0.86f
        
        // 圖片長寬比：1:1，即正方形
        const val IMAGE_ASPECT_RATIO = 1f
    }
    
    // 文字常數
    // 管理應用程式中顯示的文字內容
    object Text {
        // 應用程式名稱
        const val APP_NAME = "NASA_Navigator"
    }
    
    // API 相關常數
    // 管理所有與外部 API 相關的常數值
    object API {
        // NASA API 金鑰，用於存取 NASA Astronomy Picture of the Day (APOD) API
        const val NASA_API_KEY = "pNEoNsz9bCmAsno9YvcJGekCPgNhrWA5GTQEk4NG"
    }
}

