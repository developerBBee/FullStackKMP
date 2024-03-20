package com.example.shared

import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.graphics.Color.Companion.rgb
import kotlinx.serialization.Serializable

@Serializable
actual enum class Category(override val color: String) : CategoryColor {
    Technology(color = JsTheme.Green.hex),
    Programming(color = JsTheme.Yellow.hex),
    Design(color = JsTheme.Purple.hex),
}

enum class JsTheme(
    val hex: String,
    val rgb: Color.Rgb,
) {
    Primary(
        hex = "#00A2FF",
        rgb = rgb(0,162,255)
    ),
    Secondary(
        hex = "#001019",
        rgb = rgb(0, 16, 25)
    ),
    Tertiary(
        hex = "#001925",
        rgb = rgb(0, 25, 37)
    ),
    LightGray(
        hex = "#FAFAFA",
        rgb = rgb(250,250,250)
    ),
//    HalfWhite(
//        hex = "#FFFFFF",
//        rgb = rgba(255, 255, 255, 0.5)
//    ),
//    HalfBlack(
//        hex = "#000000",
//        rgb = rgba(0, 0, 0, 0.5)
//    ),
    Gray(
        hex = "#E9E9E9",
        rgb = rgb(233, 233, 233)
    ),
    DarkGray(
        hex = "#646464",
        rgb = rgb(100, 100, 100)
    ),
    White(
        hex = "#FFFFFF",
        rgb = rgb(255, 255, 255)
    ),
    Green(
        hex = "#00FF94",
        rgb = rgb(0, 255, 148)
    ),
    Yellow(
        hex = "#FFEC45",
        rgb = rgb(255, 236, 69)
    ),
    Red(
        hex = "#FF6359",
        rgb = rgb(255, 99, 89)
    ),
    Purple(
        hex = "#8B6DFF",
        rgb = rgb(139, 109, 255)
    ),
    Sponsored(
        hex = "#3300FF",
        rgb = rgb(51, 1, 255)
    )
}