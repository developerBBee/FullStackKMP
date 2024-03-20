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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel : ViewModel() {
    private val _allPosts: MutableState<RequestState<List<Post>>> =
        mutableStateOf(RequestState.Idle)
    val allPosts: State<RequestState<List<Post>>> = _allPosts

    private val _searchedPosts: MutableState<RequestState<List<Post>>> =
        mutableStateOf(RequestState.Idle)
    val searchedPosts: State<RequestState<List<Post>>> = _searchedPosts

    init {
        viewModelScope.launch(Dispatchers.IO) {
            App.Companion.create(APP_ID).login(Credentials.anonymous())
            fetchAllPosts()
        }
    }

    private suspend fun fetchAllPosts() {
        withContext(Dispatchers.Main) {
            _allPosts.value = RequestState.Loading
        }

        viewModelScope.launch(Dispatchers.IO) {
            MongoSync.readAllPosts().collectLatest {
                _allPosts.value = it
            }
        }
    }

    fun searchPosts(query: String) {
        _searchedPosts.value = RequestState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            MongoSync.searchPostsByTitle(query = query).collectLatest {
                _searchedPosts.value = it
            }
        }
    }

    fun resetSearchedPosts() {
        _searchedPosts.value = RequestState.Idle
    }
}