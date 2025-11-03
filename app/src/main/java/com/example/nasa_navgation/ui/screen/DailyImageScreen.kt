package com.example.nasa_navgation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nasa_navgation.Constants
import coil.compose.AsyncImage
import com.example.nasa_navgation.ui.theme.AppTypography
import com.example.nasa_navgation.ui.theme.Nasa_NavgationTheme
import com.example.nasa_navgation.ui.theme.Spacing
import com.example.nasa_navgation.viewmodel.DailyImageUiState
import com.example.nasa_navgation.viewmodel.DailyImageViewModel

// 每日推薦圖片畫面
// 這是顯示 NASA 每日天文圖片的主要畫面，包含：
// 1. 圖片標題
// 2. 日期
// 3. 天文圖片（使用 Coil 異步載入）
// 4. 詳細說明文字
// 狀態管理：
// - 使用 ViewModel 管理狀態和業務邏輯
// - 透過 StateFlow 觀察狀態變化
// - 根據不同狀態顯示不同的 UI（載入中/成功/錯誤）
// @param modifier 用於調整布局的 Modifier
// @param viewModel ViewModel 實例，預設使用 viewModel() 委託自動創建
@Composable
fun DailyImageScreen(
    modifier: Modifier = Modifier,
    viewModel: DailyImageViewModel = viewModel()  // 使用 viewModel() 委託自動創建 ViewModel
) {
    // 收集 UI 狀態，當狀態變化時會自動重組（recompose）UI
    val uiState by viewModel.uiState.collectAsState()
    
    // 根據不同的狀態顯示對應的 UI
    when (val state = uiState) {
        // 載入中狀態：顯示載入指示器
        is DailyImageUiState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Material Design 的圓形載入指示器
                CircularProgressIndicator()
            }
        }
        
        // 成功狀態：顯示圖片和相關資訊
        is DailyImageUiState.Success -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())  // 允許垂直滾動
                    .padding(Spacing.medium),               // 添加內邊距
                horizontalAlignment = Alignment.CenterHorizontally,  // 水平居中
                verticalArrangement = Arrangement.spacedBy(Spacing.medium)  // 子元素間距
            ) {
                // 圖片標題
                Text(
                    text = state.apod.title,
                    fontSize = AppTypography.Home.appNameTextSize,
                    fontWeight = FontWeight.Bold,  // 粗體
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // 圖片日期
                Text(
                    text = state.apod.date,
                    fontSize = Constants.UIValues.TEXT_SIZE_MEDIUM.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // 天文圖片
                // 使用 AsyncImage（Coil 庫）異步載入網路圖片
                AsyncImage(
                    model = state.apod.url,              // 圖片 URL
                    contentDescription = state.apod.title,  // 無障礙描述
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(Constants.UIValues.IMAGE_ASPECT_RATIO_4_3),  // 4:3 的長寬比
                    contentScale = ContentScale.Fit  // 保持比例，適應容器
                )
                
                // 詳細說明文字
                Text(
                    text = state.apod.explanation,
                    fontSize = Constants.UIValues.TEXT_SIZE_SMALL.sp,
                    textAlign = TextAlign.Justify,  // 兩端對齊
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        
        // 錯誤狀態：顯示錯誤訊息
        is DailyImageUiState.Error -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Spacing.medium)
                ) {
                    // 錯誤標題
                    Text(
                        text = Constants.Text.LOAD_FAILED,
                        fontSize = AppTypography.Home.appNameTextSize,
                        fontWeight = FontWeight.Bold
                    )
                    // 具體錯誤訊息
                    Text(
                        text = state.message,
                        fontSize = Constants.UIValues.TEXT_SIZE_SMALL.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

// 每日圖片畫面的預覽函數
@Preview(showBackground = true)
@Composable
fun DailyImageScreenPreview() {
    Nasa_NavgationTheme {
        // 注意：預覽需要 ViewModel，這裡可以顯示載入狀態
        // 或者可以創建一個測試用的 ViewModel
    }
}

