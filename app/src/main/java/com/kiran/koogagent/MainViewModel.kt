package com.kiran.koogagent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiran.koogagent.agent.FintechAgent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val isError: Boolean = false
)

class MainViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val agent = FintechAgent.create(ApiConfig.GROQ_API_KEY)

    fun submitQuery(query: String) {
        if (query.isBlank()) return

        _messages.value = _messages.value + ChatMessage(query, isUser = true)
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    agent.run(query)
                }
                _messages.value = _messages.value + ChatMessage(response, isUser = false)
            } catch (e: Exception) {
                _messages.value = _messages.value + ChatMessage(
                    e.message ?: "Something went wrong",
                    isUser = false,
                    isError = true
                )
            }
            _isLoading.value = false
        }
    }
}
