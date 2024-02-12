package com.example.blogmultiplatform.util

import com.example.blogmultiplatform.models.ApiListResponse
import com.example.blogmultiplatform.models.User
import com.example.blogmultiplatform.models.UserWithoutPassword
import com.varabyte.kobweb.browser.api
import kotlinx.browser.localStorage
import kotlinx.browser.window
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import org.w3c.dom.get

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

suspend fun fetchMyPosts(
    skip: Int,
    onSuccess: (ApiListResponse) -> Unit,
    onError: (Throwable) -> Unit,
) {
    runCatching {
        window.api.tryGet(
            apiPath = "readmyposts?skip=$skip&author=${localStorage["username"]}"
        )?.decodeToString()?.let { result ->
            onSuccess(Json.decodeFromString(result))
        } ?: throw Exception(message = "null result")
    }.getOrElse { e ->
        onError(e)
    }
}