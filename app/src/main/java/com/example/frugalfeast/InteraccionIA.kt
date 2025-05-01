package com.example.frugalfeast

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.View
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import android.widget.EditText
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

class InteraccionIA : AppCompatActivity() {

    private lateinit var viewModel: ChatViewModel
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var scrollLayout: LinearLayout
    private lateinit var firstCard: LinearLayout
    private lateinit var secondCard: LinearLayout
    private lateinit var thirdCard: LinearLayout
    private lateinit var inputField: EditText
    private lateinit var sendButton: ImageView
    private lateinit var scrollView: ScrollView

    private var hasInteracted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interaccion_ia)

        // Inicializar componentes
        viewModel = ViewModelProvider(this)[ChatViewModel::class.java]
        scrollView = findViewById(R.id.scrollContainer)
        scrollLayout = scrollView.findViewById<LinearLayout>(R.id.chatContainer)
        chatAdapter = ChatAdapter(this, scrollLayout)

        firstCard = findViewById(R.id.firstCard)
        secondCard = findViewById(R.id.secondCard)
        thirdCard = findViewById(R.id.thirdCard)
        inputField = findViewById(R.id.editPrompt)
        sendButton = findViewById(R.id.imageUploadButton)

        sendButton.setOnClickListener {
            val prompt = inputField.text.toString().trim()
            if (prompt.isNotEmpty()) {
                if (!hasInteracted) {
                    hideSuggestionCards()
                    hasInteracted = true
                }

                inputField.text.clear()

                // Mostrar pregunta
                chatAdapter.addMessage(MessageModel(prompt, "user"))

                // Mostrar carga durante el proceso de recepcion
                chatAdapter.addMessage(MessageModel("...", "model"))

                viewModel.sendMessage(
                    prompt,
                    onStart = {},
                    onSuccess = { response ->
                        removeLastMessage()
                        chatAdapter.addMessage(MessageModel(response, "model"))
                        scrollToBottom()
                    },
                    onError = { error ->
                        removeLastMessage()
                        chatAdapter.addMessage(MessageModel(error, "model"))
                        scrollToBottom()
                    }
                )
            }
        }

        // Cards predefinidas con los prompts
        firstCard.setOnClickListener { sendSuggested("¿Qué puedo cocinar en 20 minutos?") }
        secondCard.setOnClickListener { sendSuggested("Dame opciones saludables para hoy.") }
        thirdCard.setOnClickListener { sendSuggested("¿Sugiereme platos fáciles de preparar y deliciosos?") }

        // Configurar nombre de usuario
        val txtNombreUsuario = findViewById<TextView>(R.id.textWelcome)
        val usuario = FirebaseAuth.getInstance().currentUser
        txtNombreUsuario.text = "¡Hola ${usuario?.displayName ?: "Invitado"}!"
    }

    private fun sendSuggested(prompt: String) {
        inputField.setText(prompt)
        sendButton.performClick()
    }

    private fun removeLastMessage() {
        if (scrollLayout.childCount > 0) {
            scrollLayout.removeViewAt(scrollLayout.childCount - 1)
        }
    }

    private fun scrollToBottom() {
        scrollView.post {
            scrollView.fullScroll(View.FOCUS_DOWN)
        }
    }

    private fun hideSuggestionCards() {
        firstCard.visibility = View.GONE
        secondCard.visibility = View.GONE
        thirdCard.visibility = View.GONE
    }
}

