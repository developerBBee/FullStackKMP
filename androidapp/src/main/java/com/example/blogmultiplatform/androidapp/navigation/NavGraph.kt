package com.example.blogmultiplatform.androidapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.blogmultiplatform.androidapp.models.Category
import com.example.blogmultiplatform.androidapp.screens.category.CategoryScreen
import com.example.blogmultiplatform.androidapp.screens.category.CategoryViewModel
import com.example.blogmultiplatform.androidapp.screens.home.HomeScreen
import com.example.blogmultiplatform.androidapp.screens.home.HomeViewModel

@Composable
fun SetupNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
    ) {
        composable(route = Screen.Home.route) {
            val viewModel: HomeViewModel = viewModel()
            var query by remember { mutableStateOf("") }
            var searchBarOpened by remember { mutableStateOf(false) }
            var active by remember { mutableStateOf(false) }

            HomeScreen(
                posts = viewModel.allPosts.value,
                searchedPosts = viewModel.searchedPosts.value,
                query = query,
                searchBarOpened = searchBarOpened,
                active = active,
                onActiveChange = { active = it },
                onQueryChange = { query = it },
                onCategorySelect = {
                    navController.navigate(Screen.Category.passCategory(it))
                },
                onSearchBarChange = { opened ->
                    searchBarOpened = opened
                    if (!opened) {
                        query = ""
                        active = false
                        viewModel.resetSearchedPosts()
                    }
                },
                onSearch = { viewModel.searchPosts(it) },
                onPostClick = {},
            )
        }
        composable(
            route = Screen.Category.route,
            arguments = listOf(navArgument(name = "category") {
                type = NavType.StringType
            })
        ) {
            val viewModel: CategoryViewModel = viewModel()
            val selectedCategory = it.arguments?.getString("category")
                ?.let { Category.valueOf(it) }
                ?: Category.Programing

            CategoryScreen(
                posts = viewModel.categoryPost.value,
                category = selectedCategory,
                onBackPress = { navController.popBackStack() },
                onPostClick = {},
            )
        }
        composable(route = Screen.Details.route) {}
    }
}