package com.example.blogmultiplatform.pages.admin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.example.blogmultiplatform.components.AdminPageLayout
import com.example.blogmultiplatform.components.Posts
import com.example.blogmultiplatform.components.SearchBar
import com.example.blogmultiplatform.models.PostWithoutDetails
import com.example.blogmultiplatform.models.Theme
import com.example.blogmultiplatform.navigation.Screen
import com.example.blogmultiplatform.util.Constants.FONT_FAMILY
import com.example.blogmultiplatform.util.Constants.POST_PER_PAGE
import com.example.blogmultiplatform.util.Constants.QUERY_PARAM
import com.example.blogmultiplatform.util.Constants.SIDE_PANEL_WIDTH
import com.example.blogmultiplatform.util.deleteSelectedPosts
import com.example.blogmultiplatform.util.fetchMyPosts
import com.example.blogmultiplatform.util.isUserLoggedIn
import com.example.blogmultiplatform.util.noBorder
import com.example.blogmultiplatform.util.searchPostsByTitle
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.Visibility
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.fontFamily
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.visibility
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.forms.Switch
import com.varabyte.kobweb.silk.components.forms.SwitchSize
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.breakpoint.rememberBreakpoint
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px

@Page
@Composable
fun MyPostsPage() {
    isUserLoggedIn {
        MyPostsScreen()
    }
}

@Composable
fun MyPostsScreen() {
    val context = rememberPageContext()
    val scope = rememberCoroutineScope()
    val breakpoint = rememberBreakpoint()

    val myPosts = remember { mutableStateListOf<PostWithoutDetails>() }
    val selectedPosts = remember { mutableStateListOf<String>() }
    var postsToSkip by remember { mutableStateOf(0) }
    var showMoreVisibility by remember { mutableStateOf(false) }
    var selectable by remember { mutableStateOf(false) }

    val hasParams = remember(key1 = context.route) { context.route.params.containsKey(QUERY_PARAM) }
    var query = remember(key1 = context.route) {
        runCatching {
            context.route.params.getValue(QUERY_PARAM)
        }.getOrElse {
            ""
        }
    }

    LaunchedEffect(key1 = context.route) {
        postsToSkip = 0
        if (hasParams) {
            searchPostsByTitle(
                query = query,
                skip = postsToSkip,
                onSuccess = { apiListResponse ->
                    apiListResponse.data.also { data ->
                        myPosts.clear()
                        myPosts.addAll(data)
                        postsToSkip += POST_PER_PAGE
                        showMoreVisibility = data.size >= POST_PER_PAGE
                    }
                },
                onError = { throwable -> println(throwable.message) }
            )
        } else {
            fetchMyPosts(
                skip = postsToSkip,
                onSuccess = { apiListResponse ->
                    apiListResponse.data.also { data ->
                        myPosts.clear()
                        myPosts.addAll(data)
                        postsToSkip += POST_PER_PAGE
                        showMoreVisibility = data.size >= POST_PER_PAGE
                    }
                },
                onError = { throwable -> println(throwable.message) }
            )
        }
    }

    AdminPageLayout {
        Column(
            modifier = Modifier
                .margin(topBottom = 50.px)
                .fillMaxSize()
                .padding(left = if (breakpoint > Breakpoint.MD) SIDE_PANEL_WIDTH.px else 0.px),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(
                        if (breakpoint > Breakpoint.MD) 30.percent else 50.percent
                    )
                    .margin(bottom = 24.px),
                contentAlignment = Alignment.Center
            ) {
                SearchBar { searchText ->
                    context.router.navigateTo(
                        searchText.takeIf { it.isNotEmpty() }?.let { query ->
                            Screen.AdminMyPosts.searchByTitle(query = query)
                        } ?: Screen.AdminMyPosts.route
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(
                        if (breakpoint > Breakpoint.MD) 80.percent else 90.percent
                    )
                    .margin(bottom = 24.px),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        modifier = Modifier.margin(right = 8.px),
                        size = SwitchSize.LG,
                        checked = selectable,
                        onCheckedChange = {
                            selectable = it
                            if (!selectable) {
                                selectedPosts.clear()
                            }
                        }
                    )
                    SpanText(
                        modifier = Modifier
                            .color(if (selectable) Colors.Black else Theme.HalfBlack.rgb),
                        text = if (selectable) "${selectedPosts.size} Posts selected" else "Select"
                    )
                }
                Button(
                    modifier = Modifier
                        .margin(right = 20.px)
                        .height(54.px)
                        .padding(leftRight = 24.px)
                        .backgroundColor(Theme.Red.rgb)
                        .color(Colors.White)
                        .noBorder()
                        .borderRadius(r = 4.px)
                        .fontFamily(FONT_FAMILY)
                        .fontSize(14.px)
                        .visibility(
                            if (selectedPosts.isEmpty()) Visibility.Hidden else Visibility.Visible
                        )
                        .fontWeight(FontWeight.Medium),
                    onClick = {
                        scope.launch {
                            val result = deleteSelectedPosts(ids = selectedPosts)
                            if (result) {
                                selectable = false
                                postsToSkip -= selectedPosts.size
                                selectedPosts.forEach { deletedPostId ->
                                    myPosts.removeAll { it._id == deletedPostId }
                                }
                                selectedPosts.clear()
                            }
                        }
                    }
                ) {
                    SpanText("Delete")
                }
            }
            Posts(
                breakpoint = breakpoint,
                posts = myPosts,
                selectable = selectable,
                onSelect = { selectedPosts.add(it) },
                deSelect = { selectedPosts.remove(it) },
                showMoreVisibility = showMoreVisibility,
            ) {
                scope.launch {
                    if (hasParams) {
                        searchPostsByTitle(
                            query = query,
                            skip = postsToSkip,
                            onSuccess = { apiListResponse ->
                                apiListResponse.data.also { data ->
                                    if (data.isNotEmpty()) {
                                        myPosts.addAll(data)
                                        postsToSkip += POST_PER_PAGE
                                        showMoreVisibility = data.size >= POST_PER_PAGE
                                    } else {
                                        showMoreVisibility = false
                                    }
                                }
                            },
                            onError = { throwable -> println(throwable.message) }
                        )
                    } else {
                        fetchMyPosts(
                            skip = postsToSkip,
                            onSuccess = { apiListResponse ->
                                apiListResponse.data.also { data ->
                                    if (data.isNotEmpty()) {
                                        myPosts.addAll(data)
                                        postsToSkip += POST_PER_PAGE
                                        showMoreVisibility = data.size >= POST_PER_PAGE
                                    } else {
                                        showMoreVisibility = false
                                    }
                                }
                            },
                            onError = { throwable -> println(throwable.message) }
                        )
                    }
                }
            }
        }
    }
}