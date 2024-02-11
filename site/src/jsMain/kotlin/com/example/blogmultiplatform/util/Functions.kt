package com.example.blogmultiplatform.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.blogmultiplatform.components.LoadingIndicator
import com.example.blogmultiplatform.models.ControlStyle
import com.example.blogmultiplatform.models.EditorControl
import com.example.blogmultiplatform.models.Post
import com.example.blogmultiplatform.models.RandomJoke
import com.example.blogmultiplatform.navigation.Screen
import com.varabyte.kobweb.browser.api
import com.varabyte.kobweb.browser.http.http
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.border
import com.varabyte.kobweb.compose.ui.modifiers.outline
import com.varabyte.kobweb.compose.ui.modifiers.rotate
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.animation.Keyframes
import kotlinx.browser.document
import kotlinx.browser.localStorage
import kotlinx.browser.window
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.deg
import org.jetbrains.compose.web.css.px
import org.w3c.dom.HTMLTextAreaElement
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

fun Modifier.noBorder(): Modifier = this
    .border(
        width = 0.px,
        style = LineStyle.None,
        color = Colors.Transparent
    )
    .outline(
        width = 0.px,
        style = LineStyle.None,
        color = Colors.Transparent
)

suspend fun addPost(post: Post): Boolean {
    return runCatching {
        window.api.tryPost(
            apiPath = "addpost",
            body = Json.encodeToString(post).encodeToByteArray()
        )?.decodeToString().toBoolean()
    }.getOrElse { e ->
        println(e.message)
        false
    }
}

fun getEditor() = document.getElementById(Id.editor) as HTMLTextAreaElement

private fun HTMLTextAreaElement.getSelectedIntRange(): IntRange? =
    selectionStart?.let { start ->
        selectionEnd?.let { end ->
            IntRange(start, end - 1)
        }
    }

fun getSelectedText() = getEditor().run {
    getSelectedIntRange()?.let { range ->
        value.substring(range)
    }
}

fun applyStyle(controlStyle: ControlStyle) = getEditor().apply {
    getSelectedText()?.also {
        getSelectedIntRange()?.also { selectedIntRange ->
            value = value.replaceRange(
                range = selectedIntRange,
                replacement = controlStyle.style
            )
            document.getElementById(Id.editorPreview)?.innerHTML = value
        }
    }
}

fun applyControlStyle(
    editorControl: EditorControl,
    onLinkClick: () -> Unit,
) {
    when (editorControl) {
        EditorControl.Bold -> applyStyle(ControlStyle.Bold(getSelectedText()))
        EditorControl.Italic -> applyStyle(ControlStyle.Italic(getSelectedText()))
        EditorControl.Link -> {
            onLinkClick()
        }
        EditorControl.Title -> applyStyle(ControlStyle.Title(getSelectedText()))
        EditorControl.Subtitle -> applyStyle(ControlStyle.Subtitle(getSelectedText()))
        EditorControl.Quote -> applyStyle(ControlStyle.Quote(getSelectedText()))
        EditorControl.Code -> applyStyle(ControlStyle.Code(getSelectedText()))
        EditorControl.Image -> {}
    }
}