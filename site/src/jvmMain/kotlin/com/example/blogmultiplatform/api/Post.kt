package com.example.blogmultiplatform.api

import com.example.blogmultiplatform.data.MongoDB
import com.example.blogmultiplatform.models.ApiListResponse
import com.example.blogmultiplatform.models.Post
import com.varabyte.kobweb.api.Api
import com.varabyte.kobweb.api.ApiContext
import com.varabyte.kobweb.api.data.getValue
import com.varabyte.kobweb.api.http.setBodyText
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bson.codecs.ObjectIdGenerator

@Api(routeOverride = "addpost")
suspend fun addPost(context: ApiContext) {
    runCatching {
        val post = context.req.body?.decodeToString()?.let { Json.decodeFromString<Post>(it) }
        val newPost = post?.copy(_id = ObjectIdGenerator().generate().toString())
        context.res.setBodyText(
            newPost?.let {
                context.data.getValue<MongoDB>().addPost(it).toString()
            } ?: false.toString()
        )
    }.onFailure { e ->
        context.logger.info("addPost API EXCEPTION: $e")
        context.res.setBodyText(Json.encodeToString(e.message))
    }
}

@Api(routeOverride = "readmyposts")
suspend fun readMyPosts(context: ApiContext) {
    runCatching {
        val skip = context.req.params["skip"]?.toInt() ?: 0
        val author = context.req.params["author"] ?: ""
        val myPosts = context.data.getValue<MongoDB>().readMyPosts(skip = skip, author = author)
        context.res.setBodyText(Json.encodeToString(
            ApiListResponse.Success(data = myPosts))
        )
    }.onFailure { e ->
        context.logger.info("readMyPosts API EXCEPTION: $e")
        context.res.setBodyText(Json.encodeToString(
            ApiListResponse.Error(message = e.message.toString()))
        )
    }
}

@Api(routeOverride = "deleteselectedposts")
suspend fun deleteSelectedPosts(context: ApiContext) {
    runCatching {
        context.req.body?.decodeToString()
            ?.let { requestBody ->
                Json.decodeFromString<List<String>>(requestBody)
            }
            ?.let { posts ->
                context.data.getValue<MongoDB>().deleteSelectedPosts(posts = posts)
            }
            ?.let{ result ->
                context.res.setBodyText(result.toString())
            } ?: throw Exception("null request")
    }.onFailure { e ->
        context.logger.info("deleteSelectedPosts API EXCEPTION: $e")
        context.res.setBodyText(Json.encodeToString(e.message))
    }
}

@Api(routeOverride = "searchposts")
suspend fun searchPostsByTitle(context: ApiContext) {
    runCatching {
        context.req.run {
            ((params["query"] ?: throw Exception("no query")) to (params["skip"]?.toInt() ?: 0))
                .let { (query, skip) ->
                    context.data.getValue<MongoDB>().searchPostsByTitle(query = query, skip = skip)
                }
                .let{ result ->
                    context.res.setBodyText(Json.encodeToString(
                        ApiListResponse.Success(data = result))
                    )
                }
        }
    }.onFailure { e ->
        context.logger.info("readMyPosts API EXCEPTION: $e")
        context.res.setBodyText(Json.encodeToString(
            ApiListResponse.Error(message = e.message.toString()))
        )
    }
}