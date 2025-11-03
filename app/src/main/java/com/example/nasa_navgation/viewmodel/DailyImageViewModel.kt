package com.example.nasa_navgation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nasa_navgation.Constants
import com.example.nasa_navgation.Logger
import com.example.nasa_navgation.api.NasaApiHelper
import com.example.nasa_navgation.model.ApodResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// 每日圖片 ViewModel
// 採用 MVVM 架構模式中的 ViewModel 層，負責：
// 1. 管理 UI 狀態（載入中、成功、錯誤）
// 2. 處理業務邏輯（獲取圖片資料）
// 3. 與資料層（API Helper）通訊
// 使用 StateFlow 進行狀態管理：
// - StateFlow 是 Kotlin Flow 的一種，適合用於 UI 狀態
// - 具有初始值，UI 可以立即訂閱並獲取當前狀態
// - 會自動處理生命週期，Activity/Fragment 重建時不會重複請求
class DailyImageViewModel : ViewModel() {
    // 私有的可變狀態流
    // 使用 MutableStateFlow 作為內部狀態容器
    // 前置底線（_）表示這是私有的實現細節
    private val _uiState = MutableStateFlow<DailyImageUiState>(DailyImageUiState.Loading)
    
    // 公開的不可變狀態流
    // UI 層只能讀取狀態，不能直接修改
    // 使用 asStateFlow() 確保對外只提供唯讀的 StateFlow
    val uiState: StateFlow<DailyImageUiState> = _uiState.asStateFlow()
    
    // 初始化區塊
    // 當 ViewModel 被創建時自動執行，立即開始獲取圖片資料
    init {
        fetchTodayImage()
    }
    
    // 獲取今日的圖片
    // 這是主要的業務邏輯方法，負責：
    // 1. 設置載入狀態
    // 2. 調用 API Helper 獲取資料
    // 3. 處理成功/失敗情況
    // 4. 更新 UI 狀態
    // 
    // viewModelScope.launch 說明：
    // - 在背景執行任務，不會卡住畫面（UI 執行緒）
    // - 當 ViewModel 被清除時，會自動取消所有在這個 scope 裡執行的任務
    // - 避免記憶體洩漏（如果畫面關閉了，任務也會自動停止）
    // 
    // 完整流程：
    // 1. 先顯示「載入中」狀態，讓使用者知道正在處理
    // 2. 在背景去 NASA API 拿今天的圖片資料（這需要時間，所以不能在主執行緒做）
    // 3. 根據結果決定要顯示成功還是失敗的畫面
    fun fetchTodayImage() {
        viewModelScope.launch {
            // 步驟 1：設置為載入狀態
            // 把顯示牌改成「載入中」，UI 看到這個狀態後會自動顯示轉圈圈的載入動畫
            _uiState.value = DailyImageUiState.Loading
            Logger.log("開始獲取今日圖片", tag = "DailyImageViewModel")
            
            // 步驟 2：調用 API Helper 獲取資料
            // 這會在背景執行，可能需要 1-3 秒（網路請求需要時間）
            // 因為在 viewModelScope.launch 裡面，所以不會卡住畫面
            val result = NasaApiHelper.getTodayApod()
            
            // 步驟 3：檢查請求是否成功
            if (result.isSuccess) {
                // result.getOrNull()?.let { } ?: run { } 語法說明：
                // 
                // 這是一段「安全取得資料」的 Kotlin 語法：
                // 
                // 1. result.getOrNull()
                //    - 嘗試從結果中取得資料
                //    - 如果有資料：返回資料物件（ApodResponse）
                //    - 如果沒資料：返回 null
                // 
                // 2. ?.let { apod -> ... }
                //    - ?. 是「安全呼叫運算符」，意思是「如果前面不是 null，才執行後面」
                //    - let { } 是一個函數，如果前面的值不是 null，就會執行 { } 裡的程式碼
                //    - { apod -> } 表示把取得的資料傳給 apod 這個變數
                //    - 白話：如果 getOrNull() 有拿到資料，就執行這個區塊，並把資料叫做 apod
                // 
                // 3. ?: run { ... }
                //    - ?: 是「Elvis 運算符」，意思是「如果前面是 null，就執行後面」
                //    - run { } 是執行一個程式碼區塊
                //    - 白話：如果 getOrNull() 沒有拿到資料（是 null），就執行這個區塊
                // 
                // 整體邏輯：
                // 「先試著拿資料，有資料就執行 let 區塊（成功處理），沒資料就執行 run 區塊（錯誤處理）」
                // 
                // 等價的 if-else 寫法：
                // val data = result.getOrNull()
                // if (data != null) {
                //     val apod = data
                //     // 使用 apod 做事情
                // } else {
                //     // 處理沒有資料的情況
                // }
                result.getOrNull()?.let { apod ->
                    // 有資料的情況：成功獲取到圖片資料
                    // apod 就是從 API 取得的 ApodResponse 資料物件
                    Logger.log("成功獲取圖片: ${apod.title}", tag = "DailyImageViewModel")
                    // 更新狀態為成功，並把資料傳給 UI
                    _uiState.value = DailyImageUiState.Success(apod)
                } ?: run {
                    // 沒資料的情況：雖然 result.isSuccess 是 true，但資料卻是 null
                    // 這是一種異常情況（理論上不應該發生，但我們要做好防護）
                    Logger.log(Constants.Text.CANNOT_PARSE_RESPONSE, tag = "DailyImageViewModel")
                    // 更新狀態為錯誤，告訴 UI 無法解析資料
                    _uiState.value = DailyImageUiState.Error(Constants.Text.CANNOT_PARSE_RESPONSE)
                }
            } else {
                // 請求失敗的情況：API 請求本身失敗了（例如網路問題、伺服器錯誤等）
                
                // 嘗試取得錯誤的詳細訊息
                // exception?.message 的意思是：
                // - 如果 exception 不是 null，就取它的 message 屬性
                // - 如果 exception 是 null，就返回 null
                // 
                // ?: Constants.Text.UNKNOWN_ERROR_OCCURRED 的意思是：
                // - 如果前面的值是 null，就使用預設的錯誤訊息
                val exception = result.exceptionOrNull()
                val errorMessage = exception?.message ?: Constants.Text.UNKNOWN_ERROR_OCCURRED
                
                Logger.log("獲取圖片失敗: $errorMessage", tag = "DailyImageViewModel")
                // 更新狀態為錯誤，並把錯誤訊息傳給 UI 顯示
                _uiState.value = DailyImageUiState.Error(errorMessage)
            }
        }
    }
    
    // 重新載入圖片
    // 用於手動刷新資料，例如下拉刷新等場景
    fun refresh() {
        fetchTodayImage()
    }
}

// UI 狀態封裝類（密封類別）
// 使用 sealed class 的好處：
// 1. 類型安全：編譯器會檢查所有可能的情況（when 表達式必須處理所有狀態）
// 2. 限制繼承：只能在同一個檔案內定義子類別
// 3. 清晰表達：明確表示 UI 可能的所有狀態
// 三種狀態：
// - Loading：正在載入資料
// - Success：成功獲取資料
// - Error：載入失敗，包含錯誤訊息
sealed class DailyImageUiState {
    // 載入中狀態：顯示載入指示器
    object Loading : DailyImageUiState()
    
    // 成功狀態：包含獲取的 APOD 資料
    data class Success(val apod: ApodResponse) : DailyImageUiState()
    
    // 錯誤狀態：包含錯誤訊息
    data class Error(val message: String) : DailyImageUiState()
}

