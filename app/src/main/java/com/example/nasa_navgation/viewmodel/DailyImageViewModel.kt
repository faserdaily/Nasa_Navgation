package com.example.nasa_navgation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    // 使用 viewModelScope.launch 確保：
    // - 在 ViewModel 被清除時自動取消協程
    // - 避免記憶體洩漏
    fun fetchTodayImage() {
        viewModelScope.launch {
            // 設置為載入狀態
            _uiState.value = DailyImageUiState.Loading
            Log.d("DailyImageViewModel", "開始獲取今日圖片")
            
            // 調用 API Helper 獲取資料
            val result = NasaApiHelper.getTodayApod()
            
            // 檢查請求是否成功
            if (result.isSuccess) {
                // 嘗試獲取成功的資料
                result.getOrNull()?.let { apod ->
                    // 成功獲取資料，更新狀態為成功
                    Log.d("DailyImageViewModel", "成功獲取圖片: ${apod.title}")
                    _uiState.value = DailyImageUiState.Success(apod)
                } ?: run {
                    // 雖然 isSuccess 為 true，但資料為 null（異常情況）
                    Log.e("DailyImageViewModel", "無法解析回應資料")
                    _uiState.value = DailyImageUiState.Error("無法解析回應資料")
                }
            } else {
                // 請求失敗，獲取錯誤訊息
                val exception = result.exceptionOrNull()
                val errorMessage = exception?.message ?: "發生未知錯誤"
                Log.e("DailyImageViewModel", "獲取圖片失敗: $errorMessage", exception)
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

