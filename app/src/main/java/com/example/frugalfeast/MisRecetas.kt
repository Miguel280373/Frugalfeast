package com.example.frugalfeast

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.frugalfeast.MisRecetasAdapter
import com.example.frugalfeast.R
import com.example.frugalfeast.Receta
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

        adapterMisRecetas = MisRecetasAdapter(misRecetas) { receta ->
            Toast.makeText(this, "Receta seleccionada: ${receta.nombre}", Toast.LENGTH_SHORT).show()
        }
        recyclerMisRecetas.adapter = adapterMisRecetas


        cargarMisRecetas()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun cargarMisRecetas() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        db.collection("Receta")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                misRecetas.clear()
                for (document in documents) {
                    val receta = document.toObject<Receta>().apply { id = document.id }
                    misRecetas.add(receta)
                }
                adapterMisRecetas.notifyDataSetChanged()

                // Guardamos el conteo
                getSharedPreferences("frugalfeast_prefs", MODE_PRIVATE).edit()
                    .putInt("contador_creadas", misRecetas.size)
                    .apply()
            }

        val imgVolver = findViewById<ImageView>(R.id.btn_atras_mis_recetas)

        imgVolver.setOnClickListener {
            finish()
        }

    }
}