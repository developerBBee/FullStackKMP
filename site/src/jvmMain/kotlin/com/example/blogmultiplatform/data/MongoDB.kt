package com.example.blogmultiplatform.data

import com.example.blogmultiplatform.models.User
import com.example.blogmultiplatform.util.Constants.DATABASE_NAME
import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.varabyte.kobweb.api.data.add
import com.varabyte.kobweb.api.init.InitApi
import com.varabyte.kobweb.api.init.InitApiContext
import kotlinx.coroutines.flow.firstOrNull

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

    override suspend fun checkUserExistence(user: User): User? {
        return runCatching {
            userCollection
                .find(
                    Filters.and(
                        Filters.eq(User::userName.name, user.userName),
                        Filters.eq(User::password.name, user.password),
                    )
                ).firstOrNull()
        }.onFailure { e ->
            context.logger.error(e.message.toString())
        }.getOrNull()
    }
}