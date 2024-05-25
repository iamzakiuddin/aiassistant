package com.app.aiassistant.network

import com.app.aiassistant.model.ChatCompletionResponse
import com.app.aiassistant.model.ChatRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface RestApi {

    @POST("chat/completions")
    suspend fun getChatCompletions(
        @Body body: ChatRequest
    ): Response<ChatCompletionResponse>
}