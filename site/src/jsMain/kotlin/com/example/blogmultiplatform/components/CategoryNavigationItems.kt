package com.example.blogmultiplatform.components

import androidx.compose.runtime.Composable
import com.example.blogmultiplatform.models.Theme
import com.example.blogmultiplatform.navigation.Screen.SearchPage.searchByCategory
import com.example.blogmultiplatform.styles.CategoryItemStyle
import com.example.blogmultiplatform.util.Constants
import com.example.shared.Category
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.TextDecorationLine
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.fontFamily
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.onClick
import com.varabyte.kobweb.compose.ui.modifiers.textDecorationLine
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.core.PageContext
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.style.toModifier
import org.jetbrains.compose.web.css.px

@Composable
fun CategoryNavigationItems(
    context: PageContext,
    selectedCategory: Category? = null,
    vertical: Boolean = false,
    onMenuCloseArg: () -> Unit = {},
) {
    Category.entries.forEach { category ->
        Link(
            modifier = CategoryItemStyle.toModifier()
                .thenIf(
                    condition = vertical,
                    other = Modifier.margin(bottom = 24.px)
                )
                .thenIf(
                    condition = !vertical,
                    other = Modifier.margin(right = 24.px)
                )
                .thenIf(
                    condition = selectedCategory == category,
                    other = Modifier.color(Theme.Primary.rgb)
                )
                .fontFamily(Constants.FONT_FAMILY)
                .fontSize(16.px)
                .fontWeight(FontWeight.Medium)
                .textDecorationLine(TextDecorationLine.None)
                .onClick {
                    context.router.navigateTo(searchByCategory(category))
                    onMenuCloseArg()
                },
            path = "",
            text = category.name
        )
    }
}