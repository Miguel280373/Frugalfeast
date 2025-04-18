package com.example.myapplication

import Receta
import android.content.Intent
import android.widget.Toast
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class ResultadosBusqueda : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var recyclerBusqueda: RecyclerView
    private lateinit var busquedaAdapter: BusquedaAdapter
    private val recetasEncontradas = mutableListOf<Receta>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_resultados_busqueda)

       recyclerBusqueda = findViewById(R.id.recyclerResultados)
       recyclerBusqueda.layoutManager = LinearLayoutManager(this)
       busquedaAdapter = BusquedaAdapter(recetasEncontradas) { receta, _ ->
           val intent = Intent(this, PantallaReceta::class.java)
           intent.putExtra("RECETA_ID", receta.recetaId)
           startActivity(intent)
       }
       recyclerBusqueda.adapter = busquedaAdapter
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val atras = findViewById<ImageButton>(R.id.atras)
        atras.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }
        val searchView = findViewById<SearchView>(R.id.buscarRecetaBar)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    buscarRecetas(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        val query = intent.getStringExtra("query")
        if (!query.isNullOrEmpty()) {
            searchView.setQuery(query, false)
            buscarRecetas(query)
        }
    }
    private fun buscarRecetas(query: String) {
        val queryLower = query.lowercase()

        db.collection("datosDefault")
            .whereGreaterThanOrEqualTo("nombreReceta", queryLower)
            .whereLessThan("nombreReceta", queryLower + "\uf8ff")
            .get()
            .addOnSuccessListener { documents ->
                recetasEncontradas.clear()
                for (document in documents) {
                    val receta = document.toObject<Receta>()
                    recetasEncontradas.add(receta)
                    Log.d("BusquedaRecetas", "Receta encontrada: ${receta.nombreReceta}")
                }
                if (recetasEncontradas.isEmpty()) {
                    Log.d("BusquedaRecetas", "No se encontraron recetas")
                    Toast.makeText(this, "No se encontraron recetas", Toast.LENGTH_SHORT).show()
                }
                busquedaAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e("BusquedaRecetas", "Error al buscar recetas", exception)
                Toast.makeText(this, "Error al buscar recetas", Toast.LENGTH_SHORT).show()
            }
    }

}