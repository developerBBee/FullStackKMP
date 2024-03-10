package com.example.blogmultiplatform.navigation

import com.example.blogmultiplatform.models.Category
import com.example.blogmultiplatform.models.Constants.CATEGORY_PARAM
import com.example.blogmultiplatform.models.Constants.POST_ID_PARAM
import com.example.blogmultiplatform.models.Constants.QUERY_PARAM
import com.example.blogmultiplatform.models.Constants.UPDATED_PARAM


sealed class Screen(val route: String) {
    data object AdminHome : Screen("/admin/")
    data object AdminLogin : Screen("/admin/login")
    data object AdminCreate : Screen("/admin/create") {
        fun passPostId(id: String) = "$route?$POST_ID_PARAM=$id"
    }
    data object AdminMyPosts : Screen("/admin/myposts") {
        fun searchByTitle(query: String) = "$route?$QUERY_PARAM=$query"
    }
    data object AdminSuccess : Screen("/admin/success") {
        fun getRoute(updated: Boolean) = if (updated) {
            "$route?$UPDATED_PARAM=true"
        } else {
            route
        }
    }
    data object HomePage: Screen("/")
    data object SearchPage: Screen("/search/query") {
        fun searchByCategory(category: Category) = "$route?$CATEGORY_PARAM=${category.name}"
        fun searchByTitle(query: String) = "$route?$QUERY_PARAM=$query"
    }
}