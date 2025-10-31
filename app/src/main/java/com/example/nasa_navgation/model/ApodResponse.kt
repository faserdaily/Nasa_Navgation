package com.example.nasa_navgation.model

// NASA APOD (Astronomy Picture of the Day) API 響應資料模型
// 這個資料類別用於封裝 NASA 每日天文圖片 API 的回應資料。
// 使用 data class 的好處：
// 1. 自動生成 equals()、hashCode()、toString() 等方法
// 2. 支援解構聲明（destructuring declarations）
// 3. 支援 copy() 方法用於創建修改後的副本
// API 文件參考：https://api.nasa.gov/#apod
data class ApodResponse(
    // 圖片的日期，格式為 YYYY-MM-DD
    val date: String,
    
    // 圖片的詳細說明文字
    val explanation: String,
    
    // 高解析度圖片的 URL（可能為 null，有些圖片沒有高解析度版本）
    val hdurl: String?,
    
    // 媒體類型，通常是 "image" 或 "video"
    val media_type: String,
    
    // API 服務版本號
    val service_version: String,
    
    // 圖片的標題
    val title: String,
    
    // 標準解析度圖片的 URL（一定存在）
    val url: String
)

