package com.Halza.Master.presentation.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.Colors


val EatingstartColor = Color(0xFFe76796)
val EatingEndColor = Color(0xFFCF6679)
val sweep = Brush.sweepGradient(listOf(EatingstartColor, EatingEndColor))
val HalzaPrimaryColor = Color(0xFF42BFB5)
val  fasingColor=Color(0xFFFFD700)
val fastingEndingColor=Color(0xFFFFA500)
internal val wearColorPalette: Colors = Colors(
    primary = HalzaPrimaryColor,
    primaryVariant = HalzaPrimaryColor,
    secondary = HalzaPrimaryColor,
    secondaryVariant = HalzaPrimaryColor,
    error = fasingColor,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onError = Color.Black,



    )