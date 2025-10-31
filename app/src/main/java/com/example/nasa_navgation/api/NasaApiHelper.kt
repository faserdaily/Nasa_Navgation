package com.example.nasa_navgation.api

import android.util.Log
import com.example.nasa_navgation.Constants
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

/**
 * NASA API 請求類別
 * 使用 OkHttp 進行網路請求，使用 Android 原生的 JSONObject 解析 JSON
 */
object NasaApiHelper {
    private const val BASE_URL = "https://api.nasa.gov/planetary/apod"
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    /**
     * 獲取今日的 APOD 資料
     * 如果今天沒有圖片，會自動嘗試獲取最近的有圖片的日期
     */
    suspend fun getTodayApod(): Result<ApodResponse> = withContext(Dispatchers.IO) {
        try {
            // 首先嘗試不指定日期，讓 API 返回當天的圖片
            val url = "$BASE_URL?api_key=${Constants.API.NASA_API_KEY}"
            
            Log.d("NasaApiHelper", "嘗試獲取今日圖片，請求 URL: $url")
            
            val request = Request.Builder()
                .url(url)
                .build()
            
            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string() ?: ""
                Log.d("NasaApiHelper", "回應狀態碼: ${response.code}")
                
                if (response.isSuccessful && responseBody.isNotEmpty()) {
                    try {
                        val jsonObject = JSONObject(responseBody)
                        
                        // 檢查是否有錯誤訊息
                        if (jsonObject.has("error")) {
                            val errorMessage = jsonObject.optString("error", "未知錯誤")
                            Log.w("NasaApiHelper", "今天沒有圖片: $errorMessage，嘗試獲取最近的圖片")
                            // 如果今天沒有，嘗試獲取昨天的
                            return@use getApodByDate(getYesterdayDate())
                        }
                        
                        val apod = parseApodResponse(jsonObject)
                        Log.d("NasaApiHelper", "成功獲取今日圖片: ${apod.title} (${apod.date})")
                        Result.success(apod)
                    } catch (e: Exception) {
                        Log.e("NasaApiHelper", "JSON 解析錯誤", e)
                        Result.failure(IOException("JSON 解析錯誤: ${e.message}", e))
                    }
                } else {
                    // 如果今天沒有，嘗試獲取昨天的
                    Log.w("NasaApiHelper", "無法獲取今日圖片，嘗試獲取最近的圖片")
                    return@use getApodByDate(getYesterdayDate())
                }
            } ?: Result.failure(IOException("無法執行請求"))
        } catch (e: IOException) {
            Log.e("NasaApiHelper", "網路請求錯誤", e)
            Result.failure(IOException("網路請求失敗: ${e.message}", e))
        } catch (e: Exception) {
            Log.e("NasaApiHelper", "未知錯誤", e)
            Result.failure(e)
        }
    }
    
    /**
     * 獲取昨天的日期字串
     */
    private fun getYesterdayDate(): String {
        val calendar = java.util.Calendar.getInstance()
        calendar.add(java.util.Calendar.DAY_OF_MONTH, -1)
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
    }
    
    /**
     * 獲取指定日期的 APOD 資料
     * @param date 日期格式：YYYY-MM-DD
     * @return Result，成功時包含 ApodResponse，失敗時包含異常
     */
    private suspend fun getApodByDate(date: String): Result<ApodResponse> = withContext(Dispatchers.IO) {
        try {
            val url = "$BASE_URL?api_key=${Constants.API.NASA_API_KEY}&date=$date"
            Log.d("NasaApiHelper", "嘗試獲取日期 $date 的圖片，URL: $url")
            
            val request = Request.Builder()
                .url(url)
                .build()
            
            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string() ?: ""
                
                if (response.isSuccessful && responseBody.isNotEmpty()) {
                    try {
                        val jsonObject = JSONObject(responseBody)
                        
                        // 檢查是否有錯誤訊息
                        if (jsonObject.has("error")) {
                            val errorMessage = jsonObject.optString("error", "未知錯誤")
                            Log.w("NasaApiHelper", "日期 $date 沒有圖片: $errorMessage")
                            return@use Result.failure(IOException("該日期沒有圖片: $errorMessage"))
                        }
                        
                        val apod = parseApodResponse(jsonObject)
                        Log.d("NasaApiHelper", "成功獲取日期 $date 的圖片: ${apod.title}")
                        Result.success(apod)
                    } catch (e: Exception) {
                        Log.e("NasaApiHelper", "JSON 解析錯誤", e)
                        Result.failure(IOException("JSON 解析錯誤: ${e.message}", e))
                    }
                } else {
                    Log.e("NasaApiHelper", "HTTP 錯誤: ${response.code} - ${response.message}")
                    Result.failure(
                        IOException("HTTP ${response.code}: ${response.message}")
                    )
                }
            } ?: Result.failure(IOException("無法執行請求"))
        } catch (e: Exception) {
            Log.e("NasaApiHelper", "請求錯誤", e)
            Result.failure(e)
        }
    }
    
    /**
     * 解析 JSON 物件為 ApodResponse
     */
    private fun parseApodResponse(json: JSONObject): ApodResponse {
        return ApodResponse(
            date = json.optString("date", ""),
            explanation = json.optString("explanation", ""),
            hdurl = json.optString("hdurl", null).takeIf { it.isNotEmpty() },
            media_type = json.optString("media_type", ""),
            service_version = json.optString("service_version", ""),
            title = json.optString("title", ""),
            url = json.optString("url", "")
        )
    }
}

