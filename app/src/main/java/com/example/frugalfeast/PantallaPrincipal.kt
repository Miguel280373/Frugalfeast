package com.example.frugalfeast

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.cardview.widget.CardView
import androidx.core.content.edit
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class PantallaPrincipal : AppCompatActivity() {

    // Views principales
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var searchView: SearchView
    private lateinit var btnMenu: ImageButton

    // Receta del día
    private lateinit var cardRecetaDia: CardView
    private lateinit var imgRecetaDia: ImageView
    private lateinit var tvNombreRecetaDia: TextView
    private lateinit var tvPorcionesRecetaDia: TextView
    private lateinit var tvTiempoRecetaDia: TextView
    private lateinit var tvDificultadRecetaDia: TextView

    // Visto recientemente
    private lateinit var cardVistoRecientemente: CardView
    private lateinit var imgVistoRecientemente: ImageView
    private lateinit var tvNombreRecetaReciente: TextView
    private lateinit var tvPorcionesRecetaReciente: TextView
    private lateinit var tvTiempoRecetaReciente: TextView
    private lateinit var tvDificultadRecetaReciente: TextView

    // Menú del día
    private lateinit var cardDesayuno: CardView
    private lateinit var layoutDesayunoVacio: LinearLayout
    private lateinit var layoutDesayunoConReceta: LinearLayout
    private lateinit var tvDesayunoNombre: TextView
    private lateinit var tvDesayunoTiempo: TextView
    private lateinit var tvDesayunoDificultad: TextView

    private lateinit var cardAlmuerzo: CardView
    private lateinit var layoutAlmuerzoVacio: LinearLayout
    private lateinit var layoutAlmuerzoConReceta: LinearLayout
    private lateinit var tvAlmuerzoNombre: TextView
    private lateinit var tvAlmuerzoTiempo: TextView
    private lateinit var tvAlmuerzoDificultad: TextView

    private lateinit var cardCena: CardView
    private lateinit var layoutCenaVacio: LinearLayout
    private lateinit var layoutCenaConReceta: LinearLayout
    private lateinit var tvCenaNombre: TextView
    private lateinit var tvCenaTiempo: TextView
    private lateinit var tvCenaDificultad: TextView

    private val db = FirebaseFirestore.getInstance()

    // Mis Recetas
    private lateinit var cardMisRecetas: CardView
    private lateinit var layoutMisRecetasVacio: LinearLayout
    private lateinit var layoutMisRecetasContenido: LinearLayout
    private lateinit var imgMisRecetas: ImageView
    private lateinit var tvNombreMisRecetas: TextView
    private lateinit var tvTiempoMisRecetas: TextView
    private lateinit var tvPorcionesMisRecetas: TextView
    private lateinit var tvDificultadMisRecetas: TextView
    private lateinit var btnAgregarReceta: ImageView
    private lateinit var btnCrearReceta: Button
    private lateinit var btnCalularCalorias: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_principal)

        initViews()
        setupNavigationDrawer()
        setupMenuDelDia()
        cargarRecetaDelDia()
        cargarVistoRecientemente()
        cargarMisRecetas()

        btnCalularCalorias.setOnClickListener(){
            val intent = Intent(this, CalcularCalorias::class.java)
            startActivity(intent)
        }
        btnAgregarReceta.setOnClickListener {
            navegarAgregarReceta()
        }

        btnCrearReceta.setOnClickListener {
            navegarAgregarReceta()
        }


    }


    private fun initViews() {

        // Views principales
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_drawer)
        searchView = findViewById(R.id.search_view)
        btnMenu = findViewById(R.id.btn_menu)

        // Receta del día
        cardRecetaDia = findViewById(R.id.card_receta_dia)
        imgRecetaDia = findViewById(R.id.img_receta_dia)
        tvNombreRecetaDia = findViewById(R.id.tv_nombre_receta_dia)
        tvPorcionesRecetaDia = findViewById(R.id.tv_porciones_receta_dia)
        tvTiempoRecetaDia = findViewById(R.id.tv_tiempo_receta_dia)
        tvDificultadRecetaDia = findViewById(R.id.tv_dificultad_receta_dia)

        // Visto recientemente
        cardVistoRecientemente = findViewById(R.id.card_visto_recientemente)
        imgVistoRecientemente = findViewById(R.id.img_visto_recientemente)
        tvNombreRecetaReciente = findViewById(R.id.tv_nombre_receta_reciente)
        tvPorcionesRecetaReciente = findViewById(R.id.tv_porciones_receta_reciente)
        tvTiempoRecetaReciente = findViewById(R.id.tv_tiempo_receta_reciente)
        tvDificultadRecetaReciente = findViewById(R.id.tv_dificultad_receta_reciente)

        // Menú del día
        cardDesayuno = findViewById(R.id.card_desayuno)
        layoutDesayunoVacio = findViewById(R.id.layout_desayuno_vacio)
        layoutDesayunoConReceta = findViewById(R.id.layout_desayuno_con_receta)
        tvDesayunoNombre = findViewById(R.id.tv_desayuno_nombre)
        tvDesayunoTiempo = findViewById(R.id.tv_desayuno_tiempo)
        tvDesayunoDificultad = findViewById(R.id.tv_desayuno_dificultad)

        cardAlmuerzo = findViewById(R.id.card_almuerzo)
        layoutAlmuerzoVacio = findViewById(R.id.layout_almuerzo_vacio)
        layoutAlmuerzoConReceta = findViewById(R.id.layout_almuerzo_con_receta)
        tvAlmuerzoNombre = findViewById(R.id.tv_almuerzo_nombre)
        tvAlmuerzoTiempo = findViewById(R.id.tv_almuerzo_tiempo)
        tvAlmuerzoDificultad = findViewById(R.id.tv_almuerzo_dificultad)

        cardCena = findViewById(R.id.card_cena)
        layoutCenaVacio = findViewById(R.id.layout_cena_vacio)
        layoutCenaConReceta = findViewById(R.id.layout_cena_con_receta)
        tvCenaNombre = findViewById(R.id.tv_cena_nombre)
        tvCenaTiempo = findViewById(R.id.tv_cena_tiempo)
        tvCenaDificultad = findViewById(R.id.tv_cena_dificultad)

        // Mis Recetas
        cardMisRecetas = findViewById(R.id.card_mis_recetas)
        layoutMisRecetasVacio = findViewById(R.id.layout_mis_recetas_vacio)
        layoutMisRecetasContenido = findViewById(R.id.layout_mis_recetas_contenido)
        imgMisRecetas = findViewById(R.id.img_mis_recetas)
        tvNombreMisRecetas = findViewById(R.id.tv_nombre_mis_recetas)
        tvTiempoMisRecetas = findViewById(R.id.tv_tiempo_mis_recetas)
        tvPorcionesMisRecetas = findViewById(R.id.tv_porciones_mis_recetas)
        tvDificultadMisRecetas = findViewById(R.id.tv_dificultad_mis_recetas)
        btnAgregarReceta = findViewById(R.id.btn_agregar_receta)
        btnCrearReceta = findViewById(R.id.btn_crear_receta)
        btnCalularCalorias = findViewById(R.id.btn_calcular_calorias)

        //Busqueda

        searchView.setOnClickListener() {
            val intent = Intent(this, Busqueda::class.java)
            startActivity(intent)
        }


    }

    //BARRA LATERAL
    private fun setupNavigationDrawer() {
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Configurar header del drawer
        val headerView = navigationView.getHeaderView(0)
        headerView.setOnClickListener {
            startActivity(Intent(this, Miperfil::class.java))
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        // Configurar items del menú
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_recetas_guardadas -> {
                    startActivity(Intent(this, RecetasGuardadas::class.java))
                }
                R.id.nav_mi_menu -> {
                    startActivity(Intent(this, MiMenu::class.java))
                }
                R.id.nav_configuracion -> {
                    startActivity(Intent(this,PantallaPrincipal::class.java)) // reemplazar por configuracion
                }
                R.id.nav_cerrar_sesion -> {
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this, Login::class.java))
                    finish()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // Configurar nombre de usuario
        val txtNombreUsuario = headerView.findViewById<TextView>(R.id.txtNombreUsuario)
        val usuario = FirebaseAuth.getInstance().currentUser
        txtNombreUsuario.text = usuario?.displayName ?: "Invitado"
    }

    //RECETA DEL DIA

    private fun cargarRecetaDelDia() {
        var currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        db.collection("Receta")
            .limit(1)
            .whereNotEqualTo("userId", currentUserId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0]
                    val recetaId = document.id
                    val nombreReceta = document.getString("nombre") ?: ""
                    val imagenUrl = document.getString("imagenUrl") ?: ""
                    val porciones = document.getLong("porciones")?.toInt() ?: 0
                    val tiempo = document.getLong("tiempo")?.toInt() ?: 0
                    val dificultad = document.getLong("dificultad")?.toInt() ?: 1
                    val preparacion = document.getString("preparacion") ?: ""
                    val ingredientes = document.get("ingredientes") as? List<String> ?: emptyList()
                    val recetaUserId = document.getString("userId") ?: ""

                    // Actualizar UI
                    tvNombreRecetaDia.text = nombreReceta
                    tvPorcionesRecetaDia.text = "$porciones porc."
                    tvTiempoRecetaDia.text = "$tiempo h"
                    tvDificultadRecetaDia.text = obtenerDificultad(dificultad.toString())

                    Glide.with(this)
                        .load(imagenUrl)
                        .transform(CenterCrop(), RoundedCorners(30))
                        .placeholder(R.drawable.baseline_image_24)
                        .into(imgRecetaDia)

                    // Configurar Intent con todos los datos necesarios
                    cardRecetaDia.setOnClickListener {
                        val intent = Intent(this, PantallaPrincipalReceta::class.java).apply {
                            putExtra("receta_id", recetaId)
                            putExtra("receta_nombre", nombreReceta)
                            putExtra("receta_imagen", imagenUrl)
                            putExtra("receta_porciones", porciones.toString())
                            putExtra("receta_tiempo", tiempo.toString())
                            putExtra("receta_dificultad", dificultad.toString())
                            putExtra("receta_preparacion", preparacion)
                            putStringArrayListExtra("receta_ingredientes", ArrayList(ingredientes))
                            putExtra("receta_userId", recetaUserId)
                        }
                        startActivity(intent)
                    }

                } else {
                    Toast.makeText(this, "No hay receta del día disponible", Toast.LENGTH_SHORT).show()
                    cardRecetaDia.setOnClickListener(null)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar receta del día: ${e.message}", Toast.LENGTH_SHORT).show()
                cardRecetaDia.setOnClickListener(null)
            }
    }

    //VISTO RECIENTEMENTE

    private fun cargarVistoRecientemente() {
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val recetaId = sharedPref.getString("ultima_receta_vista", null)

        val layoutVacio = findViewById<LinearLayout>(R.id.layout_visto_recientemente_vacio)
        val layoutContenido = findViewById<LinearLayout>(R.id.layout_visto_recientemente_contenido)

        if (recetaId != null) {
            layoutVacio.visibility = View.GONE
            layoutContenido.visibility = View.VISIBLE

            db.collection("Receta")
                .document(recetaId)
                .get()
                .addOnSuccessListener { document ->
                    val nombre = document.getString("nombre") ?: ""
                    val imagenUrl = document.getString("imagenUrl") ?: ""
                    val porciones = document.getLong("porciones")?.toString() ?: "0"
                    val tiempo = document.getLong("tiempo")?.toString() ?: "0"
                    val dificultad = document.getLong("dificultad")?.toString() ?: "1"
                    val preparacion = document.getString("preparacion") ?: ""
                    val ingredientes = document.get("ingredientes") as? List<String> ?: emptyList()
                    val userId = document.getString("userId") ?: ""

                    // Actualizar UI
                    tvNombreRecetaReciente.text = nombre
                    tvTiempoRecetaReciente.text = "$tiempo h"
                    tvPorcionesRecetaReciente.text = "$porciones porc."
                    tvDificultadRecetaReciente.text = obtenerDificultad(dificultad)

                    if (imagenUrl.isNotEmpty()) {
                        Glide.with(this)
                            .load(imagenUrl)
                            .placeholder(R.drawable.baseline_image_24)
                            .into(imgVistoRecientemente)
                    }

                    // Configurar Intent
                    cardVistoRecientemente.setOnClickListener {
                        val intent = Intent(this, PantallaPrincipalReceta::class.java).apply {
                            putExtra("receta_id", document.id)
                            putExtra("receta_nombre", nombre)
                            putExtra("receta_imagen", imagenUrl)
                            putExtra("receta_porciones", porciones)
                            putExtra("receta_tiempo", tiempo)
                            putExtra("receta_dificultad", dificultad)
                            putExtra("receta_preparacion", preparacion)
                            putStringArrayListExtra("receta_ingredientes", ArrayList(ingredientes))
                            putExtra("receta_userId", userId)
                        }
                        startActivity(intent)
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al cargar receta reciente", Toast.LENGTH_SHORT).show()
                    mostrarEstadoVacioVistoRecientemente()
                }
        } else {
            mostrarEstadoVacioVistoRecientemente()
        }

        findViewById<Button>(R.id.btn_explorar_recetas)?.setOnClickListener {
            startActivity(Intent(this, Busqueda::class.java))
        }
    }



    private fun mostrarEstadoVacioVistoRecientemente() {
        findViewById<LinearLayout>(R.id.layout_visto_recientemente_vacio).visibility = View.VISIBLE
        findViewById<LinearLayout>(R.id.layout_visto_recientemente_contenido).visibility = View.GONE
    }



    // MENU DEL DIA
    private fun setupMenuDelDia() {
        cardDesayuno.setOnClickListener { manejarClickMenu("desayuno") }
        cardAlmuerzo.setOnClickListener { manejarClickMenu("almuerzo") }
        cardCena.setOnClickListener { manejarClickMenu("cena") }

        cargarMenuDelDia()
    }

    private fun manejarClickMenu(tipoComida: String) {
        val sharedPref = getSharedPreferences("menu_del_dia", Context.MODE_PRIVATE)
        val recetaId = sharedPref.getString("${tipoComida}_id", null)

        if (recetaId != null) {
            // Si hay receta cargada, abrir detalles
            abrirDetallesReceta(recetaId)
        } else {
            // Si no hay receta, abrir selector en modo selección
            abrirSelectorReceta(tipoComida)
        }
    }

    private fun abrirSelectorReceta(tipoComida: String) {
        val intent = Intent(this, RecetasGuardadas::class.java).apply {
            putExtra("MODO_SELECCION", true)
            putExtra("TIPO_COMIDA", tipoComida)
        }
        startActivityForResult(intent, REQUEST_SELECCIONAR_RECETA)
    }

    private fun abrirDetallesReceta(recetaId: String) {
        val intent = Intent(this, PantallaPrincipalReceta::class.java).apply {
            putExtra("receta_id", recetaId)
        }
        startActivity(intent)
    }

    private fun cargarMenuDelDia() {
        val sharedPref = getSharedPreferences("menu_del_dia", Context.MODE_PRIVATE)

        listOf("desayuno", "almuerzo", "cena").forEach { tipoComida ->
            sharedPref.getString("${tipoComida}_id", null)?.let { recetaId ->
                cargarRecetaParaMenu(recetaId, tipoComida)

                // Configurar click listener para abrir detalles si ya hay receta
                when (tipoComida) {
                    "desayuno" -> {
                        cardDesayuno.setOnClickListener { abrirDetallesReceta(recetaId) }
                    }
                    "almuerzo" -> {
                        cardAlmuerzo.setOnClickListener { abrirDetallesReceta(recetaId) }
                    }
                    "cena" -> {
                        cardCena.setOnClickListener { abrirDetallesReceta(recetaId) }
                    }
                }
            }
        }
    }

    private fun guardarRecetaEnMenu(recetaId: String, tipoComida: String) {
        val sharedPref = getSharedPreferences("menu_del_dia", Context.MODE_PRIVATE)
        sharedPref.edit {
            putString("${tipoComida}_id", recetaId)
            apply()
        }
        cargarRecetaParaMenu(recetaId, tipoComida)

        // Reconfigurar el click listener para que ahora abra los detalles
        when (tipoComida) {
            "desayuno" -> {
                cardDesayuno.setOnClickListener { abrirDetallesReceta(recetaId) }
            }
            "almuerzo" -> {
                cardAlmuerzo.setOnClickListener { abrirDetallesReceta(recetaId) }
            }
            "cena" -> {
                cardCena.setOnClickListener { abrirDetallesReceta(recetaId) }
            }
        }
    }

    private fun cargarRecetaParaMenu(recetaId: String, tipoComida: String) {
        db.collection("Receta")
            .document(recetaId)
            .get()
            .addOnSuccessListener { document ->
                val receta = document.toObject(Receta::class.java)
                receta?.let { r ->
                    when (tipoComida) {
                        "desayuno" -> {
                            layoutDesayunoVacio.visibility = View.GONE
                            layoutDesayunoConReceta.visibility = View.VISIBLE
                            tvDesayunoNombre.text = r.nombre
                            tvDesayunoTiempo.text = "${r.tiempo} h."
                            tvDesayunoDificultad.text = obtenerDificultad(r.dificultad.toString())
                        }
                        "almuerzo" -> {
                            layoutAlmuerzoVacio.visibility = View.GONE
                            layoutAlmuerzoConReceta.visibility = View.VISIBLE
                            tvAlmuerzoNombre.text = r.nombre
                            tvAlmuerzoTiempo.text = "${r.tiempo} h."
                            tvAlmuerzoDificultad.text = obtenerDificultad(r.dificultad.toString())
                        }
                        "cena" -> {
                            layoutCenaVacio.visibility = View.GONE
                            layoutCenaConReceta.visibility = View.VISIBLE
                            tvCenaNombre.text = r.nombre
                            tvCenaTiempo.text = "${r.tiempo} h."
                            tvCenaDificultad.text = obtenerDificultad(r.dificultad.toString())
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar receta: ${e.message}", Toast.LENGTH_SHORT).show()
                when (tipoComida) {
                    "desayuno" -> {
                        layoutDesayunoVacio.visibility = View.VISIBLE
                        layoutDesayunoConReceta.visibility = View.GONE
                        cardDesayuno.setOnClickListener { abrirSelectorReceta("desayuno") }
                    }
                    "almuerzo" -> {
                        layoutAlmuerzoVacio.visibility = View.VISIBLE
                        layoutAlmuerzoConReceta.visibility = View.GONE
                        cardAlmuerzo.setOnClickListener { abrirSelectorReceta("almuerzo") }
                    }
                    "cena" -> {
                        layoutCenaVacio.visibility = View.VISIBLE
                        layoutCenaConReceta.visibility = View.GONE
                        cardCena.setOnClickListener { abrirSelectorReceta("cena") }
                    }
                }
            }
    }

    private fun obtenerDificultad(dificultad: String): String {
        return when(dificultad.toInt()) {
            1 -> "Fácil"
            2 -> "Media"
            3 -> "Difícil"
            else -> ""
        }
    }


    private fun mostrarDialogoConfirmacion(recetaId: String, tipoComida: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar receta")
            .setMessage("¿Deseas agregar esta receta a tu menú?")
            .setPositiveButton("Sí") { dialog, which ->
                guardarRecetaEnMenu(recetaId, tipoComida)
                cargarRecetaParaMenu(recetaId, tipoComida)
            }
            .setNegativeButton("Cancelar", null)
            .create()
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SELECCIONAR_RECETA && resultCode == RESULT_OK) {
            data?.let {
                val tipoComida = it.getStringExtra("TIPO_COMIDA")
                val recetaId = it.getStringExtra("RECETA_ID")

                if (tipoComida != null && recetaId != null) {
                    mostrarDialogoConfirmacion(recetaId, tipoComida)
                }
            }
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    //MIS RECETAS

    private fun cargarMisRecetas() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            mostrarEstadoVacioMisRecetas()
            return
        }

        db.collection("Receta")
            .whereEqualTo("userId", currentUserId)
            .orderBy("fechaCreacion", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    mostrarEstadoVacioMisRecetas()
                    return@addOnSuccessListener
                }

                val document = querySnapshot.documents[0]
                val recetaId = document.id
                val nombreReceta = document.getString("nombre") ?: "Sin nombre"
                val imagenUrl = document.getString("imagenUrl").orEmpty()
                val porciones = document.getLong("porciones")?.toString() ?: "0"
                val tiempo = document.getLong("tiempo")?.toString() ?: "0"
                val dificultad = document.getLong("dificultad")?.toString() ?: "1"
                val preparacion = document.getString("preparacion").orEmpty()
                val ingredientes = document.get("ingredientes") as? List<String> ?: emptyList()

                // Actualizar UI
                layoutMisRecetasVacio.visibility = View.GONE
                layoutMisRecetasContenido.visibility = View.VISIBLE

                tvNombreMisRecetas.text = nombreReceta
                tvTiempoMisRecetas.text = "$tiempo min"
                tvPorcionesMisRecetas.text = "$porciones porc."
                tvDificultadMisRecetas.text = obtenerDificultad(dificultad)

                Glide.with(this)
                    .load(imagenUrl.takeIf { it.isNotEmpty() })
                    .transform(CenterCrop(), RoundedCorners(30))
                    .placeholder(R.drawable.baseline_image_24)
                    .into(imgMisRecetas)

                cardMisRecetas.setOnClickListener {
                    val intent = Intent(this, PantallaPrincipalReceta::class.java).apply {
                        putExtra("receta_id", recetaId)
                        putExtra("receta_nombre", nombreReceta)
                        putExtra("receta_imagen", imagenUrl)
                        putExtra("receta_porciones", porciones)
                        putExtra("receta_tiempo", tiempo)
                        putExtra("receta_dificultad", dificultad)
                        putExtra("receta_preparacion", preparacion)
                        putStringArrayListExtra("receta_ingredientes", ArrayList(ingredientes))
                    }
                    startActivity(intent)
                }
            }
            .addOnFailureListener { exception ->
                mostrarEstadoVacioMisRecetas()
                Log.e("MisRecetas", "Error al cargar recetas", exception)
                Toast.makeText(this, "Error al cargar tus recetas", Toast.LENGTH_SHORT).show()
            }
    }


    private fun mostrarEstadoVacioMisRecetas() {
        layoutMisRecetasVacio.visibility = View.VISIBLE
        layoutMisRecetasContenido.visibility = View.GONE
    }

    private fun navegarAgregarReceta() {
        val intent = Intent(this, AgregarReceta::class.java)
        startActivity(intent)
    }

    companion object {
        private const val REQUEST_SELECCIONAR_RECETA = 1001
    }
}

// PRUEBA

/**
private fun cargarRecetaDelDia() {
db.collection("Receta")
.limit(1)
.get()
.addOnSuccessListener { querySnapshot ->
if (!querySnapshot.isEmpty) {
val document = querySnapshot.documents[0]
val receta = document.toObject(Receta::class.java)
receta?.let { r ->  // Cambiamos 'it' por 'r' para mayor claridad
tvNombreRecetaDia.text = r.nombre
tvTiempoRecetaDia.text = r.tiempo.toString() + "\nhoras"
tvPorcionesRecetaDia.text = r.porciones.toString() + "\nporciones"
tvDificultadRecetaDia.text = obtenerDificultad(r.dificultad)

if (!r.imagenUrl.isNullOrEmpty()) {
Glide.with(this)
.load(r.imagenUrl)
.placeholder(R.drawable.baseline_image_24)
.into(imgRecetaDia)
}

// Configurar click listener para la card
cardRecetaDia.setOnClickListener {
val intent = Intent(this@PantallaPrincipal, PantallaPrincipalReceta::class.java).apply {
putExtra("receta_id", document.id)
putExtra("receta_nombre", r.nombre)
putExtra("receta_imagen", r.imagenUrl)
putExtra("receta_porciones", r.porciones)
putExtra("receta_tiempo", r.tiempo)
putExtra("receta_dificultad", r.dificultad)
putExtra("receta_preparacion", r.preparacion)
putStringArrayListExtra("receta_ingredientes", ArrayList(r.ingredientes))
}
startActivity(intent)
}
}
}
}
.addOnFailureListener { e ->
Toast.makeText(this, "Error al cargar receta del día: ${e.message}", Toast.LENGTH_SHORT).show()
}
}

 */

// DETALLE RECETAS

/**
private fun cargarDetallesReceta(recetaId: String) {
    db.collection("Receta")
        .document(recetaId)
        .get()
        .addOnSuccessListener { document ->
            val receta = document.toObject(Receta::class.java)
            receta?.let {
                layoutMisRecetasVacio.visibility = View.GONE
                layoutMisRecetasContenido.visibility = View.VISIBLE

                tvNombreMisRecetas.text = it.nombre
                tvTiempoMisRecetas.text = "${it.tiempo}\nhoras"
                tvPorcionesMisRecetas.text = "${it.porciones}\nporciones"
                tvDificultadMisRecetas.text = obtenerDificultad(it.dificultad)

                if (it.imagenUrl.isNotEmpty()) {
                    Glide.with(this)
                        .load(it.imagenUrl)
                        .placeholder(R.drawable.baseline_image_24)
                        .into(imgMisRecetas)
                }
            } ?: run {
                mostrarEstadoVacioMisRecetas()
            }
        }
        .addOnFailureListener { e ->
            Toast.makeText(this, "Error al cargar detalles de receta", Toast.LENGTH_SHORT).show()
            mostrarEstadoVacioMisRecetas()
        }
}

*/

