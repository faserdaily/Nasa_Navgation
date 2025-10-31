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

/**
 * 每日圖片 ViewModel
 */
class DailyImageViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<DailyImageUiState>(DailyImageUiState.Loading)
    val uiState: StateFlow<DailyImageUiState> = _uiState.asStateFlow()
    
    init {
        fetchTodayImage()
    }
    
    /**
     * 獲取今日的圖片
     */
    fun fetchTodayImage() {
        viewModelScope.launch {
            _uiState.value = DailyImageUiState.Loading
            Log.d("DailyImageViewModel", "開始獲取今日圖片")
            
            val result = NasaApiHelper.getTodayApod()
            if (result.isSuccess) {
                result.getOrNull()?.let { apod ->
                    Log.d("DailyImageViewModel", "成功獲取圖片: ${apod.title}")
                    _uiState.value = DailyImageUiState.Success(apod)
                } ?: run {
                    Log.e("DailyImageViewModel", "無法解析回應資料")
                    _uiState.value = DailyImageUiState.Error("無法解析回應資料")
                }
            } else {
                val exception = result.exceptionOrNull()
                val errorMessage = exception?.message ?: "發生未知錯誤"
                Log.e("DailyImageViewModel", "獲取圖片失敗: $errorMessage", exception)
                _uiState.value = DailyImageUiState.Error(errorMessage)
            }
        }
    }
    
    /**
     * 重新載入圖片
     */
    fun refresh() {
        fetchTodayImage()
    }
}

/**
 * UI 狀態封裝類
 */
sealed class DailyImageUiState {
    object Loading : DailyImageUiState()
    data class Success(val apod: ApodResponse) : DailyImageUiState()
    data class Error(val message: String) : DailyImageUiState()
}

