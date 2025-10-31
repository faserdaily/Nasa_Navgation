package com.example.nasa_navgation.model

/**
 * NASA APOD (Astronomy Picture of the Day) API 響應模型
 */
data class ApodResponse(
    val date: String,
    val explanation: String,
    val hdurl: String?,
    val media_type: String,
    val service_version: String,
    val title: String,
    val url: String
)

