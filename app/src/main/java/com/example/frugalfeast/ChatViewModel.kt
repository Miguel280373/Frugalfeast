package com.example.frugalfeast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content


class ChatViewModel : ViewModel() {

    val messageList = mutableListOf<MessageModel>()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.0-flash",
        apiKey = "AIzaSyCG0BvuBclCNFXpIBo265JYw8w4-F_YRUw"
    )

    fun sendMessage(
        question: String,
        onStart: () -> Unit,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val chat = generativeModel.startChat(
                    history = messageList.map {
                        content(it.role) { text(it.message) }
                    }
                )

                messageList.add(MessageModel(question, "user"))
                onStart()

                val response = chat.sendMessage(question)
                val answer = response.text ?: "Sin respuesta"
                messageList.add(MessageModel(answer, "model"))

                onSuccess(answer)

            } catch (e: Exception) {
                onError("Error: ${e.message}")
            }
        }
    }
}