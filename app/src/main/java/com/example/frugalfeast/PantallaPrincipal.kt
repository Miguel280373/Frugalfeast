package com.example.frugalfeast

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PantallaPrincipal : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var searchView: SearchView
    private lateinit var recyclerRecientemente: RecyclerView
    private lateinit var recyclerMisRecetas: RecyclerView
    private lateinit var textoRecientemente: TextView
    private lateinit var textoMisRecetas: TextView
    private lateinit var btnCalcularCalorias: Button
    private lateinit var btnAgregar: ImageButton

    private val db = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    private lateinit var recienteAdapter: RecetaAdapter
    private lateinit var misRecetasAdapter: RecetaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_principal)

        inicializarViews()
        //configurarListeners()
        cargarRecetaDelDia()
        //loadVistoRecientemente()
        //loadMisRecetas()

        val headerView = navigationView.getHeaderView(0)
        val txtNombreUsuario = headerView.findViewById<TextView>(R.id.txtNombreUsuario)

        setupToolbarAndDrawer()

        btnCalcularCalorias.setOnClickListener {
            startActivity(Intent(this, CalcularCalorias::class.java))
        }

        btnAgregar.setOnClickListener {
            startActivity(Intent(this, AgregarReceta::class.java))
        }
        val menuButton: ImageButton = findViewById(R.id.ic_menu)
        menuButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)  // Abre el DrawerLayout
        }
        val usuario = FirebaseAuth.getInstance().currentUser
        txtNombreUsuario.text = usuario?.displayName ?: "Invitado"

    }

    private fun inicializarViews() {
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)

       // searchView = findViewById(R.id.buscarRecetaBar)
        recyclerRecientemente = findViewById(R.id.recyclerRecientemente)
        recyclerMisRecetas = findViewById(R.id.recyclerMisRecetas)
        textoRecientemente = findViewById(R.id.textoRecientemente)
        textoMisRecetas = findViewById(R.id.textoMisRecetas)
        btnCalcularCalorias = findViewById(R.id.btnCalcularCalorias)
        btnAgregar = findViewById(R.id.agregar)

        //recyclerRecientemente.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        //recyclerMisRecetas.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        //recienteAdapter = RecetaAdapter(emptyList()) { receta -> onRecetaClicked(receta) }
        //misRecetasAdapter = RecetaAdapter(emptyList()) { receta -> onRecetaClicked(receta) }

       // recyclerRecientemente.adapter = recienteAdapter
       // recyclerMisRecetas.adapter = misRecetasAdapter
    }
    private fun setupToolbarAndDrawer() {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            val handled = when (menuItem.itemId) {
                R.id.nav_recetas_guardadas -> {
                    //startActivity(Intent(this, RecetasGuardadas::class.java))
                    true
                }

                R.id.nav_mi_menu -> {
                    //startActivity(Intent(this, MiMenu::class.java))
                    true
                }

                R.id.nav_configuracion -> {
                    //startActivity(Intent(this, Configuracion::class.java))
                    true
                }

                R.id.nav_cerrar_sesion -> {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this, Login::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    true
                }

                else -> false
            }

            drawerLayout.closeDrawer(GravityCompat.START)
            handled
        }
    }

    private fun cargarRecetaDelDia() {
        db.collection("Receta").get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val listaRecetas = result.documents
                    val randomReceta = listaRecetas.random()

                    val titulo = randomReceta.getString("nombre") ?: ""
                    val tiempo = randomReceta.getString("tiempo") ?: ""
                    val porciones = randomReceta.getString("porciones") ?: ""
                    val dificultad = randomReceta.getString("dificultad") ?: ""

                    val tvNombreReceta = findViewById<TextView>(R.id.tvNombreReceta)
                    val tvTiempo = findViewById<TextView>(R.id.tvTiempo)
                    val tvPorciones = findViewById<TextView>(R.id.tvPorciones)
                    val tvDificultad = findViewById<TextView>(R.id.tvDificultad)

                    tvNombreReceta.text = titulo
                    tvTiempo.text = "Tiempo: $tiempo"
                    tvPorciones.text = "Porciones: $porciones"
                    tvDificultad.text = "Dificultad: $dificultad"
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al cargar la receta", Toast.LENGTH_SHORT).show()
            }
    }





    /*private fun configurarListeners() {
    searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            /*val intent = Intent(this@PantallaPrincipal, BuscarRecetasActivity::class.java)
            intent.putExtra("query", query)
            startActivity(intent)*/
            return false
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            return false
        }
    })


}

