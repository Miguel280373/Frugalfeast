package com.example.frugalfeast

import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.text.Html

class ChatAdapter(private val context: Context, private val container: LinearLayout) {

    // Esta función se encarga de agregar un nuevo mensaje al contenedor
    fun addMessage(messageModel: MessageModel) {

        // Reemplazar los "*" con "<li>" para crear una lista ordenada en HTML
        val formattedMessage = if (messageModel.message.contains("**")) {
            // Si el mensaje tiene un formato que indica ingredientes u opciones, procesarlo
            var message = messageModel.message
            // Eliminar las marcas de negrita (**)
            message = message.replace("**", "")

            // Estructurar el formato para que se vea organizado
            message = message.replace("Nombre de la receta:", "<br><b>Nombre de la receta:</b><br>")
            message = message.replace("Preparación:", "<br><br><b>Preparación:</b><br>")
            message = message.replace("Ingredientes:", "<br><br><b>Ingredientes:</b><br>")
            message = message.replace("Tiempo de preparación:", "<br><br><b>Tiempo de preparación:</b> ")
            message = message.replace("Dificultad:", "<br><b>Dificultad:</b> ")
            message = message.replace("Porciones:", "<br><b>Porciones:</b> ")
            message = message.replace("Calorías:", "<br><b>Calorías:</b> ")

            // Formatear la lista de preparación como lista numerada usando Regex
            message = message.replace(Regex("(\\d+)\\. "), "<br>$1. ")

            // Reemplazar los "*" por salto de linea para ingredientes
            message = message.replace("*", "<br> ")

            message
        } else {
            // Si no es una receta con ingredientes, solo mostrarlo tal cual
            messageModel.message
        }

        val textView = TextView(context).apply {
            text = Html.fromHtml(formattedMessage, Html.FROM_HTML_MODE_LEGACY)
            textSize = 16f
            setPadding(24, 16, 24, 16)

            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(32, 12, 32, 12)
                gravity = if (messageModel.role == "user") Gravity.END else Gravity.START

                val maxTextLength = 300
                val textLength = formattedMessage.length
                width = when {
                    textLength < 50 -> ViewGroup.LayoutParams.WRAP_CONTENT
                    textLength < maxTextLength -> (context.resources.displayMetrics.widthPixels * 0.75).toInt()
                    else -> (context.resources.displayMetrics.widthPixels * 0.85).toInt()
                }
            }

            layoutParams = params

            setBackgroundResource(
                if (messageModel.role == "user") R.drawable.fondo_usuario
                else R.drawable.fondo_modelo
            )
        }
        // Añadir el TextView al contenedor de mensajes
        container.addView(textView)

    }
}