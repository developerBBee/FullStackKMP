package com.example.blogmultiplatform.util

import com.example.blogmultiplatform.models.User
import com.example.blogmultiplatform.models.UserWithoutPassword
import com.varabyte.kobweb.browser.api
import kotlinx.browser.window
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

suspend fun checkUserExistence(user: User): UserWithoutPassword? {
    return runCatching {
        window.api.tryPost(
            apiPath = "usercheck",
            body = Json.encodeToString(user).encodeToByteArray()
        )?.decodeToString().let { result ->
            Json.decodeFromString<UserWithoutPassword>(result.toString())
        }
    }.onFailure { e ->
        println("ApiFunctions: checkUserExistence() failed.")
        println(e.message)
    }.getOrNull()
}

suspend fun checkUserId(id: String): Boolean {
    return runCatching {
        window.api.tryPost(
            apiPath = "checkuserid",
            body = Json.encodeToString(id).encodeToByteArray()
        )?.decodeToString().let { result ->
            Json.decodeFromString<Boolean>(result.toString())
        }
    }.getOrElse { e ->
        println(e.message)
        false
    }
}