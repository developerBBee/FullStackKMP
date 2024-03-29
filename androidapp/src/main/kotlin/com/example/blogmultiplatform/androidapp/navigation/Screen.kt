package com.example.blogmultiplatform.androidapp.navigation

import com.example.blogmultiplatform.androidapp.util.Constants.CATEGORY_ARGUMENT
import com.example.shared.Category as PostCategory

sealed class Screen(val route: String) {
    object Home : Screen(route = "home_screen")
    object Category : Screen(route = "category_screen/{$CATEGORY_ARGUMENT}") {
        fun passCategory(category: PostCategory) = "category_screen/${category.name}"
    }
    object Details : Screen(route = "details_screen/{postId}") {
        fun passPostId(id: String) = "details_screen/$id"
    }
}