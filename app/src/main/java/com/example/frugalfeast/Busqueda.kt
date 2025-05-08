package com.example.frugalfeast

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class Busqueda : AppCompatActivity(), AdaptadorBusqueda.OnItemClickListener {

    private lateinit var recyclerViewBusqueda: RecyclerView
    private lateinit var barraBusqueda: EditText
    private lateinit var adaptador: AdaptadorBusqueda
    private var listaBusquedaOriginal = ArrayList<BarraBusqueda>()
    private var listaBusquedaFiltrada = ArrayList<BarraBusqueda>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_busqueda)

        recyclerViewBusqueda = findViewById(R.id.recyclerViewBusqueda)
        barraBusqueda = findViewById(R.id.barraBusqueda)

        setupRecyclerView()

        val queryText = intent.getStringExtra("query") ?: ""
        barraBusqueda.setText(queryText)

        val botonIA: ImageView = findViewById(R.id.imageView58)
        botonIA.setOnClickListener {
            val intent = Intent(this, PresentacionIA::class.java)
            startActivity(intent)
        }

        val btnAtras: ImageView = findViewById(R.id.btnAtrasBusqueda)
        btnAtras.setOnClickListener {
            finish()
        }

        barraBusqueda.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim().lowercase()
                if (query.isNotEmpty()) {
                    filterList(query)
                } else {
                    listaBusquedaFiltrada.clear()
                    adaptador.notifyDataSetChanged()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        if (queryText.isNotEmpty()) {
            barraBusqueda.post {
                filterList(queryText)
            }
        }
    }

    private fun setupRecyclerView() {
        recyclerViewBusqueda.layoutManager = LinearLayoutManager(this)
        adaptador = AdaptadorBusqueda(listaBusquedaFiltrada, this)
        recyclerViewBusqueda.adapter = adaptador
    }

    private fun filterList(query: String) {
        listaBusquedaFiltrada.clear()

        if (listaBusquedaOriginal.isEmpty()) {
            db.collection("Receta")
                .get()
                .addOnSuccessListener { result ->
                    listaBusquedaOriginal.clear()
                    for (document in result) {
                        val receta = document.toObject(BarraBusqueda::class.java)?.copy(id = document.id) ?: BarraBusqueda()
                        listaBusquedaOriginal.add(receta)
                    }
                    actualizarListaFiltrada(query)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al cargar recetas: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            actualizarListaFiltrada(query)
        }
    }

    private fun actualizarListaFiltrada(query: String) {
        listaBusquedaFiltrada.clear()
        for (receta in listaBusquedaOriginal) {
            if (receta.nombre?.lowercase()?.contains(query) == true) {
                listaBusquedaFiltrada.add(receta)
            }
        }
        adaptador.notifyDataSetChanged()

        if (listaBusquedaFiltrada.isEmpty() && query.isNotEmpty()) {
            Toast.makeText(this, "No se encontraron recetas", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onItemClick(receta: BarraBusqueda) {
        val intent = Intent(this, PantallaPrincipalReceta::class.java)
        intent.putExtra("recetaId", receta.id)
        startActivity(intent)
    }
}