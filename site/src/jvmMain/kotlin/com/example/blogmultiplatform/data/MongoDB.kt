package com.example.blogmultiplatform.data

import com.example.blogmultiplatform.models.Constants.POSTS_PER_PAGE
import com.example.blogmultiplatform.models.Post
import com.example.blogmultiplatform.models.PostWithoutDetails
import com.example.blogmultiplatform.models.User
import com.example.blogmultiplatform.util.Constants.DATABASE_NAME
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Sorts.descending
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.varabyte.kobweb.api.data.add
import com.varabyte.kobweb.api.init.InitApi
import com.varabyte.kobweb.api.init.InitApiContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList

@InitApi
fun initMongoDB(context: InitApiContext) {
    // Need to add those two properties because without these, MongoDB will not work.
    System.setProperty(
        "org.litote.mongo.test.mapping.service",
        "org.litote.kmongo.serialization.SerializationClassMappingTypeService"
    )
    context.data.add(MongoDB(context))
}

class MongoDB(private val context: InitApiContext) : MongoRepository {
    private val client = MongoClient.create()
    private val database = client.getDatabase(DATABASE_NAME)
    private val userCollection = database.getCollection<User>("user")
    private val postCollection = database.getCollection<Post>("post")

    override suspend fun addPost(post: Post): Boolean {
        return postCollection.insertOne(post).wasAcknowledged()
    }

    override suspend fun updatePost(post: Post): Boolean {
        return postCollection.updateOne(
            filter = Filters.eq(Post::_id.name, post._id),
            update = mutableListOf(
                Updates.set(Post::title.name, post.title),
                Updates.set(Post::subtitle.name, post.subtitle),
                Updates.set(Post::category.name, post.category),
                Updates.set(Post::thumbnail.name, post.thumbnail),
                Updates.set(Post::content.name, post.content),
                Updates.set(Post::popular.name, post.popular),
                Updates.set(Post::main.name, post.main),
                Updates.set(Post::sponsored.name, post.sponsored),
            )
        ).wasAcknowledged()
    }

    override suspend fun readMyPosts(skip: Int, author: String): List<PostWithoutDetails> {
        return postCollection
            .withDocumentClass(PostWithoutDetails::class.java)
            .find(Filters.eq(PostWithoutDetails::author.name, author))
            .sort(descending(PostWithoutDetails::date.name))
            .skip(skip)
            .limit(POSTS_PER_PAGE)
            .toList()
    }

    override suspend fun deleteSelectedPosts(posts: List<String>): Boolean {
        return postCollection
            .deleteMany(Filters.`in`(Post::_id.name, posts))
            .wasAcknowledged()
    }

    override suspend fun searchPostsByTitle(query: String, skip: Int): List<PostWithoutDetails> {
        val regexQuery = query.toRegex(RegexOption.IGNORE_CASE)
        return postCollection
            .withDocumentClass(PostWithoutDetails::class.java)
            .find(Filters.regex(PostWithoutDetails::title.name, regexQuery.pattern))
            .sort(descending(PostWithoutDetails::date.name))
            .skip(skip)
            .limit(POSTS_PER_PAGE)
            .toList()
    }

    override suspend fun readSelectedPost(id: String): Post {
        return postCollection
            .find(Filters.eq(Post::_id.name, id))
            .first()
    }

    override suspend fun checkUserExistence(user: User): User? {
        return runCatching {
            userCollection
                .find(
                    Filters.and(
                        Filters.eq(User::username.name, user.username),
                        Filters.eq(User::password.name, user.password),
                    )
                ).firstOrNull()
        }.onFailure { e ->
            context.logger.error(e.message.toString())
        }.getOrNull()
    }

    override suspend fun checkUserId(id: String): Boolean {
        return runCatching {
            val documentCount = userCollection.countDocuments(Filters.eq(User::_id.name, id))
            documentCount > 0
        }.getOrElse { e ->
            context.logger.error(e.message.toString())
            false
        }
    }
}