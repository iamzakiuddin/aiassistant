package com.app.aiassistant.model


import com.google.gson.annotations.SerializedName

data class ChatCompletionResponse(
    @SerializedName("choices")
    val choices: List<Choice>,
    @SerializedName("created")
    val created: Int,
    @SerializedName("id")
    val id: String,
    @SerializedName("model")
    val model: String,
    @SerializedName("object")
    val objectX: String,
    @SerializedName("system_fingerprint")
    val systemFingerprint: String,
    @SerializedName("usage")
    val usage: Usage
)