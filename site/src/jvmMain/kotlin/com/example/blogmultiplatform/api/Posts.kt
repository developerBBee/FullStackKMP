package com.example.blogmultiplatform.api

import com.example.blogmultiplatform.data.MongoDB
import com.example.blogmultiplatform.models.ApiListResponse
import com.example.blogmultiplatform.models.ApiResponse
import com.example.blogmultiplatform.models.Constants.AUTHOR_PARAM
import com.example.blogmultiplatform.models.Constants.POST_ID_PARAM
import com.example.blogmultiplatform.models.Constants.QUERY_PARAM
import com.example.blogmultiplatform.models.Constants.SKIP_PARAM
import com.example.blogmultiplatform.models.Post
import com.varabyte.kobweb.api.Api
import com.varabyte.kobweb.api.ApiContext
import com.varabyte.kobweb.api.data.getValue
import com.varabyte.kobweb.api.http.Request
import com.varabyte.kobweb.api.http.Response
import com.varabyte.kobweb.api.http.setBodyText
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bson.codecs.ObjectIdGenerator

@Api(routeOverride = "addpost")
suspend fun addPost(context: ApiContext) {
    context.runCatching {
        req.getBody<Post>()
            ?.run { copy(_id = ObjectIdGenerator().generate().toString()) }
            ?.let { data.getValue<MongoDB>().addPost(it) }
            .also { res.setBody(it ?: false) }
    }.onFailure { e ->
        context.logger.info("addPost API EXCEPTION: $e")
        context.res.setBody(e.message)
    }
}

@Api(routeOverride = "updatepost")
suspend fun updatePost(context: ApiContext) {
    context.runCatching {
        req.getBody<Post>()
            ?.let { data.getValue<MongoDB>().updatePost(it) }
            .also { res.setBody(it ?: false) }
    }.onFailure { e ->
        context.logger.info("addPost API EXCEPTION: $e")
        context.res.setBody(e.message)
    }
}

@Api(routeOverride = "readmyposts")
suspend fun readMyPosts(context: ApiContext) {
    context.runCatching {
        val skip = req.params[SKIP_PARAM]?.toInt() ?: 0
        val author = req.params[AUTHOR_PARAM] ?: ""
        val myPosts = data.getValue<MongoDB>().readMyPosts(skip = skip, author = author)
        res.setBody(ApiListResponse.Success(data = myPosts))
    }.onFailure { e ->
        context.logger.info("readMyPosts API EXCEPTION: $e")
        context.res.setBody(ApiListResponse.Error(message = e.message.toString()))
    }
}

@Api(routeOverride = "readmainposts")
suspend fun readMainPosts(context: ApiContext) {
    context.runCatching {
        val mainPosts = data.getValue<MongoDB>().readMainPosts()
        res.setBody(ApiListResponse.Success(data = mainPosts))
    }.onFailure { e ->
        context.logger.info("readMainPosts API EXCEPTION: $e")
        context.res.setBody(ApiListResponse.Error(message = e.message.toString()))
    }
}

@Api(routeOverride = "readlatestposts")
suspend fun readLatestPosts(context: ApiContext) {
    context.runCatching {
        val skip = req.params[SKIP_PARAM]?.toInt() ?: 0
        val latestPosts = data.getValue<MongoDB>().readLatestPosts(skip = skip)
        res.setBody(ApiListResponse.Success(data = latestPosts))
    }.onFailure { e ->
        context.logger.info("readLatestPosts API EXCEPTION: $e")
        context.res.setBody(ApiListResponse.Error(message = e.message.toString()))
    }
}

@Api(routeOverride = "deleteselectedposts")
suspend fun deleteSelectedPosts(context: ApiContext) {
    context.runCatching {
        req.getBody<List<String>>()
            ?.let { posts ->
                data.getValue<MongoDB>().deleteSelectedPosts(posts = posts)
            }
            ?.let{ result ->
                res.setBody(result)
            } ?: throw Exception("null request")
    }.onFailure { e ->
        context.logger.info("deleteSelectedPosts API EXCEPTION: $e")
        context.res.setBody(e.message)
    }
}

@Api(routeOverride = "searchposts")
suspend fun searchPostsByTitle(context: ApiContext) {
    context.runCatching {
        ((req.params[QUERY_PARAM] ?: "") to (req.params[SKIP_PARAM]?.toInt() ?: 0))
            .let { (query, skip) ->
                data.getValue<MongoDB>().searchPostsByTitle(query = query, skip = skip)
            }
            .let{ result ->
                res.setBody(ApiListResponse.Success(data = result))
            }
    }.onFailure { e ->
        context.logger.info("readMyPosts API EXCEPTION: $e")
        context.res.setBody(ApiListResponse.Error(message = e.message.toString()))
    }
}

@Api(routeOverride = "readselectionpost")
suspend fun readSelectionPost(context: ApiContext) {
    val postId = context.req.params[POST_ID_PARAM]
    if (!postId.isNullOrEmpty()) {
        runCatching {
            val selectedPost = context.data.getValue<MongoDB>().readSelectedPost(id = postId)
            ApiResponse.Success(data = selectedPost)
        }.getOrElse { e ->
            ApiResponse.Error(message = e.message.toString())
        }
    } else {
        ApiResponse.Error(message = "Selected Post does not exist.")
    }.also { apiResponse ->
        context.res.setBody(apiResponse)
    }
}

inline fun <reified T> Response.setBody(data: T) {
    setBodyText(Json.encodeToString(data))
}

inline fun <reified T> Request.getBody(): T? {
    return body?.decodeToString()?.let { Json.decodeFromString(it) }
}