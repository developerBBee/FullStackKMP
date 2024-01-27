package com.example.blogmultiplatform.models

import org.jetbrains.compose.web.css.CSSColorValue
import org.jetbrains.compose.web.css.rgb
import org.jetbrains.compose.web.css.rgba

enum class Theme(
    val hex: String,
    val rgb: CSSColorValue,
) {
    Primary(
        hex = "#00A2FF",
        rgb = rgb(0,162,255)
    ),
    Secondary(
        hex = "#001019",
        rgb = rgb(0, 16, 25)
    ),
    LightGray(
        hex = "#FAFAFA",
        rgb = rgb(250,250,250)
    ),
    HalfWhite(
        hex = "#FFFFFF",
        rgb = rgba(255, 255, 255, 0.5)
    ),
    White(
        hex = "#FFFFFF",
        rgb = rgb(255, 255, 255)
    )
}