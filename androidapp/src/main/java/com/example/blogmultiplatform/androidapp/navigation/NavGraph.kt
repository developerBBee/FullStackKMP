package com.example.blogmultiplatform.androidapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.blogmultiplatform.androidapp.navigation.distinations.categoryRoute
import com.example.blogmultiplatform.androidapp.navigation.distinations.detailsRoute
import com.example.blogmultiplatform.androidapp.navigation.distinations.homeRoute

@Composable
fun SetupNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
    ) {
        homeRoute(
            onCategorySelect = {
                navController.navigate(Screen.Category.passCategory(it))
            },
            onPostClick = {
                navController.navigate(Screen.Details.passPostId(it))
            }
        )

        categoryRoute(
            onBackPress = { navController.popBackStack() },
            onPostClick = { navController.navigate(Screen.Details.passPostId(it)) }
        )

        detailsRoute(onBackPress = { navController.popBackStack() })
    }
}