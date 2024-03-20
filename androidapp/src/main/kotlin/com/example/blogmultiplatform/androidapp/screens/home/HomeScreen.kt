package com.example.blogmultiplatform.androidapp.screens.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.blogmultiplatform.androidapp.components.NavigationDrawer
import com.example.blogmultiplatform.androidapp.components.PostCardsView
import com.example.blogmultiplatform.androidapp.models.Post
import com.example.blogmultiplatform.androidapp.util.RequestState
import com.example.shared.Category
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    posts: RequestState<List<Post>>,
    searchedPosts: RequestState<List<Post>>,
    query: String,
    searchBarOpened: Boolean,
    active: Boolean,
    onActiveChange: (Boolean) -> Unit,
    onQueryChange: (String) -> Unit,
    onCategorySelect: (Category) -> Unit,
    onSearchBarChange: (Boolean) -> Unit,
    onSearch: (String) -> Unit,
    onPostClick: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val onMenuClick: () -> Unit = {
        scope.launch {
            drawerState.open()
        }
    }
    val onCategorySelectAndCloseMenu: (Category) -> Unit = { category ->
        onCategorySelect(category)
        scope.launch {
            drawerState.close()
        }
    }

    NavigationDrawer(
        drawerState = drawerState,
        onCategorySelect = onCategorySelectAndCloseMenu,
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(text = "Blog") },
                    navigationIcon = {
                        IconButton(onClick = onMenuClick) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Drawer Icon"
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                onSearchBarChange(true)
                                onActiveChange(true)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search Icon",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                )
                if (searchBarOpened) {
                    SearchBar(
                        query = query,
                        onQueryChange = onQueryChange,
                        onSearch = onSearch,
                        active = active,
                        onActiveChange = onActiveChange,
                        placeholder = { Text(text = "Search here...") },
                        leadingIcon = {
                            IconButton(onClick = { onSearchBarChange(false) }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back Arrow Icon",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        },
                        trailingIcon = {
                            IconButton(onClick = { onQueryChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close Icon",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    ) {
                        PostCardsView(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 12.dp)
                                .padding(horizontal = 24.dp),
                            posts = searchedPosts,
                            onPostClick = onPostClick,
                        )
                    }
                }
            }
        ) { innerPadding ->
            PostCardsView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding())
                    .padding(horizontal = 24.dp),
                posts = posts,
                hideMessage = true,
                onPostClick = onPostClick,
            )
        }
    }
}
