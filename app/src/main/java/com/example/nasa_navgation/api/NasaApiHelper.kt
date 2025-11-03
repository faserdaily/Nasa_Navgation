package com.example.nasa_navgation.api

import com.example.nasa_navgation.Constants
import com.example.nasa_navgation.Logger
import com.example.nasa_navgation.model.ApodResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

// NASA API 請求類別
// 採用單例模式（object），負責與 NASA APOD API 進行通訊。
// 技術選擇說明：
// 1. 使用 OkHttp 進行網路請求（輕量級，無需 Retrofit）
// 2. 使用 Android 原生的 JSONObject 解析 JSON（無需 Gson 等額外依賴）
// 3. 使用 Kotlin Coroutines 進行異步處理
// 4. 使用 Result 類型封裝成功/失敗狀態
// 功能特性：
// - 獲取今日的 APOD 圖片
// - 智能回退：如果今天沒有圖片，自動嘗試獲取昨天的圖片
// - 完整的錯誤處理和日誌記錄
object NasaApiHelper {
    
    // OkHttp 客戶端實例
    // 配置了連接、讀取、寫入的超時時間，確保網路請求不會無限等待
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)  // 連接超時：30 秒
        .readTimeout(30, TimeUnit.SECONDS)      // 讀取超時：30 秒
        .writeTimeout(30, TimeUnit.SECONDS)     // 寫入超時：30 秒
        .build()
    
    // 獲取今日的 APOD 資料
    // 智能獲取策略：
    // 1. 首先嘗試不指定日期參數，讓 API 自動返回當天的圖片
    // 2. 如果今天沒有圖片（API 返回錯誤或空回應），自動嘗試獲取昨天的圖片
    // 3. 這樣可以確保使用者總能看到內容，即使今天沒有新圖片
    // 執行流程：
    // 1. 構建 API 請求 URL（不包含日期參數）
    // 2. 發送 HTTP GET 請求
    // 3. 檢查回應狀態和內容
    // 4. 如果成功，解析 JSON 並返回資料
    // 5. 如果失敗，嘗試獲取昨天的圖片作為回退
    // @return Result<ApodResponse> 成功時包含 APOD 資料，失敗時包含異常資訊
    // 注意：此方法使用 suspend 函數和 withContext(Dispatchers.IO)，
    // 確保網路請求在 IO 執行緒中進行，不會阻塞 UI 執行緒
    suspend fun getTodayApod(): Result<ApodResponse> = withContext(Dispatchers.IO) {
        try {
            val apod_Url = "${Constants.API.BASE_URL}?api_key=${Constants.API.NASA_API_KEY}"
            
            Logger.log("嘗試獲取今日圖片，請求 URL: $apod_Url", tag = "NasaApiHelper")

            val apod_request = Request.Builder()
                .url(apod_Url)
                .build()
            
            // 執行請求並使用 use{} 確保資源正確關閉
            client.newCall(apod_request).execute().use { response ->

                val responseBody = response.body?.string() ?: ""

                Logger.log("回應狀態碼: ${response.code}", tag = "NasaApiHelper")
                
                // 檢查請求是否成功且回應內容不為空
                if (response.isSuccessful && responseBody.isNotEmpty()) {
                    try {
                        val jsonObject = JSONObject(responseBody)
                        
                        // 檢查 JSON 中是否包含錯誤訊息
                        // NASA API 會在 JSON 中返回錯誤訊息，而不是使用 HTTP 錯誤碼
                        if (jsonObject.has("error")) {
                            val errorMessage = jsonObject.optString("error", Constants.Text.UNKNOWN_ERROR)
                            Logger.log("今天沒有圖片: $errorMessage，嘗試獲取最近的圖片", tag = "NasaApiHelper")
                            // 智能回退：嘗試獲取昨天的圖片
                            return@use getApodByDate(getYesterdayDate())
                        }
                        
                        // 成功獲取資料，解析 JSON 為 ApodResponse
                        val apod = parseApodResponse(jsonObject)
                        Logger.log("成功獲取今日圖片: ${apod.title} (${apod.date})", tag = "NasaApiHelper")
                        Result.success(apod)
                    } catch (e: Exception) {
                        // JSON 解析過程中的異常
                        Logger.log("${Constants.Text.JSON_PARSE_ERROR}: ${e.message}", tag = "NasaApiHelper")
                        Result.failure(IOException("${Constants.Text.JSON_PARSE_ERROR}: ${e.message}", e))
                    }
                } else {
                    // HTTP 請求失敗或回應為空，嘗試獲取昨天的圖片
                    Logger.log("無法獲取今日圖片，嘗試獲取最近的圖片", tag = "NasaApiHelper")
                    return@use getApodByDate(getYesterdayDate())
                }
            }
        } catch (e: IOException) {
            // 網路相關異常（連接失敗、超時等）
            Logger.log("${Constants.Text.NETWORK_REQUEST_FAILED}: ${e.message}", tag = "NasaApiHelper")
            Result.failure(IOException("${Constants.Text.NETWORK_REQUEST_FAILED}: ${e.message}", e))
        } catch (e: Exception) {
            // 其他未知異常
            Logger.log("${Constants.Text.UNKNOWN_ERROR}: ${e.message}", tag = "NasaApiHelper")
            Result.failure(e)
        }
    }
    
    // 獲取昨天的日期字串
    // 用於當今天沒有圖片時的智能回退機制。
    // @return 格式化的日期字串，格式為 YYYY-MM-DD（例如：2025-01-15）
    private fun getYesterdayDate(): String {
        // 獲取當前日曆實例
        val calendar = java.util.Calendar.getInstance()
        
        // 將日期減去 1 天
        calendar.add(java.util.Calendar.DAY_OF_MONTH, -1)
        
        // 使用 SimpleDateFormat 格式化日期為 YYYY-MM-DD 格式
        return SimpleDateFormat(Constants.API.DATE_FORMAT, Locale.getDefault()).format(calendar.time)
    }
    
    // 獲取指定日期的 APOD 資料（私有方法）
    // 此方法用於獲取特定日期的天文圖片，主要用於智能回退機制。
    // 當今天沒有圖片時，會調用此方法嘗試獲取昨天的圖片。
    // 執行流程：
    // 1. 構建包含日期參數的 API URL
    // 2. 發送 HTTP GET 請求
    // 3. 解析回應的 JSON 資料
    // 4. 檢查是否有錯誤訊息
    // 5. 返回成功或失敗的 Result
    // @param date 日期字串，格式必須為 YYYY-MM-DD（例如：2025-01-15）
    // @return Result<ApodResponse> 成功時包含 APOD 資料，失敗時包含異常資訊
    // 注意：
    // - 此方法是私有的，只在內部使用（用於智能回退）
    // - 如果該日期沒有圖片，會返回包含錯誤訊息的 Result
    // - 日期格式錯誤或日期不存在會導致請求失敗
    private suspend fun getApodByDate(date: String): Result<ApodResponse> = withContext(Dispatchers.IO) {
        try {
            // 構建包含日期參數的 URL
            val url = "${Constants.API.BASE_URL}?api_key=${Constants.API.NASA_API_KEY}&date=$date"
            Logger.log("嘗試獲取日期 $date 的圖片，URL: $url", tag = "NasaApiHelper")
            
            // 創建 HTTP 請求
            val request = Request.Builder()
                .url(url)
                .build()
            
            // 執行請求
            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string() ?: ""
                
                // 檢查請求是否成功
                if (response.isSuccessful && responseBody.isNotEmpty()) {
                    try {
                        val jsonObject = JSONObject(responseBody)
                        
                        // 檢查 API 返回的錯誤訊息
                        if (jsonObject.has("error")) {
                            val errorMessage = jsonObject.optString("error", Constants.Text.UNKNOWN_ERROR)
                            Logger.log("日期 $date 沒有圖片: $errorMessage", tag = "NasaApiHelper")
                            return@use Result.failure(IOException("${Constants.Text.NO_IMAGE_FOR_DATE}: $errorMessage"))
                        }
                        
                        // 成功解析資料
                        val apod = parseApodResponse(jsonObject)

                        Logger.log("成功獲取日期 ${apod.date} 的圖片: ${apod.title}", tag = "NasaApiHelper")
                        Result.success(apod)
                    } catch (e: Exception) {
                        // JSON 解析異常
                        Logger.log("${Constants.Text.JSON_PARSE_ERROR}: ${e.message}", tag = "NasaApiHelper")
                        Result.failure(IOException("${Constants.Text.JSON_PARSE_ERROR}: ${e.message}", e))
                    }
                } else {
                    // HTTP 請求失敗
                    Logger.log("${Constants.Text.HTTP_ERROR}: ${response.code} - ${response.message}", tag = "NasaApiHelper")
                    Result.failure(
                        IOException("${Constants.Text.HTTP_ERROR} ${response.code}: ${response.message}")
                    )
                }
            } ?: Result.failure(IOException(Constants.Text.CANNOT_EXECUTE_REQUEST))
        } catch (e: Exception) {
            // 其他異常（網路異常、格式錯誤等）
            Logger.log("${Constants.Text.CANNOT_EXECUTE_REQUEST}: ${e.message}", tag = "NasaApiHelper")
            Result.failure(e)
        }
    }
    
    // 解析 JSON 物件為 ApodResponse 資料模型
    // 將從 API 獲取的 JSON 資料轉換為型別安全的 Kotlin 資料類別。
    // @param json 從 API 獲取的 JSONObject
    // @return 解析後的 ApodResponse 物件
    // 注意：
    // - 使用 optString() 而非 getString()，避免欄位不存在時拋出異常
    // - hdurl 可能為 null（有些圖片沒有高解析度版本）
    // - 使用 takeIf { it.isNotEmpty() } 確保空字串會被轉換為 null
    private fun parseApodResponse(json: JSONObject): ApodResponse {
        return ApodResponse(
            // 日期欄位，預設值為空字串
            date = json.optString("date", ""),
            
            // 說明文字
            explanation = json.optString("explanation", ""),
            
            // 高解析度圖片 URL（可能為 null）
            // 如果值為空字串，則轉換為 null
            hdurl = json.optString("hdurl", null).takeIf { it.isNotEmpty() },
            
            // 媒體類型（通常是 "image"）
            media_type = json.optString("media_type", ""),
            
            // API 服務版本
            service_version = json.optString("service_version", ""),
            
            // 圖片標題
            title = json.optString("title", ""),
            
            // 標準解析度圖片 URL（一定存在）
            url = json.optString("url", "")
        )
    }
}

