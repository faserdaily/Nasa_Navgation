package com.example.nasa_navgation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.nasa_navgation.Constants
import com.example.nasa_navgation.Navigator
import com.example.nasa_navgation.ui.theme.AppTypography
import com.example.nasa_navgation.ui.theme.Nasa_NavgationTheme
import com.example.nasa_navgation.ui.theme.Spacing
import com.example.nasa_navgation.viewmodel.DailyImageUiState
import com.example.nasa_navgation.viewmodel.DailyImageViewModel

// 首頁畫面
// 這是應用程式的主入口畫面，顯示：
// 1. NASA 標誌圖片（可點擊）
// 2. 應用程式名稱
// 互動行為：
// - 點擊 NASA Logo 會導航到每日推薦圖片頁面
// @param modifier 用於調整此 Composable 的布局和行為的 Modifier
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    // 獲取當前的 Context，用於導航等操作
    val context = LocalContext.current
    
    // 使用 Column 垂直排列子元素
    Column(
        modifier = modifier.fillMaxSize(),                    // 填滿整個可用空間
        horizontalAlignment = Alignment.CenterHorizontally,  // 水平居中
        verticalArrangement = Arrangement.Center             // 垂直居中
    ) {
        // NASA Logo 圖片
        Image(
            painter = painterResource(id = Constants.Images.NASA_LOGO),
            contentDescription = Constants.Images.NASA_LOGO_DESCRIPTION,  // 無障礙描述
            modifier = Modifier
                .fillMaxWidth(Constants.UI.IMAGE_WIDTH_RATIO)  // 寬度為螢幕的 86%
                .aspectRatio(Constants.UI.IMAGE_ASPECT_RATIO)   // 長寬比 1:1（正方形）
                .clickable {  // 添加點擊事件
                    // 點擊後導航到每日圖片頁面
                    Navigator.navigateToDailyImage(context)
                },
            contentScale = ContentScale.Fit  // 保持圖片比例，適應容器大小
        )
        
        // 應用程式名稱文字
        Text(
            text = Constants.Text.APP_NAME,
            fontSize = AppTypography.Home.appNameTextSize,   // 使用主題中定義的文字大小
            textAlign = TextAlign.Center,                    // 文字居中對齊
            modifier = Modifier.padding(top = Spacing.Home.textTopPadding)  // 添加上邊距
        )
    }
}

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
                    fontSize = 16.sp,
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
                        .aspectRatio(4f / 3f),  // 4:3 的長寬比
                    contentScale = ContentScale.Fit  // 保持比例，適應容器
                )
                
                // 詳細說明文字
                Text(
                    text = state.apod.explanation,
                    fontSize = 14.sp,
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
                        text = "載入失敗",
                        fontSize = AppTypography.Home.appNameTextSize,
                        fontWeight = FontWeight.Bold
                    )
                    // 具體錯誤訊息
                    Text(
                        text = state.message,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

// 首頁畫面的預覽函數
// 用於 Android Studio 的 Compose 預覽功能，可以即時預覽 UI
// 無需運行應用程式就能看到 UI 效果
// @param showBackground 是否顯示背景（用於更好地觀察 UI）
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    // 應用主題以獲得完整的預覽效果
    Nasa_NavgationTheme {
        HomeScreen()
    }
}

