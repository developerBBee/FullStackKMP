package com.example.blogmultiplatform.sections

import androidx.compose.runtime.Composable
import com.example.blogmultiplatform.components.PostPreview
import com.example.blogmultiplatform.models.ApiListResponse
import com.example.blogmultiplatform.models.PostWithoutDetails
import com.example.blogmultiplatform.models.Theme
import com.example.blogmultiplatform.util.Constants.PAGE_WIDTH
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px

@Composable
fun MainSection(
    breakpoint: Breakpoint,
    posts: ApiListResponse,
    onClick: (String) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .backgroundColor(Theme.Secondary.rgb),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .maxWidth(PAGE_WIDTH.px)
                .backgroundColor(Theme.Secondary.rgb),
            contentAlignment = Alignment.Center
        ) {
            when (posts) {
                ApiListResponse.Idle -> {}
                is ApiListResponse.Success -> {
                    MainPosts(breakpoint = breakpoint, posts = posts.data, onClick = onClick)
                }
                is ApiListResponse.Error -> {}
            }
        }
    }
}

@Composable
fun MainPosts(
    breakpoint: Breakpoint,
    posts: List<PostWithoutDetails>,
    onClick: (String) -> Unit,
) {
    if (posts.isEmpty()) return

    Row(
        modifier = Modifier
            .fillMaxWidth(if (breakpoint > Breakpoint.MD) 80.percent else 90.percent)
            .margin(topBottom = 50.px)
    ) {
        if (breakpoint == Breakpoint.XL) {
            PostPreview(
                post = posts.first(),
                darkTheme = true,
                thumbnailHeight = 640.px,
                onClick = onClick,
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth(80.percent)
                    .margin(left = 20.px)
            ) {
                posts.drop(1).forEach {
                    PostPreview(
                        modifier = Modifier.margin(bottom = 20.px),
                        post = it,
                        darkTheme = true,
                        vertical = false,
                        thumbnailHeight = 200.px,
                        titleMaxLines = 1,
                        onClick = onClick,
                    )
                }
            }
        } else if (breakpoint >= Breakpoint.LG && posts.size > 1) {
            PostPreview(
                modifier = Modifier.margin(right = 10.px),
                post = posts.first(),
                darkTheme = true,
                onClick = onClick,
            )
            PostPreview(
                modifier = Modifier.margin(left = 10.px),
                post = posts[1],
                darkTheme = true,
                onClick = onClick,
            )
        } else {
            PostPreview(
                post = posts.first(),
                darkTheme = true,
                thumbnailHeight = 640.px,
                onClick = onClick,
            )
        }
    }
}