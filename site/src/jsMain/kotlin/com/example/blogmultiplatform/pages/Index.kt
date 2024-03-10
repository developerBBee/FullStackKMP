package com.example.blogmultiplatform.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.example.blogmultiplatform.components.CategoryNavigationItems
import com.example.blogmultiplatform.components.OverflowSidePanel
import com.example.blogmultiplatform.models.ApiListResponse
import com.example.blogmultiplatform.models.Constants.POSTS_PER_PAGE
import com.example.blogmultiplatform.models.PostWithoutDetails
import com.example.blogmultiplatform.sections.HeaderSection
import com.example.blogmultiplatform.sections.MainSection
import com.example.blogmultiplatform.sections.NewsletterSection
import com.example.blogmultiplatform.sections.PostsSection
import com.example.blogmultiplatform.sections.SponsoredPostsSection
import com.example.blogmultiplatform.util.fetchLatestPosts
import com.example.blogmultiplatform.util.fetchMainPosts
import com.example.blogmultiplatform.util.fetchPopularPosts
import com.example.blogmultiplatform.util.fetchSponsoredPosts
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.theme.breakpoint.rememberBreakpoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Page
@Composable
fun HomePage() {
    val scope = rememberCoroutineScope()
    val breakpoint = rememberBreakpoint()
    val context = rememberPageContext()

    var overflowMenuOpened by remember { mutableStateOf(false) }

    var mainPosts by remember { mutableStateOf<ApiListResponse>(ApiListResponse.Idle) }
    val latestPosts = remember { mutableStateListOf<PostWithoutDetails>() }
    val sponsoredPosts = remember { mutableStateListOf<PostWithoutDetails>() }
    val popularPosts = remember { mutableStateListOf<PostWithoutDetails>() }

    var latestPostsToSkip by remember { mutableStateOf(0) }
    var popularPostsToSkip by remember { mutableStateOf(0) }
    var showMoreLatest by remember { mutableStateOf(false) }
    var showMorePopular by remember { mutableStateOf(false) }

    val onSuccessLatest: (ApiListResponse.Success) -> Unit = { response ->
        latestPosts.addAll(response.data)
        latestPostsToSkip += POSTS_PER_PAGE
        showMoreLatest = (response.data.size == POSTS_PER_PAGE)
    }

    val onSuccessSponsored: (ApiListResponse.Success) -> Unit = { response ->
        sponsoredPosts.addAll(response.data)
    }

    val onSuccessPopular: (ApiListResponse.Success) -> Unit = { response ->
        popularPosts.addAll(response.data)
        popularPostsToSkip += POSTS_PER_PAGE
        showMorePopular = (response.data.size == POSTS_PER_PAGE)
    }

    LaunchedEffect(Unit) {
        fetchMainPosts(
            onSuccess = { mainPosts = it },
            onError = { println(it.message) }
        )
        fetchLatestPosts(
            skip = latestPostsToSkip,
            onSuccess = { onSuccessLatest(it) },
            onError = { println(it.message) },
        )
        fetchSponsoredPosts(
            onSuccess = { onSuccessSponsored(it) },
            onError = { println(it.message) },
        )
        fetchPopularPosts(
            skip = popularPostsToSkip,
            onSuccess = { onSuccessPopular(it) },
            onError = { println(it.message) },
        )
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
                        delay(300)
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
        HeaderSection(
            context = context,
            breakpoint = breakpoint,
            onMenuClick = { overflowMenuOpened = true }
        )
        MainSection(breakpoint = breakpoint, posts = mainPosts)
        PostsSection(
            breakpoint = breakpoint,
            posts = latestPosts,
            title = "Latest Posts",
            showMoreVisibility = showMoreLatest,
            onShowMore = {
                scope.launch {
                    fetchLatestPosts(
                        skip = latestPostsToSkip,
                        onSuccess = { onSuccessLatest(it) },
                        onError = { println(it.message) },
                    )
                }
            },
            onClick = {}
        )
        SponsoredPostsSection(
            breakpoint = breakpoint,
            posts = sponsoredPosts,
            onClick = {}
        )
        PostsSection(
            breakpoint = breakpoint,
            posts = popularPosts,
            title = "Popular Posts",
            showMoreVisibility = showMorePopular,
            onShowMore = {
                scope.launch {
                    fetchPopularPosts(
                        skip = popularPostsToSkip,
                        onSuccess = { onSuccessPopular(it) },
                        onError = { println(it.message) },
                    )
                }
            },
            onClick = {}
        )
        NewsletterSection(breakpoint = breakpoint)
    }
}
