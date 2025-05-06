package com.example.frugalfeast

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class Busqueda : AppCompatActivity(), adaptadorBusqueda.OnItemClickListener {

    private lateinit var recyclerViewBusqueda: RecyclerView
    private lateinit var editTextBusqueda: EditText
    private lateinit var botonIA: ImageView
    private lateinit var adaptador: adaptadorBusqueda
    private var listaOriginal = ArrayList<BarraBusqueda>()
    private var listaFiltrada = ArrayList<BarraBusqueda>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_busqueda)

        if (!::recyclerViewBusqueda.isInitialized) {
            initViews()
            setupRecyclerView()
            cargarRecetasDesdeFirebase()
            setupListeners()
        }
    }

    private fun initViews() {
        try {
            recyclerViewBusqueda = findViewById(R.id.recyclerViewBusqueda)
            editTextBusqueda = findViewById(R.id.editTextText9)
            botonIA = findViewById(R.id.imageView58)

            // Verificar que las vistas no sean nulas
            if (::recyclerViewBusqueda.isInitialized &&
                ::editTextBusqueda.isInitialized &&
                ::botonIA.isInitialized) {

                adaptador = adaptadorBusqueda(listaFiltrada, this)
            } else {
                throw IllegalStateException("Alguna vista no se inicializó correctamente")
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error al inicializar vistas: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun setupRecyclerView() {
        recyclerViewBusqueda.apply {
            layoutManager = LinearLayoutManager(this@Busqueda)
            addItemDecoration(DividerItemDecoration(this@Busqueda, DividerItemDecoration.VERTICAL))
            adapter = adaptador
            setHasFixedSize(true)
        }
    }

    private fun cargarRecetasDesdeFirebase() {
        db.collection("Receta")
            .get()
            .addOnSuccessListener { result ->
                listaOriginal.clear()
                for (document in result) {
                    try {
                        val receta = document.toObject(BarraBusqueda::class.java).apply {
                            id = document.id
                            nombre = nombre ?: "Sin nombre"
                            imagenUrl = imagenUrl ?: ""
                        }
                        listaOriginal.add(receta)
                    } catch (e: Exception) {
                        Log.e("Busqueda", "Error al parsear receta: ${e.message}")
                    }
                }
                actualizarListaFiltrada("")
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar recetas: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("Busqueda", "Firestore error: ${e.stackTraceToString()}")
            }
    }

    private fun setupListeners() {
        botonIA.setOnClickListener {
            startActivity(Intent(this, PresentacionIA::class.java))
        }

        editTextBusqueda.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                actualizarListaFiltrada(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun actualizarListaFiltrada(query: String) {
        listaFiltrada.clear()

        if (query.isEmpty()) {
            listaFiltrada.addAll(listaOriginal)
        } else {
            val queryLower = query.lowercase()
            listaFiltrada.addAll(listaOriginal.filter {
                it.nombre?.lowercase()?.contains(queryLower) == true
            })
        }

        adaptador.actualizarLista(listaFiltrada)

        if (listaFiltrada.isEmpty() && query.isNotEmpty()) {
            Toast.makeText(this, "No se encontraron recetas", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onItemClick(receta: BarraBusqueda) {
        try {
            val intent = Intent(this, PantallaPrincipalReceta::class.java).apply {
                putExtra("recetaId", receta.id ?: "")
                putExtra("recetaNombre", receta.nombre ?: "Sin nombre")
                putExtra("recetaImagen", receta.imagenUrl ?: "")
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Error al abrir receta", Toast.LENGTH_SHORT).show()
            Log.e("Busqueda", "Error en onItemClick: ${e.stackTraceToString()}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        recyclerViewBusqueda.adapter = null
    }
}