package com.example.myapplication

import Receta
import RecientementeAdapter
import android.content.Intent
import android.os.Bundle
import android.view.MenuInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class Recetas : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var recientementeAdapter: RecientementeAdapter
    private lateinit var recyclerRecientemente : RecyclerView
    private lateinit var MisRecetasAdapter: RecientementeAdapter
    private lateinit var recyclerMisRecetas : RecyclerView
    private val recetasRecientes = mutableListOf<Receta>()
    private val misRecetas= mutableListOf<Receta>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.fragment_recetas1)

        recyclerRecientemente = findViewById(R.id.recyclerRecientemente)
        recientementeAdapter = RecientementeAdapter(recetasRecientes) { receta, view ->
            mostrarMenuContextualRecientes(receta, view)
        }
        recyclerRecientemente.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerRecientemente.adapter = recientementeAdapter

        recyclerMisRecetas = findViewById(R.id.recyclerMisRecetas)
        MisRecetasAdapter = RecientementeAdapter(misRecetas) { receta, view ->
            mostrarMenuContextualMisRecetas(receta, view)
        }
        recyclerMisRecetas.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerMisRecetas.adapter = MisRecetasAdapter

        val bottom_bar = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottom_bar.selectedItemId = R.id.nav_inicio
        bottom_bar.setOnItemSelectedListener() { item ->
            when(item.itemId) {
                R.id.nav_usuario -> {
                    val intent = Intent(this, Usuario::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_inicio -> {
                    val intent = Intent(this, Recetas::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_recetas_guardadas ->{
                    val intent = Intent(this, RecetasGuardadas::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }

        cargarRecetasRecientes()
        verificarRecetasRecientes()
        cargarMisRecetas()
        verificarMisRecetas()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val calcularCalorias = findViewById<Button>(R.id.btnCalcularCalorias)
       calcularCalorias.setOnClickListener {
            val intent = Intent(this, CalcularCalorias::class.java)
            startActivity(intent)
        }

        val agregar = findViewById<ImageButton>(R.id.agregar)
        agregar.setOnClickListener{
            val intent = Intent(this, AgregarReceta::class.java)
            startActivity(intent)
        }

        val searchView = findViewById<SearchView>(R.id.buscarRecetaBar)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    val intent = Intent(this@Recetas, ResultadosBusqueda::class.java)
                    intent.putExtra("query", query)
                    startActivity(intent)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

    }


    // RECETAS RECIENTES
    private fun verificarRecetasRecientes() {
        val textoMensaje = findViewById<TextView>(R.id.textoRecientemente)

        if (recetasRecientes.isEmpty()) {
            textoMensaje.visibility = View.VISIBLE
        } else {
            textoMensaje.visibility = View.GONE
        }
    }

    private fun cargarRecetasRecientes() {
        db.collection("usuarios").document(FirebaseAuth.getInstance().currentUser?.uid ?: return)
            .collection("recetasRecientes")
            .limit(3)
            .get()
            .addOnSuccessListener { documents ->
                recetasRecientes.clear()
                for (document in documents) {
                    val receta = document.toObject<Receta>()
                    recetasRecientes.add(receta)
                }
                verificarRecetasRecientes()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar recetas recientes: $e", Toast.LENGTH_SHORT).show()
            }
    }


    private fun guardarRecetaEnFirebase(receta: Receta) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        db.collection("usuarios").document(userId).collection("recetasGuardadas")
            .add(receta)
            .addOnSuccessListener {
                Toast.makeText(this, "Receta guardada exitosamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al guardar la receta: $e", Toast.LENGTH_SHORT).show()
            }
    }



    private fun mostrarMenuContextualRecientes(receta: Receta, view: View) {
        val popup = PopupMenu(this, view)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.menu_receta, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.guardar -> {
                    guardarRecetaEnFirebase(receta)
                    true
                }
                R.id.eliminar -> {
                    recetasRecientes.remove(receta)
                    verificarRecetasRecientes()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    //MIS RECETAS

    private fun verificarMisRecetas() {
        val textoMensaje = findViewById<TextView>(R.id.textoMisRecetas)

        if (misRecetas.isEmpty()) {
            textoMensaje.visibility = View.VISIBLE
        } else {
            textoMensaje.visibility = View.GONE
        }
    }
    private fun mostrarMenuContextualMisRecetas(receta: Receta, view: View) {
        val popup = PopupMenu(this, view)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.menu_receta_2, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.editar -> {
                    true
                }
                R.id.eliminar -> {
                    recetasRecientes.remove(receta)
                    verificarRecetasRecientes()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }
    fun cargarMisRecetas() {
        db.collection("usuarios").document(FirebaseAuth.getInstance().currentUser?.uid ?: return)
            .collection("recetasGuardadas")
            .limit(3)
            .get()
            .addOnSuccessListener { documents ->
                recetasRecientes.clear()
                for (document in documents) {
                    val receta = document.toObject<Receta>()
                    recetasRecientes.add(receta)
                }
                verificarRecetasRecientes()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar recetas recientes: $e", Toast.LENGTH_SHORT).show()
            }
    }



}
