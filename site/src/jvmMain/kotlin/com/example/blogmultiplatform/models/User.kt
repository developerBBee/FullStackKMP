package com.example.blogmultiplatform.models

import kotlinx.serialization.Serializable
import org.bson.codecs.ObjectIdGenerator

@Serializable
actual data class User(
    actual val _id: String = ObjectIdGenerator().generate().toString(),
    actual val userName: String = "",
    actual val password: String = ""
)

@Serializable
actual data class UserWithoutPassword(
    actual val _id: String = ObjectIdGenerator().generate().toString(),
    actual val userName: String = ""
)
