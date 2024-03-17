package com.example.blogmultiplatform.androidapp.navigation.distinations

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.blogmultiplatform.androidapp.models.Category
import com.example.blogmultiplatform.androidapp.navigation.Screen
import com.example.blogmultiplatform.androidapp.screens.category.CategoryScreen
import com.example.blogmultiplatform.androidapp.screens.category.CategoryViewModel
import com.example.blogmultiplatform.androidapp.util.Constants.CATEGORY_ARGUMENT

fun NavGraphBuilder.categoryRoute(
    onBackPress: () -> Unit,
    onPostClick: (String) -> Unit,
) {
    composable(
        route = Screen.Category.route,
        arguments = listOf(navArgument(name = CATEGORY_ARGUMENT) {
            type = NavType.StringType
        })
    ) {
        val viewModel: CategoryViewModel = viewModel()
        val selectedCategory = it.arguments?.getString(CATEGORY_ARGUMENT)
            ?.let { Category.valueOf(it) }
            ?: Category.Programing

        CategoryScreen(
            posts = viewModel.categoryPost.value,
            category = selectedCategory,
            onBackPress = onBackPress,
            onPostClick = onPostClick,
        )
    }
}