private fun cargarRecetaDelDia() {
    db.collection("recipes").get()
        .addOnSuccessListener { result ->
            if (!result.isEmpty) {
                val listaRecetas = result.documents
                val randomReceta = listaRecetas.random()

                val titulo = randomReceta.getString("nombre") ?: ""
                val tiempo = randomReceta.getString("tiempo") ?: ""
                val porciones = randomReceta.getString("porciones") ?: ""
                val dificultad = randomReceta.getString("dificultad") ?: ""

                // falta codigo para actualizar
            }
        }
        .addOnFailureListener {
            // Manejo de error
        }
}

private fun loadVistoRecientemente() {
    currentUser?.let { user ->
        db.collection("users").document(user.uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val viewedList = document.get("viewedRecipes") as? List<String>
                    if (!viewedList.isNullOrEmpty()) {
                        val ultimos3 = viewedList.takeLast(3).reversed()

                        textoRecientemente.visibility = View.GONE
                        recyclerRecientemente.visibility = View.VISIBLE

                        db.collection("recipes").whereIn("__name__", ultimos3).get()
                            .addOnSuccessListener { snapshots ->
                                val recetasVistas = snapshots.documents.map { snap ->
                                    Receta(
                                        id = snap.id,
                                        nombre = snap.getString("title") ?: "",
                                        imageUrl = snap.getString("imageUrl") ?: "",
                                        tiempo = snap.getString("time") ?: "",
                                        porciones = snap.getString("portions") ?: "",
                                        dificultad = snap.getString("difficulty") ?: "",
                                        preparacion = snap.getString("preparation") ?: "",
                                        ingredientes = snap.get("ingredients") as? List<String> ?: emptyList()
                                    )
                                }
                                recienteAdapter.updateData(recetasVistas)
                            }
                    } else {
                        textoRecientemente.visibility = View.VISIBLE
                        recyclerRecientemente.visibility = View.GONE
                    }
                }
            }
    }
}

private fun loadMisRecetas() {
    currentUser?.let { user ->
        db.collection("users").document(user.uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val myRecipesList = document.get("myRecipes") as? List<String>
                    if (!myRecipesList.isNullOrEmpty()) {
                        val ultimas3 = myRecipesList.takeLast(3).reversed()

                        textoMisRecetas.visibility = View.GONE
                        recyclerMisRecetas.visibility = View.VISIBLE

                        db.collection("recipes").whereIn("__name__", ultimas3).get()
                            .addOnSuccessListener { snapshots ->
                                val misRecetas = snapshots.documents.map { snap ->
                                    Receta(
                                        id = snap.id,
                                        nombre = snap.getString("title") ?: "",
                                        imageUrl = snap.getString("imageUrl") ?: "",
                                        tiempo = snap.getString("time") ?: "",
                                        porciones = snap.getString("portions") ?: "",
                                        dificultad = snap.getString("difficulty") ?: "",
                                        preparacion = snap.getString("preparation") ?: "",
                                        ingredientes = snap.get("ingredients") as? List<String> ?: emptyList()
                                    )
                                }
                                misRecetasAdapter.updateData(misRecetas)
                            }
                    } else {
                        textoMisRecetas.visibility = View.VISIBLE
                        recyclerMisRecetas.visibility = View.GONE
                    }
                }
            }
    }
}

private fun onRecetaClicked(receta: Receta) {
    val intent = Intent(this, PantallaPrincipalReceta::class.java)
    intent.putExtra("RECETA_ID", receta.id)
    startActivity(intent)

    currentUser?.let { user ->
        val userRef = db.collection("users").document(user.uid)
        userRef.get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val viewedList = doc.get("viewedRecipes") as? MutableList<String> ?: mutableListOf()
                    viewedList.add(receta.id)
                    if (viewedList.size > 10) {
                        viewedList.removeAt(0)
                    }
                    userRef.update("viewedRecipes", viewedList)
                }
            }
    }
}



}*/

}

