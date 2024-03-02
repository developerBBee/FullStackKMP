package com.example.blogmultiplatform.util

import com.example.blogmultiplatform.models.ApiListResponse
import com.example.blogmultiplatform.models.ApiResponse
import com.example.blogmultiplatform.models.Constants.AUTHOR_PARAM
import com.example.blogmultiplatform.models.Constants.POST_ID_PARAM
import com.example.blogmultiplatform.models.Constants.QUERY_PARAM
import com.example.blogmultiplatform.models.Constants.SKIP_PARAM
import com.example.blogmultiplatform.models.Post
import com.example.blogmultiplatform.models.RandomJoke
import com.example.blogmultiplatform.models.User
import com.example.blogmultiplatform.models.UserWithoutPassword
import com.varabyte.kobweb.browser.api
import com.varabyte.kobweb.browser.http.http
import kotlinx.browser.localStorage
import kotlinx.browser.window
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import org.w3c.dom.get
import org.w3c.dom.set
import kotlin.js.Date

suspend fun checkUserExistence(user: User): UserWithoutPassword? {
    return runCatching {
        window.api.tryPost(
            apiPath = "usercheck",
            body = Json.encodeToString(user).encodeToByteArray()
        )?.decodeToString()?.let { result ->
            result.parseData<UserWithoutPassword>()
        } ?: throw Exception(message = "null result")
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
        )?.decodeToString()?.parseData<Boolean>()
            ?: throw Exception(message = "null result")
    }.getOrElse { e ->
        println(e.message)
        false
    }
}

suspend fun getJoke(): RandomJoke = runCatching {
    window.http.get(KeyFile.HUMOR_API_URL).decodeToString()
        .let { result ->
            localStorage["date"] = Date.now().toString()
            localStorage["joke"] = result
            result.parseData<RandomJoke>()
        }
}.getOrElse { e ->
    println("getJoke() error ${e.message}")
    getLocalJoke()
}

fun getLocalJoke(): RandomJoke = runCatching {
    requireNotNull(
        localStorage["joke"]?.parseData<RandomJoke>()
    )
}.getOrElse { e ->
    println("getLocalJoke() error ${e.message}")
    RandomJoke(id = -1, joke = "Unexpected Error.")
}

suspend fun addPost(post: Post): Boolean {
    return runCatching {
        window.api.tryPost(
            apiPath = "addpost",
            body = Json.encodeToString(post).encodeToByteArray()
        )?.decodeToString().toBoolean()
    }.getOrElse { e ->
        println(e.message)
        false
    }
}

suspend fun fetchMyPosts(
    skip: Int,
    onSuccess: (ApiListResponse.Success) -> Unit,
    onError: (Throwable) -> Unit,
) {
    runCatching {
        window.api.tryGet(
            apiPath = "readmyposts?$SKIP_PARAM=$skip&$AUTHOR_PARAM=${localStorage["username"]}"
        )?.decodeToString()?.let { result ->
            runCatching {
                onSuccess(result.parseData<ApiListResponse.Success>())
            }.onFailure {
                throw Exception(result.parseData<ApiListResponse.Error>().message)
            }
        } ?: throw Exception(message = "null result")
    }.getOrElse { e ->
        onError(e)
    }
}

suspend fun deleteSelectedPosts(ids: List<String>): Boolean {
    return runCatching {
        window.api.tryPost(
            apiPath = "deleteselectedposts",
            body = Json.encodeToString(ids).encodeToByteArray()
        )?.decodeToString()?.let { result ->
            result.toBoolean()
        } ?: throw Exception(message = "null result")
    }.getOrElse { e ->
        println(e.message)
        false
    }
}

suspend fun searchPostsByTitle(
    query: String,
    skip: Int,
    onSuccess: (ApiListResponse.Success) -> Unit,
    onError: (Throwable) -> Unit,
) {
    runCatching {
        window.api.tryGet(
            apiPath = "searchposts?$QUERY_PARAM=$query&$SKIP_PARAM=$skip"
        )?.decodeToString()?.let { result ->
            runCatching {
                onSuccess(result.parseData<ApiListResponse.Success>())
            }.onFailure {
                throw Exception(result.parseData<ApiListResponse.Error>().message)
            }
        }
    }.getOrElse { e ->
        onError(e)
    }
}

suspend fun fetchSelectedPost(id: String): ApiResponse =
    runCatching {
        window.api.tryGet(
            apiPath = "readselectionpost?$POST_ID_PARAM=$id"
        )?.decodeToString()?.let { result ->
            result.parseData<ApiResponse.Success>()
        } ?: ApiResponse.Error(message = "result is null.")
    }.getOrElse { e ->
        println(e.stackTraceToString())
        ApiResponse.Error(message = e.message.toString())
    }

inline fun <reified T> String?.parseData(): T {
    return Json.decodeFromString(this.toString())
}