package com.example.blogmultiplatform.api

import com.example.blogmultiplatform.data.MongoDB
import com.example.blogmultiplatform.models.User
import com.example.blogmultiplatform.models.UserWithoutPassword
import com.varabyte.kobweb.api.Api
import com.varabyte.kobweb.api.ApiContext
import com.varabyte.kobweb.api.data.getValue
import com.varabyte.kobweb.api.http.setBodyText
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

@Api(routeOverride = "usercheck")
suspend fun userCheck(context: ApiContext) {
    runCatching {
        context.req.body?.decodeToString()
            ?.let { reqUserJson ->
                Json.decodeFromString<User>(reqUserJson)
            }
            ?.let { reqUser ->
                context.data.getValue<MongoDB>().checkUserExistence(
                    User(username = reqUser.username, password = hashPassword(reqUser.password))
                )
            }
            ?.also { user ->
                context.res.setBodyText(
                    Json.encodeToString(
                        UserWithoutPassword(_id = user._id, username = user.username)
                    )
                )
            }
            ?: run {
                context.res.setBodyText(Json.encodeToString("User does not exist."))
            }
    }.onFailure { e ->
        context.res.setBodyText(Json.encodeToString(Exception(e.message)))
    }
}

private fun hashPassword(password: String): String {
    val messageDigest = MessageDigest.getInstance("SHA-256")
    val hashBytes = messageDigest.digest(password.toByteArray(StandardCharsets.UTF_8))
    val hexString = StringBuffer()

    for (byte in hashBytes) {
        hexString.append(String.format("%02x", byte))
    }

    return hexString.toString()
}