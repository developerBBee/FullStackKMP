package com.example.blogmultiplatform.androidapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.blogmultiplatform.androidapp.navigation.SetupNavGraph
import com.example.blogmultiplatform.androidapp.ui.theme.BlogMultiplatformTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BlogMultiplatformTheme {
                SetupNavGraph(navController = rememberNavController())
            }
        }
    }
}
