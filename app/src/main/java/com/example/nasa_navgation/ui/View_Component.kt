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

/**
 * 首頁畫面
 */
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = Constants.Images.NASA_LOGO),
            contentDescription = Constants.Images.NASA_LOGO_DESCRIPTION,
            modifier = Modifier
                .fillMaxWidth(Constants.UI.IMAGE_WIDTH_RATIO)
                .aspectRatio(Constants.UI.IMAGE_ASPECT_RATIO)
                .clickable {
                    Navigator.navigateToDailyImage(context)
                },
            contentScale = ContentScale.Fit
        )
        Text(
            text = Constants.Text.APP_NAME,
            fontSize = AppTypography.Home.appNameTextSize,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = Spacing.Home.textTopPadding)
        )
    }
}

/**
 * 每日推薦圖片畫面
 */
@Composable
fun DailyImageScreen(
    modifier: Modifier = Modifier,
    viewModel: DailyImageViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    when (val state = uiState) {
        is DailyImageUiState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        
        is DailyImageUiState.Success -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(Spacing.medium),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.medium)
            ) {
                // 標題
                Text(
                    text = state.apod.title,
                    fontSize = AppTypography.Home.appNameTextSize,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // 日期
                Text(
                    text = state.apod.date,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // 圖片
                AsyncImage(
                    model = state.apod.url,
                    contentDescription = state.apod.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(4f / 3f),
                    contentScale = ContentScale.Fit
                )
                
                // 說明文字
                Text(
                    text = state.apod.explanation,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        
        is DailyImageUiState.Error -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Spacing.medium)
                ) {
                    Text(
                        text = "載入失敗",
                        fontSize = AppTypography.Home.appNameTextSize,
                        fontWeight = FontWeight.Bold
                    )
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

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    Nasa_NavgationTheme {
        HomeScreen()
    }
}

