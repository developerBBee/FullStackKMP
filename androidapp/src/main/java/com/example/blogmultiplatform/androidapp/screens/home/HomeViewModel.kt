package com.example.blogmultiplatform.androidapp.screens.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blogmultiplatform.androidapp.data.MongoSync
import com.example.blogmultiplatform.androidapp.models.Post
import com.example.blogmultiplatform.androidapp.util.KeyFile.APP_ID
import com.example.blogmultiplatform.androidapp.util.RequestState
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.Credentials
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val _allPosts: MutableState<RequestState<List<Post>>> =
        mutableStateOf(RequestState.Idle)
    val allPosts: State<RequestState<List<Post>>> = _allPosts

    init {
        viewModelScope.launch {
            App.Companion.create(APP_ID).login(Credentials.anonymous())
            fetchAllPosts()
        }
    }

    private fun fetchAllPosts() {
        viewModelScope.launch {
            MongoSync.readAllPosts().collectLatest {
                _allPosts.value = it
            }
        }
    }
}