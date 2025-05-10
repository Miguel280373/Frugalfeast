package com.example.frugalfeast

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import java.util.Date

class RecetasGuardadas : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var recyclerGuardadas: RecyclerView
    private lateinit var adapterGuardadas: RecetasGuardadasAdapter
    private val recetasGuardadas = mutableListOf<Receta>()
    private var modoSeleccion: Boolean = false
    private var tipoComida: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recetas_guardadas)

        // Configuración inicial
        modoSeleccion = intent.getBooleanExtra("MODO_SELECCION", false)
        tipoComida = intent.getStringExtra("TIPO_COMIDA")

        setupRecyclerView()
        setupBackButton()
        cargarRecetasGuardadas()

       /*adapterGuardadas.notifyDataSetChanged()

        if (recetasGuardadas.isEmpty()) {
            Toast.makeText(this, "No tienes recetas guardadas aún", Toast.LENGTH_SHORT).show()
        }*/

    }

    private fun setupRecyclerView() {
        recyclerGuardadas = findViewById(R.id.recyclerRecetasGuardadas)
        recyclerGuardadas.layoutManager = LinearLayoutManager(this)

        adapterGuardadas = RecetasGuardadasAdapter(
            mode = if (modoSeleccion) RecetaMode.SELECT_FOR_MENU else RecetaMode.VIEW_DETAILS,
            recetas = recetasGuardadas,
            onItemClick = { receta -> if (!modoSeleccion) abrirDetallesReceta(receta) },
            onSelectForMenu = { receta -> if (modoSeleccion) mostrarDialogoConfirmacion(receta) }
        )

        recyclerGuardadas.adapter = adapterGuardadas
    }

    private fun setupBackButton() {
        findViewById<ImageButton>(R.id.btn_atras_guardadas).setOnClickListener { finish() }
    }

    private fun cargarRecetasGuardadas() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            Toast.makeText(this, "No se pudo identificar al usuario", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("favoritos").document(userId).collection("recetas_favoritas")
            .get()
            .addOnSuccessListener { documents ->
                recetasGuardadas.clear()
                documents.forEach { document ->
                    try {
                        val receta = document.toObject(Receta::class.java).apply {
                            id = document.id
                            fechaCreacion = document.getDate("fechaGuardado") ?: Date()
                        }
                        recetasGuardadas.add(receta)
                    } catch (e: Exception) {
                        Log.e("RecetasGuardadas", "Error al mapear receta: ${e.message}")
                    }
                }

                adapterGuardadas.notifyDataSetChanged()

                val prefs = getSharedPreferences("frugalfeast_prefs", Context.MODE_PRIVATE)
                prefs.edit().putInt("contador_guardadas", recetasGuardadas.size).apply()

                if (recetasGuardadas.isEmpty()) {
                    Toast.makeText(this, "No tienes recetas guardadas aún", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar recetas: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("RecetasGuardadas", "Error al cargar recetas", e)
            }
    }

    private fun abrirDetallesReceta(receta: Receta) {
        Intent(this, PantallaPrincipalReceta::class.java).apply {
            putExtra("receta_id", receta.id)
            putExtra("receta_nombre", receta.nombre)
            putExtra("receta_imagen", receta.imagenUrl)
            putExtra("receta_tiempo", receta.tiempo?.toString() ?: "0")
            putExtra("receta_porciones", receta.porciones?.toString() ?: "0")
            putExtra("receta_dificultad", receta.dificultad?.toString() ?: "1")
            putExtra("receta_preparacion", receta.preparacion ?: "")
            putStringArrayListExtra("receta_ingredientes", ArrayList(receta.ingredientes ?: emptyList()))
            putExtra("receta_userId", receta.userId ?: "")
            startActivity(this)
        }
    }

    private fun mostrarDialogoConfirmacion(receta: Receta) {
        tipoComida?.let { comida ->
            AlertDialog.Builder(this)
                .setTitle("Agregar al menú")
                .setMessage("¿Deseas agregar '${receta.nombre}' a tu menú de $comida?")
                .setPositiveButton("Sí") { _, _ ->
                    guardarRecetaEnMenu(receta)
                    Toast.makeText(this, "Receta agregada al menú", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        } ?: run {
            Toast.makeText(this, "Error: tipo de comida no especificado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun guardarRecetaEnMenu(receta: Receta) {
        tipoComida?.let { comida ->
            getSharedPreferences("menu_del_dia", Context.MODE_PRIVATE).edit {
                putString("${comida}_id", receta.id)
            }
            setResult(RESULT_OK, Intent().apply {
                putExtra("RECETA_ID", receta.id)
                putExtra("TIPO_COMIDA", comida)
            })
        }
    }

}