package com.example.blogmultiplatform.models

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

@Serializable
sealed class ApiListResponse {
    @Serializable
    @SerialName("idle")
    object Idle : ApiListResponse()

    @Serializable
    @SerialName("success")
    data class Success(val data: List<PostWithoutDetails>) : ApiListResponse()

    @Serializable
    @SerialName("error")
    data class Error(val message: String) : ApiListResponse()
}

@Serializable(ApiResponseSerializer::class)
sealed class ApiResponse {
    @Serializable
    @SerialName("idle")
    object Idle : ApiResponse()

    @Serializable
    @SerialName("success")
    data class Success(val data: Post) : ApiResponse()

    @Serializable
    @SerialName("error")
    data class Error(val message: String) : ApiResponse()
}

object ApiResponseSerializer : JsonContentPolymorphicSerializer<ApiResponse>(ApiResponse::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<ApiResponse> =
        when {
            "data" in element.jsonObject -> ApiResponse.Success.serializer()
            "message" in element.jsonObject -> ApiResponse.Success.serializer()
            else -> ApiResponse.Idle.serializer()
        }
}