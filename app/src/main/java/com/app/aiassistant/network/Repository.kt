package com.app.aiassistant.network

import com.app.aiassistant.model.ChatCompletionResponse
import com.app.aiassistant.model.ChatRequest
import com.app.aiassistant.model.MessageX
import com.google.gson.JsonObject
import org.json.JSONException

class Repository(val dataApi: RestApi) {

    suspend fun getChatCompletions(userQuery: String): NetworkResources<ChatCompletionResponse> {
        try {
            //val systemMessageItem = MessageX("Introduce your self and Let ask me what topic I want to discuss   ","system")
            val userMessageItem = MessageX(userQuery,"user")
            val response = dataApi.getChatCompletions(
                    ChatRequest(
                        arrayListOf(userMessageItem),
                    "gpt-3.5-turbo-16k"
                )
            )
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null && response.code() == 200) {
                    try {
                        if (responseBody.choices.isNotEmpty() && responseBody.choices.size>0){
                            return NetworkResources.success(responseBody)
                        } else {
                            return NetworkResources.error("No data!")
                        }
                    } catch (e: JSONException) {
                        return NetworkResources.error("Something went wrong")
                    }
                } else {
                    return NetworkResources.error("No data!")
                }
            } else {
                return NetworkResources.error(response.message())
            }
        } catch (e: Exception) {
            return NetworkResources.error(e.message ?: "Unknown error")
        }
    }
}

fun JsonObject.getStringOrNull(key: String): String? {
    return this.get(key)?.takeIf { it.isJsonPrimitive }?.asString
}