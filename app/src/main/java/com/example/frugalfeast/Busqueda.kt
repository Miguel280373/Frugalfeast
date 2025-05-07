package com.example.frugalfeast

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.frugalfeast.databinding.ActivityBusquedaBinding
import com.google.firebase.firestore.FirebaseFirestore

class BusquedaActivity : AppCompatActivity(), Adaptador.OnItemClickListener {

    private lateinit var binding: ActivityBusquedaBinding
    private lateinit var adaptador: Adaptador
    private var listaBusquedaOriginal = ArrayList<BarraBusqueda>()
    private var listaBusquedaFiltrada = ArrayList<BarraBusqueda>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBusquedaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        cargarRecetasDesdeFirebase()

        val botonIA: ImageView = findViewById(R.id.imageView58)
        botonIA.setOnClickListener {
            val intent = Intent(this, PresentacionIA::class.java)
            startActivity(intent)
        }

        binding.editTextText9.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim().lowercase()
                filterList(query)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupRecyclerView() {
        binding.recyclerViewBusqueda.layoutManager = LinearLayoutManager(this)
        adaptador = Adaptador(listaBusquedaFiltrada, this)
        binding.recyclerViewBusqueda.adapter = adaptador
    }

    private fun cargarRecetasDesdeFirebase() {
        db.collection("Receta")
            .get()
            .addOnSuccessListener { result ->
                listaBusquedaOriginal.clear()
                for (document in result) {
                    val receta = document.toObject(BarraBusqueda::class.java)?.copy(id = document.id) ?: BarraBusqueda()
                    listaBusquedaOriginal.add(receta)
                }
                listaBusquedaFiltrada.clear()
                listaBusquedaFiltrada.addAll(listaBusquedaOriginal)
                adaptador.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar recetas: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun filterList(query: String) {
        listaBusquedaFiltrada.clear()
        if (query.isEmpty()) {
            listaBusquedaFiltrada.addAll(listaBusquedaOriginal)
        } else {
            for (receta in listaBusquedaOriginal) {
                if (receta.nombre?.lowercase()?.contains(query) == true) {
                    listaBusquedaFiltrada.add(receta)
                }
            }
        }
        adaptador.notifyDataSetChanged()
    }

    override fun onItemClick(receta: BarraBusqueda) {
        val intent = Intent(this, PantallaPrincipalReceta::class.java)
        intent.putExtra("recetaId", receta.id)
        startActivity(intent)
    }
}
