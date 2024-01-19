package com.tabka.backblogapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.tabka.backblogapp.R

object AppFont {
    val Inter = FontFamily(
        Font(R.font.inter_regular),
        Font(R.font.inter_black, FontWeight.Black),
        Font(R.font.inter_extralight, FontWeight.ExtraLight),
        Font(R.font.inter_light, FontWeight.Light),
        Font(R.font.inter_thin, FontWeight.Thin),
        Font(R.font.inter_medium, FontWeight.Medium),
        Font(R.font.inter_semibold, FontWeight.SemiBold),
        Font(R.font.inter_bold, FontWeight.Bold),
        Font(R.font.inter_extrabold, FontWeight.ExtraBold)
    )
}

// Set of Material typography styles to start with
private val typography = Typography()
val Typography = Typography(
    displayLarge = typography.displayLarge.copy(fontFamily = AppFont.Inter, color = Color.White),
    displayMedium = typography.displayMedium.copy(fontFamily = AppFont.Inter, color = Color.White),
    displaySmall = typography.displaySmall.copy(fontFamily = AppFont.Inter, color = Color.White),

    headlineLarge = typography.headlineLarge.copy(fontFamily = AppFont.Inter, color = Color.White, fontWeight = FontWeight.Bold),
    headlineMedium = typography.headlineMedium.copy(fontFamily = AppFont.Inter, color = Color.White, fontWeight = FontWeight.Bold),
    headlineSmall = typography.headlineSmall.copy(fontFamily = AppFont.Inter, color = Color.White, fontWeight = FontWeight.Bold),

    titleLarge = typography.titleLarge.copy(fontFamily = AppFont.Inter, color = Color.White),
    titleMedium = typography.titleMedium.copy(fontFamily = AppFont.Inter, color = Color.White),
    titleSmall = typography.titleSmall.copy(fontFamily = AppFont.Inter, color = Color.White),

    bodyLarge = typography.bodyLarge.copy(fontFamily = AppFont.Inter, color = Color.White),
    bodyMedium = typography.bodyMedium.copy(fontFamily = AppFont.Inter, color = Color.White),
    bodySmall = typography.bodySmall.copy(fontFamily = AppFont.Inter, color = Color.White),

    labelLarge = typography.labelLarge.copy(fontFamily = AppFont.Inter, color = Color.White),
    labelMedium = typography.labelMedium.copy(fontFamily = AppFont.Inter, color = Color.White),
    labelSmall = typography.labelSmall.copy(fontFamily = AppFont.Inter, color = Color.White)
)