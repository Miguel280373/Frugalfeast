package com.example.frugalfeast

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import android.widget.EditText
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
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
    private lateinit var saveButton: ImageView
    //Botones IA
    private lateinit var plusButton: ImageView
    private lateinit var extraButtonsContainer: LinearLayout
    //Ultima Respuesta ChatBot
    private var lastBotResponse: String = ""

    private var hasInteracted = false

    private val fixedPrompt =  "Eres un asistente especializado en recetas de comida. principalmente puedes responder consultas sobre recetas, ingredientes, preparaciones, tiempos de cocción, sobre alimentos, frutas, verdras etc. y consejos culinarios. Si la pregunta no está relacionada con comida, responde educadamente que solo puedes proporcionar información culinaria. Para cada receta solicitada, sigue este formato:\n" +
            "* **Nombre de la receta:** \\[nombre]\n" +
            "* **Preparación:** \\[pasos detallados]\n" +
            "* **Ingredientes:**\n" +
            "  * \\[ingrediente 1]\n" +
            "  * \\[ingrediente 2]\n" +
            "  * \\[ingrediente 3]\n" +
            "* **Tiempo de preparación:** \\[número entero en horas]\n" +
            "* **Dificultad:** \\[1-3] (1 = fácil, 3 = difícil)\n" +
            "* **Porciones:** \\[número entero]\n" +
            "* **Calorías:** \\[número entero]\n" +
            "Asegúrate de mantener el formato y estructura en todas las respuestas.\n"



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
        saveButton = findViewById(R.id.saveButton)


        sendButton.setOnClickListener {
            val prompt = inputField.text.toString().trim()
            if (prompt.isNotEmpty()) {
                if (!hasInteracted) {
                    hideSuggestionCards()
                    hasInteracted = true
                }

                val fullPrompt = "$fixedPrompt\n$prompt"
                inputField.text.clear()


                // Mostrar pregunta
                chatAdapter.addMessage(MessageModel(prompt, "user"))

                // Mostrar carga durante el proceso de recepcion
                chatAdapter.addMessage(MessageModel("...", "model"))

                viewModel.sendMessage(
                    fullPrompt,
                    onStart = {},
                    onSuccess = { response ->
                        lastBotResponse = response
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


        val imgVolver = findViewById<ImageView>(R.id.btnAtrasBusqueda)

        imgVolver.setOnClickListener {
            finish() // Regresa a la actividad anterior
        }

        // Cards predefinidas con los prompts
        firstCard.setOnClickListener { sendSuggested("¿Qué puedo cocinar en 20 minutos?") }
        secondCard.setOnClickListener { sendSuggested("Dame opciones saludables para hoy.") }
        thirdCard.setOnClickListener { sendSuggested("¿Sugiereme platos fáciles de preparar y deliciosos?") }

        // Configurar nombre de usuario
        val txtNombreUsuario = findViewById<TextView>(R.id.textWelcome)
        val usuario = FirebaseAuth.getInstance().currentUser
        txtNombreUsuario.text = "¡Hola ${usuario?.displayName ?: "Invitado"}!"



        //Guardar IA
        saveButton.setOnClickListener {
            if (lastBotResponse.isNotEmpty()) {
                val extractedData = extractRecipeData(lastBotResponse)

                Log.d("ExtractRecipeData", "Datos antes de enviar: $extractedData")

                if (extractedData.isNotEmpty()) {
                    val intent = Intent(this, AgregarRecetaSeg::class.java).apply {
                        putExtra("nombre", extractedData["Nombre de la receta"] as? String ?: "")
                        putExtra("preparacion", extractedData["Preparación"] as? String ?: "")
                        putExtra("tiempo", extractedData["Tiempo de preparación"] as? String ?: "")
                        putExtra("dificultad", extractedData["Dificultad"] as? String ?: "")
                        putExtra("porciones", extractedData["Porciones"] as? String ?: "")
                        putExtra("calorias", extractedData["Calorías"] as? String ?: "")

                        // Convertir ingredientes a Array<String>
                        val ingredientesList = extractedData["Ingredientes"] as? ArrayList<String> ?: arrayListOf()
                        putExtra("ingredientes", ingredientesList.toTypedArray())
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "No se pudo extraer información válida.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "No hay respuesta del bot para guardar.", Toast.LENGTH_SHORT).show()
            }
        }

        plusButton = findViewById(R.id.plusButton)
        extraButtonsContainer = findViewById(R.id.extraButtonsContainer)

        plusButton.setOnClickListener {
            toggleExtraButtons()
        }

    }


    private fun toggleExtraButtons() {
        if (extraButtonsContainer.visibility == View.VISIBLE) {
            extraButtonsContainer.visibility = View.GONE
        } else {
            extraButtonsContainer.visibility = View.VISIBLE
        }
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


    private fun extractRecipeData(response: String): Map<String, Any> {
        val cleanedResponse = response.replace("*", "").replace("**", "").replace(":", "")
        val dataMap = mutableMapOf<String, Any>()

        Log.d("ExtractRecipeData", "----- INICIO PROCESO DE EXTRACCIÓN -----")
        Log.d("ExtractRecipeData", "Contenido completo: $cleanedResponse")

        // Capturar todo el texto en una variable
        val fullContent = cleanedResponse

        // Extraer cada sección utilizando regex
        val nombreRegex = Regex("Nombre de la receta\\s+(.+?)\\s+(Preparación|Ingredientes|Tiempo de preparación|Dificultad|Porciones|Calorías|$)")
        val preparacionRegex = Regex("Preparación\\s+((?s).+?)\\s+(Ingredientes|Tiempo de preparación|Dificultad|Porciones|Calorías|$)")
        val ingredientesRegex = Regex("Ingredientes\\s+((?s).+?)\\s+(Tiempo de preparación|Dificultad|Porciones|Calorías|$)")
        val tiempoRegex = Regex("Tiempo de preparación\\s+(.+?)\\s+(Dificultad|Porciones|Calorías|$)")
        val dificultadRegex = Regex("Dificultad\\s+(.+?)\\s+(Porciones|Calorías|$)")
        val porcionesRegex = Regex("Porciones\\s+(.+?)\\s+(Calorías|$)")
        val caloriasRegex = Regex("Calorías\\s+(.+)")

        // Asignar valores utilizando los regex
        dataMap["Nombre de la receta"] = nombreRegex.find(fullContent)?.groupValues?.get(1)?.trim() ?: ""
        dataMap["Preparación"] = preparacionRegex.find(fullContent)?.groupValues?.get(1)?.trim() ?: ""

        // Ingredientes: convertir a lista
        val ingredientesText = ingredientesRegex.find(fullContent)?.groupValues?.get(1)?.trim() ?: ""
        val ingredientesList = ingredientesText.split("\n").map { it.trim() }.filter { it.isNotEmpty() }
        dataMap["Ingredientes"] = ingredientesList

        dataMap["Tiempo de preparación"] = tiempoRegex.find(fullContent)?.groupValues?.get(1)?.trim() ?: ""
        dataMap["Dificultad"] = dificultadRegex.find(fullContent)?.groupValues?.get(1)?.trim() ?: ""
        dataMap["Porciones"] = porcionesRegex.find(fullContent)?.groupValues?.get(1)?.trim() ?: ""
        dataMap["Calorías"] = caloriasRegex.find(fullContent)?.groupValues?.get(1)?.trim() ?: ""

        Log.d("ExtractRecipeData", "Datos extraídos: $dataMap")
        Log.d("ExtractRecipeData", "----- FIN PROCESO DE EXTRACCIÓN -----")

        return dataMap
    }

}