package com.example.myapplication

import MisRecetasAdapter
import Receta
import android.content.Intent
import android.os.Bundle
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
import com.google.firebase.firestore.ktx.toObject

class MisRecetas : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var recyclerMisRecetas: RecyclerView
    private lateinit var adapterMisRecetas: MisRecetasAdapter
    private val misRecetas = mutableListOf<Receta>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mis_recetas)

        recyclerMisRecetas = findViewById(R.id.recyclerMisRecetas)
        recyclerMisRecetas.layoutManager = LinearLayoutManager(this)

        adapterMisRecetas = MisRecetasAdapter(misRecetas)
        recyclerMisRecetas.adapter = adapterMisRecetas


        cargarMisRecetas()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val bottom_bar = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottom_bar.selectedItemId = R.id.nav_usuario
        bottom_bar.setOnItemSelectedListener() { item ->
            when(item.itemId) {
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
                R.id.nav_recetas_guardadas ->{
                    val intent = Intent(this, RecetasGuardadas::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }
    private fun cargarMisRecetas() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        db.collection("usuarios").document(userId).collection("MisRecetas")
            .get()
            .addOnSuccessListener { documents ->
                misRecetas.clear()
                for (document in documents) {
                    val receta = document.toObject<Receta>()
                    misRecetas.add(receta)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar las recetas: $e", Toast.LENGTH_SHORT).show()
            }
    }
}