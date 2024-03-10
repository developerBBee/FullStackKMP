package com.example.blogmultiplatform.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.blogmultiplatform.models.Theme
import com.example.blogmultiplatform.util.Id
import com.example.blogmultiplatform.util.noBorder
import com.varabyte.kobweb.compose.css.CSSTransition
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.border
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.cursor
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.id
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.onClick
import com.varabyte.kobweb.compose.ui.modifiers.onFocusIn
import com.varabyte.kobweb.compose.ui.modifiers.onFocusOut
import com.varabyte.kobweb.compose.ui.modifiers.onKeyDown
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.transition
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.silk.components.forms.Input
import com.varabyte.kobweb.silk.components.icons.fa.FaMagnifyingGlass
import com.varabyte.kobweb.silk.components.icons.fa.IconSize
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.ms
import org.jetbrains.compose.web.css.px

@Composable
fun SearchBar(
    breakpoint: Breakpoint,
    modifier: Modifier = Modifier,
    fullWidth: Boolean = true,
    darkTheme: Boolean = false,
    initialSearchBarText: String = "",
    onSearchIconClick: (Boolean) -> Unit = {},
    onEnterClick: (String) -> Unit
) {
    var focused by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf(initialSearchBarText) }

    LaunchedEffect(breakpoint) {
        if (breakpoint >= Breakpoint.SM) onSearchIconClick(false)
    }
    LaunchedEffect(fullWidth) {
        if (breakpoint < Breakpoint.SM) {
            searchText = ""
        }
    }
    LaunchedEffect(initialSearchBarText) {
        searchText = initialSearchBarText
    }

    if (breakpoint >= Breakpoint.SM || fullWidth) {
        Row(
            modifier = modifier
                .thenIf(
                    condition = fullWidth,
                    other = Modifier.fillMaxWidth()
                )
                .padding(left = 20.px)
                .height(54.px)
                .backgroundColor(if (darkTheme) Theme.Tertiary.rgb else Theme.LightGray.rgb)
                .borderRadius(r = 100.px)
                .border(
                    width = 2.px,
                    style = LineStyle.Solid,
                    color = if (darkTheme) {
                        if (focused) Theme.Primary.rgb else Theme.Secondary.rgb
                    } else {
                        if (focused) Theme.Primary.rgb else Theme.LightGray.rgb
                    }
                )
                .transition(CSSTransition(property = "border", duration = 200.ms)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FaMagnifyingGlass(
                modifier = Modifier
                    .margin(right = 14.px)
                    .color(if (focused) Theme.Primary.rgb else Theme.DarkGray.rgb)
                    .transition(CSSTransition(property = "color", duration = 200.ms)),
                size = IconSize.SM
            )
            Input(
                modifier = Modifier
                    .id(Id.adminSearchBar)
                    .fillMaxSize()
                    .color(if (darkTheme) Colors.White else Colors.Black)
                    .backgroundColor(Colors.Transparent)
                    .noBorder()
                    .onFocusIn { focused = true }
                    .onFocusOut { focused = false }
                    .onKeyDown {
                        if (it.key == "Enter") {
                            onEnterClick(searchText)
                        }
                    },
                focusBorderColor = Colors.Transparent,
                type = InputType.Text,
                value = searchText,
                placeholder = "Search...",
                onValueChanged = { searchText = it }
            )
        }
    } else {
        FaMagnifyingGlass(
            modifier = Modifier
                .margin(right = 14.px)
                .color(Theme.Primary.rgb)
                .cursor(Cursor.Pointer)
                .onClick { onSearchIconClick(true) },
            size = IconSize.SM
        )
    }
}