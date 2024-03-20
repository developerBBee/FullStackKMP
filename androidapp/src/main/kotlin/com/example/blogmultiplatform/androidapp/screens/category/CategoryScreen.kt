package com.example.blogmultiplatform.androidapp.screens.category

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.blogmultiplatform.androidapp.components.PostCardsView
import com.example.blogmultiplatform.androidapp.models.Post
import com.example.blogmultiplatform.androidapp.util.RequestState
import com.example.shared.Category

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    posts: RequestState<List<Post>>,
    category: Category,
    onBackPress: () -> Unit,
    onPostClick: (String) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = category.name) },
                navigationIcon = {
                    IconButton(onClick = onBackPress) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back Arrow Icon"
                        )
                    }
                }
            )
        },
    ) {
        PostCardsView(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = it.calculateTopPadding())
                .padding(horizontal = 24.dp),
            posts = posts,
            onPostClick = onPostClick,
        )
    }
}