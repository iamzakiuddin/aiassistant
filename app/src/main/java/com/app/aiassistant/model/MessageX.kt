package com.app.aiassistant.model


import com.google.gson.annotations.SerializedName

data class MessageX(
    @SerializedName("content")
    val content: String,
    @SerializedName("role")
    val role: String
)