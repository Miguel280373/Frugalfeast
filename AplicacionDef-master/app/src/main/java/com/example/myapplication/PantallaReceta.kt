package com.example.myapplication

import Receta
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PantallaReceta : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pantalla_receta)

        val recetaId = intent.getIntExtra("RECETA_ID", -1)
        if (recetaId != -1) {
            Log.d("PantallaReceta", "Receta ID recibido: $recetaId")
            cargarReceta(recetaId)
        } else {
            Log.e("PantallaReceta", "No se recibió un ID de receta válido")
            mostrarMensajeError()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Botón de regreso
        val atras = findViewById<ImageButton>(R.id.atras)
        atras.setOnClickListener {
            finish()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun cargarReceta(recetaId: Int) {
        db.collection("datosDefault").document(recetaId.toString()).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    Log.d("PantallaReceta", "Documento de receta encontrado")

                    val recetaNombre = document.getString("nombreReceta")
                    val recetaDificultad = document.getString("dificultad")
                    val recetaPorciones = document.getString("porciones")
                    val recetaTiempo = document.getString("tiempoPreparacion")
                    val recetaPreparacion = document.getString("preparacion")
                    val recetaIngredientes = document.get("ingredientes") as? List<*>
                    val recetaImagenUrl = document.getString("imagenUrl")
                    val recetaCalorias = document.getString("calorias")

                    findViewById<TextView>(R.id.nombreReceta).text = recetaNombre ?: "Sin nombre"
                    findViewById<TextView>(R.id.recetaPreparacion).text = recetaPreparacion ?: "Sin preparación"
                    findViewById<TextView>(R.id.recetaPorciones).text = recetaPorciones ?: "Sin porciones"
                    findViewById<TextView>(R.id.recetaTiempo).text = recetaTiempo ?: "Sin tiempo"
                    findViewById<TextView>(R.id.recetaDificultad).text = recetaDificultad ?: "Sin dificultad"
                    findViewById<TextView>(R.id.recetaCalorias).text = recetaCalorias ?: "Sin calorías"

                    if (recetaIngredientes != null) {
                        findViewById<TextView>(R.id.recetaIngredientes).text =
                            recetaIngredientes.joinToString("\n") { "• $it" }
                    } else {
                        findViewById<TextView>(R.id.recetaIngredientes).text = "Sin ingredientes"
                    }

                    if (!recetaImagenUrl.isNullOrEmpty()) {
                        Glide.with(this).load(recetaImagenUrl).into(findViewById(R.id.recetaImagen))
                    } else {
                        Log.d("PantallaReceta", "URL de imagen no disponible")
                    }

                } else {
                    Log.e("PantallaReceta", "El documento de receta no existe en Firestore")
                    mostrarMensajeError()
                }
            }
            .addOnFailureListener { e ->
                Log.e("PantallaReceta", "Error al obtener receta: $e")
                mostrarMensajeError()
            }
    }

    private fun mostrarMensajeError() {
        findViewById<TextView>(R.id.nombreReceta).text =
            getString(R.string.error_al_cargar_la_receta)
    }
}
