package com.example.blogmultiplatform.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.blogmultiplatform.navigation.Screen
import com.varabyte.kobweb.compose.css.AnimationIterationCount
import com.varabyte.kobweb.compose.css.functions.linearGradient
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.animation
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.backgroundImage
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.rotate
import com.varabyte.kobweb.compose.ui.modifiers.width
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.animation.Keyframes
import com.varabyte.kobweb.silk.components.animation.toAnimation
import kotlinx.browser.localStorage
import org.jetbrains.compose.web.css.AnimationTimingFunction
import org.jetbrains.compose.web.css.deg
import org.jetbrains.compose.web.css.ms
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.w3c.dom.get
import org.w3c.dom.set

val SpinKeyframes by Keyframes {
    from { Modifier.rotate(0.deg) }
    to { Modifier.rotate(360.deg) }
}

@Composable
fun isUserLoggedIn(content: @Composable () -> Unit) {
    val context = rememberPageContext()
    val remembered = remember { localStorage["remember"].toBoolean() }
    val userId = remember { localStorage["userId"] }
    var userIdExists by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        userIdExists = if (!userId.isNullOrEmpty()) checkUserId(userId) else false

        if (!(remembered && userIdExists)) {
            context.router.navigateTo(Screen.AdminLogin.route)
        }
    }

    if (remembered && userIdExists) {
        content()
    } else {
        LoadingIndicator()
        println("Loading...")
    }
}

@Composable
fun LoadingIndicator() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .width(40.px)
                .height(40.px)
                .padding(2.px)
                .borderRadius(50.percent)
                .backgroundImage(
                    linearGradient(
                        from = Colors.Aqua,
                        to = Colors.White
                    )
                )
                .animation(
                    SpinKeyframes.toAnimation(
                        duration = 1000.ms,
                        timingFunction = AnimationTimingFunction.Linear,
                        iterationCount = AnimationIterationCount.Infinite,
                    )
                )
        ) {
            Box(
                modifier = Modifier
                    .width(100.percent)
                    .height(100.percent)
                    .backgroundColor(color = Colors.White)
                    .borderRadius(50.percent)
            )
        }
    }
}

fun logout() {
    localStorage["remember"] = "false"
    localStorage["userId"] = ""
    localStorage["username"] = ""
}