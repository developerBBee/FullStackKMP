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
import com.example.blogmultiplatform.components.PostsView
import com.example.blogmultiplatform.components.SearchBar
import com.example.blogmultiplatform.models.Constants.POSTS_PER_PAGE
import com.example.blogmultiplatform.models.Constants.QUERY_PARAM
import com.example.blogmultiplatform.models.PostWithoutDetails
import com.example.blogmultiplatform.models.Theme
import com.example.blogmultiplatform.navigation.Screen
import com.example.blogmultiplatform.util.Constants.FONT_FAMILY
import com.example.blogmultiplatform.util.Constants.SIDE_PANEL_WIDTH
import com.example.blogmultiplatform.util.Id
import com.example.blogmultiplatform.util.deleteSelectedPosts
import com.example.blogmultiplatform.util.fetchMyPosts
import com.example.blogmultiplatform.util.isUserLoggedIn
import com.example.blogmultiplatform.util.noBorder
import com.example.blogmultiplatform.util.searchPostsByTitle
import com.varabyte.kobweb.compose.css.CSSTransition
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.TransitionProperty
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
import com.varabyte.kobweb.compose.ui.modifiers.transition
import com.varabyte.kobweb.compose.ui.modifiers.visibility
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.forms.Switch
import com.varabyte.kobweb.silk.components.forms.SwitchSize
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.breakpoint.rememberBreakpoint
import kotlinx.browser.document
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.css.ms
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.HTMLInputElement

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
    var selectableMode by remember { mutableStateOf(false) }

    val hasParams = remember(key1 = context.route) { context.route.params.containsKey(QUERY_PARAM) }
    val query = remember(key1 = context.route) { context.route.params[QUERY_PARAM] ?: "" }

    LaunchedEffect(key1 = context.route) {
        postsToSkip = 0
        if (hasParams) {
            renderComposable(rootElementId = "root") {
                val decodeURIComponent: dynamic = js("decodeURIComponent")
                val decoded = decodeURIComponent(query).toString()
                (document.getElementById(Id.adminSearchBar) as HTMLInputElement).value = decoded
            }
            searchPostsByTitle(
                query = query,
                skip = postsToSkip,
                onSuccess = { apiListResponse ->
                    apiListResponse.data.also { data ->
                        myPosts.clear()
                        myPosts.addAll(data)
                        postsToSkip += POSTS_PER_PAGE
                        showMoreVisibility = data.size >= POSTS_PER_PAGE
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
                        postsToSkip += POSTS_PER_PAGE
                        showMoreVisibility = data.size >= POSTS_PER_PAGE
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
                SearchBar(
                    breakpoint = breakpoint,
                    modifier = Modifier
                        .visibility(if (selectableMode) Visibility.Hidden else Visibility.Visible)
                        .transition(
                            CSSTransition(property = TransitionProperty.All, duration = 200.ms)
                        )
                ) { searchText ->
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
                        checked = selectableMode,
                        onCheckedChange = {
                            selectableMode = it
                            if (!selectableMode) {
                                selectedPosts.clear()
                            }
                        }
                    )
                    SpanText(
                        modifier = Modifier
                            .color(if (selectableMode) Colors.Black else Theme.HalfBlack.rgb),
                        text = if (selectableMode) "${selectedPosts.size} Posts selected" else "Select"
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
                                selectableMode = false
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
            PostsView(
                breakpoint = breakpoint,
                posts = myPosts,
                selectableMode = selectableMode,
                onSelect = { selectedPosts.add(it) },
                deSelect = { selectedPosts.remove(it) },
                showMoreVisibility = showMoreVisibility,
                onShowMore = {
                    scope.launch {
                        if (hasParams) {
                            searchPostsByTitle(
                                query = query,
                                skip = postsToSkip,
                                onSuccess = { apiListResponse ->
                                    apiListResponse.data.also { data ->
                                        if (data.isNotEmpty()) {
                                            myPosts.addAll(data)
                                            postsToSkip += POSTS_PER_PAGE
                                            showMoreVisibility = data.size >= POSTS_PER_PAGE
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
                                            postsToSkip += POSTS_PER_PAGE
                                            showMoreVisibility = data.size >= POSTS_PER_PAGE
                                        } else {
                                            showMoreVisibility = false
                                        }
                                    }
                                },
                                onError = { throwable -> println(throwable.message) }
                            )
                        }
                    }
                },
                onClick = { postId ->
                    context.router.navigateTo(Screen.AdminCreate.passPostId(id = postId))
                },
            )
        }
    }
}