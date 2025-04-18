package com.example.myapplication

import Receta
import RecetasGuardadasAdapter
import com.example.myapplication.Usuario
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
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

        // Inicializar RecyclerView
        recyclerGuardadas = findViewById(R.id.recyclerRecetasGuardadas)
        recyclerGuardadas.layoutManager = LinearLayoutManager(this)

        // Inicializar el adaptador con la lista de recetas
        adapterGuardadas = RecetasGuardadasAdapter(recetasGuardadas)
        recyclerGuardadas.adapter = adapterGuardadas

        // Cargar las recetas guardadas
        cargarRecetasGuardadas()

        // Ajustar el margen superior en dispositivos con barras de sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configuración del BottomNavigation
        val bottom_bar = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottom_bar.selectedItemId = R.id.nav_recetas_guardadas
        bottom_bar.setOnItemSelectedListener() { item ->
            when (item.itemId) {
                R.id.nav_usuario -> {
                    val intent = Intent(this, Usuario::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_inicio -> {
                    val intent = Intent(this, Recetas::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_recetas_guardadas -> {
                    val intent = Intent(this, RecetasGuardadas::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    // Cargar recetas guardadas desde Firestore
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
                // Notificar al adaptador para que actualice la vista
                adapterGuardadas.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar las recetas: $e", Toast.LENGTH_SHORT).show()
            }
    }
}