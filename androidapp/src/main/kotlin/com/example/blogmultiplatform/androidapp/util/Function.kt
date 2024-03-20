package com.example.blogmultiplatform.androidapp.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.text.DateFormat
import java.util.Date

fun String.decodeThumbnailImage(): Bitmap? = runCatching {
    val byteArray = Base64.decode(cleanupImageString(), Base64.NO_WRAP)
    BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
}.getOrNull()

fun String.cleanupImageString(): String =
    replace("data:image/png;base64,", "")
        .replace("data:image/jpeg;base64,", "")

fun Long.convertLongToDate(): String = DateFormat.getDateInstance().format(Date(this))