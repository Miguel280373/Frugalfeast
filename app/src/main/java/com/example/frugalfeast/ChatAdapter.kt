package com.example.frugalfeast

import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView


class ChatAdapter(private val context: Context, private val container: LinearLayout) {

    fun addMessage(messageModel: MessageModel) {
        val textView = TextView(context).apply {
            text = messageModel.message
            textSize = 16f
            setPadding(24, 16, 24, 16) // Este Padding  es interno del texto

            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                // Márgenes para separar los mensajes entre sí
                setMargins(32, 12, 32, 12)

                // Se Alinea a izquierda o derecha según el rol
                gravity = if (messageModel.role == "user") Gravity.END else Gravity.START
            }

            layoutParams = params

            // Fondo personalizado según el emisor
            setBackgroundResource(
                if (messageModel.role == "user") R.drawable.fondo_usuario
                else R.drawable.fondo_modelo
            )
        }

        container.addView(textView)
    }
}