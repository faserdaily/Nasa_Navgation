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
fun DailyImageScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "每日推薦圖片",
            fontSize = AppTypography.Home.appNameTextSize,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    Nasa_NavgationTheme {
        HomeScreen()
    }
}

