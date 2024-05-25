package com.app.aiassistant.model


import com.google.gson.annotations.SerializedName

data class ChatRequest(
    @SerializedName("messages")
    val messages: List<MessageX>,
    @SerializedName("model")
    val model: String
)