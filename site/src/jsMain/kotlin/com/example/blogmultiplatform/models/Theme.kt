package com.example.blogmultiplatform.models

import org.jetbrains.compose.web.css.CSSColorValue
import org.jetbrains.compose.web.css.rgb

enum class Theme(
    val hex: String,
    val rgb: CSSColorValue,
) {
    Primary(
        hex = "#00A2FF",
        rgb = rgb(0,162,255)
    ),
    LightGray(
        hex = "#FAFAFA",
        rgb = rgb(250,250,250)
    ),
}