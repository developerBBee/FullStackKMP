package com.example.blogmultiplatform.navigation

sealed class Screen(val route: String) {
    data object AdminHome : Screen("/admin/")
    data object AdminLogin : Screen("/admin/login")
    data object AdminCreate : Screen("/admin/create")
    data object AdminMyPosts : Screen("/admin/myposts")
    data object AdminSuccess : Screen("/admin/success")
}