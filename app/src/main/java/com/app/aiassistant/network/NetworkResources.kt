package com.app.aiassistant.network

import androidx.annotation.NonNull
import androidx.annotation.Nullable


class NetworkResources<T> (var status: NetworkStatus?,
                           @Nullable var data: T?,
                           @Nullable var message: String?){


    companion object {

        fun <T> success(@Nullable data: T): NetworkResources<T> {
            return NetworkResources(NetworkStatus.SUCCESS, data, null)
        }

        fun <T> error(@NonNull msg: String): NetworkResources<T> {
            return NetworkResources(NetworkStatus.ERROR, null, msg)
        }

        fun <T> loading(): NetworkResources<T>? {
            return NetworkResources(NetworkStatus.LOADING, null, null)
        }

        fun <T>successMessage(successMessage : String) : NetworkResources<T>{
            return NetworkResources(NetworkStatus.SUCCESS_MESSAGE, null, null)
        }

    }
    enum class NetworkStatus {
        ERROR, LOADING, SUCCESS, SUCCESS_MESSAGE
    }
}