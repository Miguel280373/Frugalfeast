package com.example.frugalfeast

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import java.util.Random

class PantallaPrincipal : AppCompatActivity() {

    // Views
    private lateinit var btnIA: ImageButton
    private lateinit var recetaImagen: ImageView
    private lateinit var recetaTitulo: TextView
    private lateinit var recetaTiempo: TextView
    private lateinit var recetaPorciones: TextView
    private lateinit var recetaDificultad: TextView
    private lateinit var desayunoMenu: TextView
    private lateinit var almuerzoMenu: TextView
    private lateinit var cenaMenu: TextView
    private lateinit var btnCalcularCalorias: Button

    // Firebase
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pantalla_principal)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()


        cargarRecetaDelDia()
        cargarRecetasRecientes()
        cargarMisRecetas()

        setupListeners()
    }

    private fun initViews() {
        btnIA = findViewById(R.id.btnIA)
        recetaImagen = findViewById(R.id.recetaImagen)
        recetaTitulo = findViewById(R.id.recetaTitulo)
        recetaTiempo = findViewById(R.id.recetaTiempo)
        recetaPorciones = findViewById(R.id.recetaPorciones)
        recetaDificultad = findViewById(R.id.recetaDificultad)
        desayunoMenu = findViewById(R.id.desayunoMenu)
        almuerzoMenu = findViewById(R.id.almuerzoMenu)
        cenaMenu = findViewById(R.id.cenaMenu)
        btnCalcularCalorias = findViewById(R.id.btnCalcularCalorias)
    }

    private fun cargarRecetaDelDia() {

        val hoy = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val random = Random(hoy.toLong())

        db.collection("recetas")
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val recetas = result.documents
                    val recetaAleatoria = recetas[random.nextInt(recetas.size)]


                    recetaTitulo.text = recetaAleatoria.getString("nombre") ?: "Receta del día"
                    recetaTiempo.text = recetaAleatoria.getString("tiempoPreparacion") ?: "1 hora"
                    recetaPorciones.text = "${recetaAleatoria.getLong("porciones") ?: 4} porciones"
                    recetaDificultad.text = recetaAleatoria.getString("dificultad") ?: "Fácil"

                }
            }
            .addOnFailureListener {

                recetaTitulo.text = "Fricasé de pollo"
                recetaTiempo.text = "1 hora"
                recetaPorciones.text = "4 porciones"
                recetaDificultad.text = "Fácil"
            }
    }

    private fun cargarRecetasRecientes() {
        // Ejemplo con Firebase Firestore
        val db = FirebaseFirestore.getInstance()
        db.collection("recetas")
            .orderBy("fechaVista", Query.Direction.DESCENDING)
            .limit(3)
            .get()
            .addOnSuccessListener { result ->
                val layoutRecientes = findViewById<LinearLayout>(R.id.layoutRecientemente)
                layoutRecientes.removeAllViews()

                for (document in result) {
                    val textView = TextView(this).apply {
                        text = document.getString("nombre") ?: ""
                        textSize = 16f
                        setTypeface(null, Typeface.BOLD)
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            bottomMargin = 8.dpToPx()
                        }
                    }
                    layoutRecientes.addView(textView)
                }
            }
    }

    private fun cargarMisRecetas() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val db = FirebaseFirestore.getInstance()
        db.collection("recetas")
            .whereEqualTo("usuarioId", userId)
            .limit(3)
            .get()
            .addOnSuccessListener { result ->
                val layoutMisRecetas = findViewById<LinearLayout>(R.id.layoutMisRecetas)
                layoutMisRecetas.removeAllViews()

                for (document in result) {
                    val textView = TextView(this).apply {
                        text = document.getString("nombre") ?: ""
                        textSize = 16f
                        setTypeface(null, Typeface.BOLD)
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            bottomMargin = 8.dpToPx()
                        }
                    }
                    layoutMisRecetas.addView(textView)
                }
            }
    }

    private fun setupListeners() {
        btnIA.setOnClickListener {

            startActivity(Intent(this, PresentacionIA::class.java))
        }

        desayunoMenu.setOnClickListener { mostrarDialogoSeleccionReceta("Desayuno") }
        almuerzoMenu.setOnClickListener { mostrarDialogoSeleccionReceta("Almuerzo") }
        cenaMenu.setOnClickListener { mostrarDialogoSeleccionReceta("Cena") }


        btnCalcularCalorias.setOnClickListener {

            startActivity(Intent(this, CalcularCalorias::class.java))
        }
    }

    private fun mostrarDialogoSeleccionReceta(tipoComida: String) {
        val intent = Intent(this, RecetaAnadirMenuActivity::class.java).apply {
            putExtra("TIPO_COMIDA", tipoComida)
        }
        startActivityForResult(intent, REQUEST_SELECCION_RECETA)
    }

    private fun mostrarDialogoConfirmacion(titulo: String, mensaje: String, accionConfirmar: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle(titulo)
            .setMessage(mensaje)
            .setPositiveButton("Confirmar") { _, _ -> accionConfirmar() }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_SELECCION_RECETA && resultCode == RESULT_OK) {
            data?.let {
                val nombreReceta = it.getStringExtra("NOMBRE_RECETA") ?: return@let
                val tipoComida = it.getStringExtra("TIPO_COMIDA") ?: return@let

                mostrarDialogoConfirmacion(
                    "Confirmar",
                    "¿Deseas agregar la receta al menú?",
                    { actualizarMenu(tipoComida, nombreReceta) }
                )
            }
        }
    }

    private fun actualizarMenu(tipoComida: String, nombreReceta: String) {
        when (tipoComida) {
            "Desayuno" -> desayunoMenu.text = nombreReceta
            "Almuerzo" -> almuerzoMenu.text = nombreReceta
            "Cena" -> cenaMenu.text = nombreReceta
        }
    }

    companion object {
        private const val REQUEST_SELECCION_RECETA = 1001
    }
}