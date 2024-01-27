package com.example.blogmultiplatform.pages.admin

import androidx.compose.runtime.Composable
import com.example.blogmultiplatform.util.isUserLoggedIn
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.silk.components.text.SpanText

@Page
@Composable
fun HomeScreen() {
    isUserLoggedIn() {
        HomePage()
    }
}

@Composable
fun HomePage() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        SpanText(text = "hello")
    }
}