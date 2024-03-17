package com.example.blogmultiplatform.androidapp.models

import com.example.blogmultiplatform.CategoryCommon

enum class Category(override val color: String) : CategoryCommon {
    Programing(color = ""),
    Technology(color = ""),
    Design(color = ""),
}