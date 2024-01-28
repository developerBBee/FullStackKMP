package com.example.blogmultiplatform.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.blogmultiplatform.components.LoadingIndicator
import com.example.blogmultiplatform.models.RandomJoke
import com.example.blogmultiplatform.navigation.Screen
import com.varabyte.kobweb.browser.http.http
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.rotate
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.animation.Keyframes
import kotlinx.browser.localStorage
import kotlinx.browser.window
import kotlinx.serialization.json.Json
import org.jetbrains.compose.web.css.deg
import org.w3c.dom.get
import org.w3c.dom.set
import kotlin.js.Date

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

fun logout() {
    localStorage["remember"] = "false"
    localStorage["userId"] = ""
    localStorage["username"] = ""
}

suspend fun getJoke(): RandomJoke = runCatching {
    window.http.get(KeyFile.HUMOR_API_URL).decodeToString()
        .let { result ->
            localStorage["date"] = Date.now().toString()
            localStorage["joke"] = result
            Json.decodeFromString<RandomJoke>(result)
        }
}.getOrElse { e ->
    println("getJoke() error ${e.message}")
    getLocalJoke()
}

fun getLocalJoke(): RandomJoke = runCatching {
    requireNotNull(
        localStorage["joke"]?.let { Json.decodeFromString<RandomJoke>(it) }
    )
}.getOrElse { e ->
    println("getLocalJoke() error ${e.message}")
    RandomJoke(id = -1, joke = "Unexpected Error.")
}