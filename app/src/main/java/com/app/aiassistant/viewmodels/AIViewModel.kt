package com.app.aiassistant.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.aiassistant.App
import com.app.aiassistant.model.ChatCompletionResponse
import com.app.aiassistant.network.NetworkResources
import com.app.aiassistant.network.NetworkUtil
import com.app.aiassistant.network.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class AIViewModel : ViewModel(){

    private var repository: Repository? = null

    private var chatCompletionResponse = MutableLiveData<NetworkResources<ChatCompletionResponse>>()
    private var chatResponseObserver: LiveData<NetworkResources<ChatCompletionResponse>> = chatCompletionResponse

    init {
        repository = NetworkUtil.provideRepository()
    }

    fun getChatCompletion(userQuery: String){
        chatCompletionResponse.value = NetworkResources.loading()
        viewModelScope.launch {
            chatCompletionResponse.value = repository?.getChatCompletions(userQuery)
        }
    }
    fun chatResponse():LiveData<NetworkResources<ChatCompletionResponse>>{
        return chatResponseObserver
    }


}