package com.example.blogmultiplatform.pages.posts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.example.shared.Constants.SHOW_SECTIONS_PARAM
import com.example.blogmultiplatform.components.CategoryNavigationItems
import com.example.blogmultiplatform.components.ErrorView
import com.example.blogmultiplatform.components.LoadingIndicator
import com.example.blogmultiplatform.components.OverflowSidePanel
import com.example.blogmultiplatform.models.ApiResponse
import com.example.blogmultiplatform.models.Constants.POST_ID_PARAM
import com.example.blogmultiplatform.models.Post
import com.example.blogmultiplatform.models.Theme
import com.example.blogmultiplatform.sections.FooterSection
import com.example.blogmultiplatform.sections.HeaderSection
import com.example.blogmultiplatform.util.Constants.FONT_FAMILY
import com.example.blogmultiplatform.util.Id
import com.example.blogmultiplatform.util.Res
import com.example.blogmultiplatform.util.fetchSelectedPost
import com.example.blogmultiplatform.util.parseDateString
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.css.TextOverflow
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.fontFamily
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.id
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import com.varabyte.kobweb.compose.ui.modifiers.overflow
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.textOverflow
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.graphics.Image
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.breakpoint.rememberBreakpoint
import kotlinx.browser.document
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.HTMLDivElement
import kotlin.time.Duration.Companion.milliseconds

@Page(routeOverride = "post")
@Composable
fun PostPage() {
    val scope = rememberCoroutineScope()
    val breakpoint = rememberBreakpoint()
    val context = rememberPageContext()

    var overflowMenuOpened by remember { mutableStateOf(false) }
    var showSections by remember { mutableStateOf(true) }

    var apiResponse by remember { mutableStateOf<ApiResponse>(ApiResponse.Idle) }
    val hasPostIdParam = remember(key1 = context.route) {
        context.route.params.containsKey(POST_ID_PARAM)
    }

    LaunchedEffect(key1 = context.route) {
        if (context.route.params.containsKey(SHOW_SECTIONS_PARAM)) {
            context.route.params.getValue(SHOW_SECTIONS_PARAM)
                .split("=")
                .last()
                .toBoolean()
                .also { showSections = it }
        }

        if (hasPostIdParam) {
            val postId = context.route.params.getValue(POST_ID_PARAM)
            apiResponse = fetchSelectedPost(id = postId)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (overflowMenuOpened) {
            OverflowSidePanel(
                onMenuCloseArg = {
                    scope.launch {
                        delay(300.milliseconds)
                        overflowMenuOpened = false
                    }
                },
            ) {
                CategoryNavigationItems(
                    context = context,
                    vertical = true,
                    onMenuCloseArg = { overflowMenuOpened = false },
                )
            }
        }
        if (showSections) {
            HeaderSection(
                context = context,
                breakpoint = breakpoint,
                logoHome = Res.Image.logo,
                onMenuClick = { overflowMenuOpened = true },
            )
        }
        when (val response = apiResponse) {
            is ApiResponse.Success -> {
                PostContent(
                    post = response.data,
                    breakpoint = breakpoint,
                )
                scope.launch {
                    delay(50.milliseconds)
                    kotlin.runCatching {
                        js("hljs.highlightAll()") as Unit
                    }.onFailure { e ->
                        println(e.message)
                    }
                }
            }
            is ApiResponse.Error -> {
                ErrorView(message = response.message)
            }
            ApiResponse.Idle -> {
                LoadingIndicator()
            }
        }
        if (showSections) {
            FooterSection()
        }
    }
}

@Composable
fun PostContent(
    post: Post,
    breakpoint: Breakpoint,
) {
    LaunchedEffect(key1 = post) {
        (document.getElementById(Id.postContent) as HTMLDivElement).innerHTML = post.content
    }

    Column(
        modifier = Modifier
            .margin(top = 50.px, bottom = 200.px)
            .padding(leftRight = 24.px)
            .fillMaxWidth()
            .maxWidth(800.px),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SpanText(
            modifier = Modifier
                .fillMaxWidth()
                .color(Theme.HalfBlack.rgb)
                .fontFamily(FONT_FAMILY)
                .fontSize(14.px),
            text = post.date.parseDateString(),
        )
        SpanText(
            modifier = Modifier
                .fillMaxWidth()
                .margin(bottom = 20.px)
                .color(Colors.Black)
                .fontFamily(FONT_FAMILY)
                .fontSize(40.px)
                .fontWeight(FontWeight.Bold)
                .textOverflow(TextOverflow.Ellipsis)
                .overflow(Overflow.Hidden)
                .styleModifier {
                    property("display", "-webkit-box")
                    property("-webkit-line-clamp", "2")
                    property("line-clamp", "2")
                    property("-webkit-box-orient", "vertical")
                },
            text = post.title,
        )
        Image(
            modifier = Modifier
                .margin(bottom = 40.px)
                .fillMaxWidth()
                .height(
                    if (breakpoint <= Breakpoint.SM) 250.px
                    else if (breakpoint <= Breakpoint.MD) 400.px
                    else 600.px
                ),
            src = post.thumbnail,
        )
        Div(
            attrs = Modifier
                .id(Id.postContent)
                .fontFamily(FONT_FAMILY)
                .fillMaxWidth()
                .toAttrs(),
        )
    }
}