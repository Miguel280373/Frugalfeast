package com.example.frugalfeast

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class RecetasGuardadas : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var recyclerGuardadas: RecyclerView
    private lateinit var adapterGuardadas: RecetasGuardadasAdapter
    private val recetasGuardadas = mutableListOf<Receta>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recetas_guardadas)

        recyclerGuardadas = findViewById(R.id.recyclerRecetasGuardadas)
        recyclerGuardadas.layoutManager = LinearLayoutManager(this)

        adapterGuardadas = RecetasGuardadasAdapter(recetasGuardadas)
        recyclerGuardadas.adapter = adapterGuardadas

        cargarRecetasGuardadas()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }

    private fun cargarRecetasGuardadas() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        db.collection("usuarios").document(userId).collection("recetasGuardadas")
            .get()
            .addOnSuccessListener { documents ->
                recetasGuardadas.clear()
                for (document in documents) {
                    val receta = document.toObject<Receta>()
                    recetasGuardadas.add(receta)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar las recetas: $e", Toast.LENGTH_SHORT).show()
            }

        val imgVolver = findViewById<ImageView>(R.id.imageView8)

        imgVolver.setOnClickListener {
            finish()
        }

    }
}