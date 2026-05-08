package com.example.disneycast.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.disneycast.R

private val DisneyDisplayFontFamily = FontFamily(
    Font(
        resId = R.font.cormorant_garamond,
        weight = FontWeight.Normal
    ),
    Font(
        resId = R.font.cormorant_garamond,
        weight = FontWeight.SemiBold
    ),
    Font(
        resId = R.font.cormorant_garamond,
        weight = FontWeight.Bold
    )
)

val Typography = Typography(
    headlineLarge = TextStyle(
        fontFamily = DisneyDisplayFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 34.sp,
        lineHeight = 42.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = DisneyDisplayFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
        lineHeight = 42.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = DisneyDisplayFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        letterSpacing = 0.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)
