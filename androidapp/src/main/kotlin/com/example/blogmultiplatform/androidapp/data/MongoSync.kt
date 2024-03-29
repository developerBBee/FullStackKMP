package com.example.blogmultiplatform.androidapp.data

import com.example.blogmultiplatform.androidapp.models.Post
import com.example.blogmultiplatform.androidapp.util.KeyFile.APP_ID
import com.example.blogmultiplatform.androidapp.util.RequestState
import com.example.shared.Category
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.log.LogLevel
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

object MongoSync : MongoSyncRepository {
    private val app = App.Companion.create(APP_ID)
    private val user = app.currentUser
    private lateinit var realm: Realm

    init {
        configureTheRealm()
    }

    override fun configureTheRealm() {
        if (user != null) {
            val config = SyncConfiguration.Builder(user, setOf(Post::class))
                .initialSubscriptions {
                    add(query = it.query(Post::class), name = "Blog Posts")
                }
                .log(LogLevel.ALL)
                .build()
            realm = Realm.open(config)
        }
    }

    override fun readAllPosts(): Flow<RequestState<List<Post>>> {
        return if (user != null) {
            runCatching<Flow<RequestState<List<Post>>>> {
                realm.query(Post::class)
                    .asFlow()
                    .map { result ->
                        RequestState.Success(data = result.list)
                    }
            }.getOrElse {
                flow { emit(RequestState.Error(it)) }
            }
        } else {
            flow { emit(RequestState.Error(Exception("User not authenticated."))) }
        }
    }

    override fun searchPostsByTitle(query: String): Flow<RequestState<List<Post>>> {
        return if (user != null) {
            runCatching<Flow<RequestState<List<Post>>>> {
                realm.query<Post>(query = "title CONTAINS[c] $0", query)
                    .asFlow()
                    .map { result ->
                        RequestState.Success(data = result.list)
                    }
            }.getOrElse {
                flow { emit(RequestState.Error(it)) }
            }
        } else {
            flow { emit(RequestState.Error(Exception("User not authenticated."))) }
        }
    }

    override fun searchPostsByCategory(category: Category): Flow<RequestState<List<Post>>> {
        return if (user != null) {
            runCatching<Flow<RequestState<List<Post>>>> {
                realm.query<Post>(query = "category == $0", category.name)
                    .asFlow()
                    .map {
                        RequestState.Success(data = it.list)
                    }
            }.getOrElse {
                flow { emit(RequestState.Error(it)) }
            }
        } else {
            flow { emit(RequestState.Error(Exception("User not authenticated."))) }
        }
    }
}