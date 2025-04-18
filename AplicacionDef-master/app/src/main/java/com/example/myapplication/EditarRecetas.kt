package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class EditarRecetas : AppCompatActivity() {
    private lateinit var imgReceta: ImageView
    private lateinit var nombreReceta: EditText
    private lateinit var preparacion: EditText
    private lateinit var ingrediente: EditText
    private lateinit var botonGuardar: Button
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_recetas)


        imgReceta = findViewById(R.id.imageView31) // Imagen de la receta (estático)
        nombreReceta = findViewById(R.id.textView5) // Nombre de la receta
        preparacion = findViewById(R.id.textView79) // Preparación de la receta
        ingrediente = findViewById(R.id.textView81) // Ingredientes de la receta
        botonGuardar = findViewById(R.id.button13) // Botón para guardar cambios

        // Obtener el ID de la receta a editar
        val recipeId = intent.getStringExtra("recipeId")
        if (recipeId != null) {
            cargarDatosReceta(recipeId)  // Cargar datos de Firebase si existe un ID
        } else {
            Toast.makeText(this, "No se encontró el ID de la receta", Toast.LENGTH_SHORT).show()
            finish() // Cerrar la actividad si no hay un ID válido
        }

        // Configurar botón para guardar cambios
        botonGuardar.setOnClickListener {
            //guardarCambios(recipeId)
        }

        findViewById<ImageButton>(R.id.button8).setOnClickListener {
            onBackPressed()
        }
    }

    private fun cargarDatosReceta(recipeId: String?) {
        recipeId?.let {
            db.collection("recipes").document(it).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        nombreReceta.setText(document.getString("name"))
                        preparacion.setText(document.getString("preparation"))
                        ingrediente.setText(document.get("ingredients") as? String)
                    } else {
                        Toast.makeText(this, "No se encontró la receta", Toast.LENGTH_SHORT).show()
                    }

                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        "Error al cargar la receta: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }


        fun guardarCambios(recipeId: String) {

            val nombre = nombreReceta.text.toString()
            val prep = preparacion.text.toString()
            val ingredientes = ingrediente.text.toString()

            if (nombre.isEmpty() || prep.isEmpty() || ingredientes.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT)
                    .show()
                return
            }

            val recetaActualizada = hashMapOf(
                "name" to nombre,
                "preparation" to prep,
                "ingredients" to ingredientes
            )

            recipeId?.let {
                db.collection("recipes").document(it).set(recetaActualizada)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Receta actualizada exitosamente", Toast.LENGTH_SHORT)
                            .show()
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            this,
                            "Error al actualizar la receta: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }

            findViewById<ImageButton>(R.id.button8).setOnClickListener {
                onBackPressed()
            }
            db.collection("recipes").document(recipeId).set(recetaActualizada)
                .addOnSuccessListener {
                    Toast.makeText(this, "Receta actualizada exitosamente", Toast.LENGTH_SHORT)
                        .show()
                    finish() // Cerrar la actividad después de guardar
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        "Error al actualizar la receta: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }
}
