package com.example.blogmultiplatform.navigation

import com.example.blogmultiplatform.util.Constants.POST_ID_PARAM
import com.example.blogmultiplatform.util.Constants.QUERY_PARAM

sealed class Screen(val route: String) {
    data object AdminHome : Screen("/admin/")
    data object AdminLogin : Screen("/admin/login")
    data object AdminCreate : Screen("/admin/create") {
        fun passPostId(id: String) = "/admin/create?$POST_ID_PARAM=$id"
    }
    data object AdminMyPosts : Screen("/admin/myposts") {
        fun searchByTitle(query: String) = "/admin/myposts?$QUERY_PARAM=$query"
    }
    data object AdminSuccess : Screen("/admin/success")
}