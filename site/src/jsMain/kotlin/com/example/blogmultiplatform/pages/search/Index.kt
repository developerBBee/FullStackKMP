package com.example.blogmultiplatform.pages.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.example.blogmultiplatform.components.CategoryNavigationItems
import com.example.blogmultiplatform.components.LoadingIndicator
import com.example.blogmultiplatform.components.OverflowSidePanel
import com.example.blogmultiplatform.models.ApiListResponse
import com.example.blogmultiplatform.models.Category
import com.example.blogmultiplatform.models.Constants
import com.example.blogmultiplatform.models.Constants.CATEGORY_PARAM
import com.example.blogmultiplatform.models.Constants.QUERY_PARAM
import com.example.blogmultiplatform.models.PostWithoutDetails
import com.example.blogmultiplatform.navigation.Screen
import com.example.blogmultiplatform.sections.HeaderSection
import com.example.blogmultiplatform.sections.PostsSection
import com.example.blogmultiplatform.util.Constants.FONT_FAMILY
import com.example.blogmultiplatform.util.Res
import com.example.blogmultiplatform.util.searchPostsByCategory
import com.example.blogmultiplatform.util.searchPostsByTitle
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.fontFamily
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.textAlign
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.breakpoint.rememberBreakpoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.css.px

@Page(routeOverride = "query")
@Composable
fun SearchPage() {
    val scope = rememberCoroutineScope()
    val breakpoint = rememberBreakpoint()
    val context = rememberPageContext()

    var apiResponse by remember { mutableStateOf<ApiListResponse>(ApiListResponse.Idle) }
    var overflowMenuOpened by remember { mutableStateOf(false) }

    val searchedPosts = remember { mutableStateListOf<PostWithoutDetails>() }
    var postsToSkip by remember { mutableStateOf(0) }
    var showMorePosts by remember { mutableStateOf(false) }
    var searchBarText by remember { mutableStateOf("") }

    val hasCategoryParam = remember(key1 = context.route) {
        context.route.params.containsKey(CATEGORY_PARAM)
    }

    val hasQueryParam = remember(key1 = context.route) {
        context.route.params.containsKey(QUERY_PARAM)
    }

    val value = remember(key1 = context.route) {
        if (hasCategoryParam) {
            context.route.params.getValue(CATEGORY_PARAM)
        } else if (hasQueryParam) {
            context.route.params.getValue(QUERY_PARAM)
        } else {
            ""
        }
    }

    val isLoading = apiResponse == ApiListResponse.Idle

    val selectedCategory = runCatching {
        Category.valueOf(value)
    }.getOrNull()

    val onSuccessSearch: (ApiListResponse.Success) -> Unit = { response ->
        apiResponse = response
        searchedPosts.addAll(response.data)
        postsToSkip += Constants.POSTS_PER_PAGE
        showMorePosts = (response.data.size == Constants.POSTS_PER_PAGE)
    }

    val onErrorSearch: (Throwable) -> Unit = { error ->
        (error.message ?: "")
            .also { message ->
                apiResponse = ApiListResponse.Error(message)
                println(message)
            }
    }

    LaunchedEffect(key1 = context.route) {
        postsToSkip = 0
        searchBarText = ""
        if (hasCategoryParam && selectedCategory != null) {
            apiResponse = ApiListResponse.Idle
            searchPostsByCategory(
                category = selectedCategory,
                skip = postsToSkip,
                onSuccess = {
                    searchedPosts.clear()
                    onSuccessSearch(it)
                },
                onError = { onErrorSearch(it) },
            )
        } else if (hasQueryParam) {
            apiResponse = ApiListResponse.Idle
            searchBarText = value
            searchPostsByTitle(
                query = value,
                skip = postsToSkip,
                onSuccess = {
                    searchedPosts.clear()
                    onSuccessSearch(it)
                },
                onError = { onErrorSearch(it) },
            )
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
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
                onClickHome = { context.router.navigateTo(Screen.HomePage.route) },
            ) {
                CategoryNavigationItems(
                    context = context,
                    selectedCategory = selectedCategory,
                    vertical = true,
                    onMenuCloseArg = { overflowMenuOpened = false },
                )
            }
        }
        HeaderSection(
            context = context,
            breakpoint = breakpoint,
            selectedCategory = selectedCategory,
            initialSearchBarText = searchBarText,
            logoHome = Res.Image.logo,
            onMenuClick = { overflowMenuOpened = true }
        )
        if (selectedCategory != null) {
            SpanText(
                modifier = Modifier
                    .fillMaxWidth()
                    .textAlign(TextAlign.Center)
                    .margin(top = 100.px, bottom = 40.px)
                    .fontFamily(FONT_FAMILY)
                    .fontSize(36.px),
                text = value
            )
            PostsSection(
                breakpoint = breakpoint,
                posts = searchedPosts,
                showMoreVisibility = showMorePosts && !isLoading,
                onShowMore = {
                    scope.launch {
                        apiResponse = ApiListResponse.Idle
                        searchPostsByCategory(
                            category = selectedCategory,
                            skip = postsToSkip,
                            onSuccess = { onSuccessSearch(it) },
                            onError = { onErrorSearch(it) },
                        )
                    }
                },
                onClick = { },
            )
        } else if (hasQueryParam) {
            PostsSection(
                breakpoint = breakpoint,
                posts = searchedPosts,
                showMoreVisibility = showMorePosts && !isLoading,
                onShowMore = {
                    scope.launch {
                        apiResponse = ApiListResponse.Idle
                        searchPostsByTitle(
                            query = value,
                            skip = postsToSkip,
                            onSuccess = { onSuccessSearch(it) },
                            onError = { onErrorSearch(it) },
                        )
                    }
                },
                onClick = { },
            )
        }
        when (val response = apiResponse) {
            ApiListResponse.Idle -> {
                LoadingIndicator()
            }

            is ApiListResponse.Error -> {
                SpanText(
                    modifier = Modifier
                        .fillMaxWidth()
                        .textAlign(TextAlign.Center)
                        .margin(top = 100.px, bottom = 40.px)
                        .fontFamily(FONT_FAMILY)
                        .fontSize(36.px),
                    text = response.message
                )
            }

            else -> {}
        }
    }
}