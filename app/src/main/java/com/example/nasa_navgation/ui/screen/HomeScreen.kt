package com.example.nasa_navgation.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.nasa_navgation.Constants
import com.example.nasa_navgation.Navigator
import com.example.nasa_navgation.ui.theme.AppTypography
import com.example.nasa_navgation.ui.theme.Nasa_NavgationTheme
import com.example.nasa_navgation.ui.theme.Spacing

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
                .fillMaxWidth(Constants.UI.IMAGE_WIDTH_RATIO)
                .aspectRatio(Constants.UI.IMAGE_ASPECT_RATIO)
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

// 首頁畫面的預覽函數
// 用於 Android Studio 的 Compose 預覽功能，可以即時預覽 UI
// 無需運行應用程式就能看到 UI 效果
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    // 應用主題以獲得完整的預覽效果
    Nasa_NavgationTheme {
        HomeScreen()
    }
}

