package com.example.blogmultiplatform.data

import com.example.blogmultiplatform.models.Post
import com.example.blogmultiplatform.models.PostWithoutDetails
import com.example.blogmultiplatform.models.User

interface MongoRepository {
    suspend fun addPost(post: Post): Boolean
    suspend fun updatePost(post: Post): Boolean
    suspend fun readMyPosts(skip: Int, author: String): List<PostWithoutDetails>
    suspend fun readMainPosts(): List<PostWithoutDetails>
    suspend fun deleteSelectedPosts(posts: List<String>): Boolean
    suspend fun searchPostsByTitle(query: String, skip: Int): List<PostWithoutDetails>
    suspend fun readSelectedPost(id: String): Post
    suspend fun checkUserExistence(user: User): User?
    suspend fun checkUserId(id: String): Boolean
}