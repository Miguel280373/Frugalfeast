package com.example.frugalfeast

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source

class PantallaPrincipal : AppCompatActivity() {

    // Views principales
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var searchView: EditText
    private lateinit var btnMenu: ImageButton
    private lateinit var headerView: View // Referencia al header view

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
    private lateinit var imgDesayuno: ImageView
    private lateinit var tvDesayunoDificultad: TextView

    private lateinit var cardAlmuerzo: CardView
    private lateinit var layoutAlmuerzoVacio: LinearLayout
    private lateinit var layoutAlmuerzoConReceta: LinearLayout
    private lateinit var tvAlmuerzoNombre: TextView
    private lateinit var tvAlmuerzoTiempo: TextView
    private lateinit var imgAlmuerzo: ImageView
    private lateinit var tvAlmuerzoDificultad: TextView

    private lateinit var cardCena: CardView
    private lateinit var layoutCenaVacio: LinearLayout
    private lateinit var layoutCenaConReceta: LinearLayout
    private lateinit var tvCenaNombre: TextView
    private lateinit var tvCenaTiempo: TextView
    private lateinit var imgCena: ImageView
    private lateinit var tvCenaDificultad: TextView

    private lateinit var tituloDesayunoSin: TextView
    private lateinit var tituloAlmuerzoSin: TextView
    private lateinit var tituloCenaSin: TextView

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
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    private val userRef = FirebaseFirestore.getInstance().collection("usuarios").document(currentUserId)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_principal)

        initViews()
        setupSearchView()
        setupNavigationDrawer() // Llama a setupNavigationDrawer primero
        setupMenuDelDia()
        cargarRecetaDelDia()
        cargarVistoRecientemente()
        cargarMisRecetas()

        btnCalularCalorias.setOnClickListener {
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
        searchView = findViewById(R.id.edit_text_busqueda)
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
        imgDesayuno = findViewById(R.id.img_desayuno)
        tvDesayunoDificultad = findViewById(R.id.tv_desayuno_dificultad)

        cardAlmuerzo = findViewById(R.id.card_almuerzo)
        layoutAlmuerzoVacio = findViewById(R.id.layout_almuerzo_vacio)
        layoutAlmuerzoConReceta = findViewById(R.id.layout_almuerzo_con_receta)
        tvAlmuerzoNombre = findViewById(R.id.tv_almuerzo_nombre)
        tvAlmuerzoTiempo = findViewById(R.id.tv_almuerzo_tiempo)
        imgAlmuerzo = findViewById(R.id.img_almuerzo)
        tvAlmuerzoDificultad = findViewById(R.id.tv_almuerzo_dificultad)

        cardCena = findViewById(R.id.card_cena)
        layoutCenaVacio = findViewById(R.id.layout_cena_vacio)
        layoutCenaConReceta = findViewById(R.id.layout_cena_con_receta)
        tvCenaNombre = findViewById(R.id.tv_cena_nombre)
        tvCenaTiempo = findViewById(R.id.tv_cena_tiempo)
        imgCena = findViewById(R.id.img_cena)
        tvCenaDificultad = findViewById(R.id.tv_cena_dificultad)

        tituloDesayunoSin = findViewById(R.id.tituloDesayunoSin)
        tituloAlmuerzoSin = findViewById(R.id.tituloAlmuerzoSin)
        tituloCenaSin = findViewById(R.id.tituloCenaSin)

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
    }

    // BUSQUEDA
    private fun setupSearchView() {
        val editTextBusqueda = findViewById<EditText>(R.id.edit_text_busqueda)

        editTextBusqueda.setOnClickListener {
            try {
                val intent = Intent(this@PantallaPrincipal, Busqueda::class.java)
                intent.putExtra("query", editTextBusqueda.text.toString())
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "Error al iniciar búsqueda", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //BARRA LATERAL
    private fun setupNavigationDrawer() {
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        headerView = navigationView.getHeaderView(0)
        headerView.setOnClickListener {
            startActivity(Intent(this, Miperfil::class.java))
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_recetas_guardadas -> {
                    startActivity(Intent(this, RecetasGuardadas::class.java))
                }
                R.id.nav_mi_menu -> {
                    startActivity(Intent(this, MiMenu::class.java))
                }
                R.id.nav_configuracion -> {
                    startActivity(Intent(this, ConfiguracionyPrivacidad::class.java))
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

        cargarNombreUsuarioHeader(headerView)
        cargarFotoPerfilHeader(headerView)
    }

    private fun cargarNombreUsuarioHeader(headerView: View) {
        val txtNombreUsuario = headerView.findViewById<TextView>(R.id.txtNombreUsuario)
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            FirebaseFirestore.getInstance().collection("usuarios").document(currentUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists() && document.contains("apodo")) {
                        val apodo = document.getString("apodo")
                        txtNombreUsuario.text = apodo ?: "Invitado"
                    } else {
                        txtNombreUsuario.text = currentUser.displayName ?: "Invitado"
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("PantallaPrincipal", "Error al cargar el apodo del usuario", e)
                    txtNombreUsuario.text = currentUser.displayName ?: "Invitado"
                }
        } else {
            txtNombreUsuario.text = getString(R.string.invitado)
        }
    }

    private fun cargarFotoPerfilHeader(headerView: View) {
        val headerImageView = headerView.findViewById<ImageView>(R.id.imageViewNavHeader)

        db.collection("usuarios").document(currentUserId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val fotoPerfilUrl = document.getString("fotoPerfil")
                    if (!fotoPerfilUrl.isNullOrEmpty()) {
                        Glide.with(this)
                            .load(fotoPerfilUrl)
                            .placeholder(R.drawable.avatar1)
                            .error(R.drawable.avatar1)
                            .into(headerImageView)
                    }
                }
            }
    }

    override fun onResume() {
        super.onResume()
        cargarFotoPerfilHeader(headerView)
        val headerView = navigationView.getHeaderView(0)
        cargarNombreUsuarioHeader(headerView)
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

                    tvNombreRecetaDia.text = nombreReceta
                    tvPorcionesRecetaDia.text = "$porciones porc."
                    tvTiempoRecetaDia.text = "$tiempo h"
                    tvDificultadRecetaDia.text = obtenerDificultad(dificultad.toString())

                    Glide.with(this)
                        .load(imagenUrl)
                        .transform(CenterCrop(), RoundedCorners(30))
                        .placeholder(R.drawable.baseline_image_24)
                        .into(imgRecetaDia)

                    cardRecetaDia.setOnClickListener {
                        val intent = Intent(this, PantallaPrincipalReceta::class.java).apply {
                            putExtra("receta_id", recetaId)
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
        userRef.collection("visto_recientemente")
            .orderBy("fecha", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val doc = querySnapshot.documents[0]
                    val recetaId = doc.getString("recetaId") ?: return@addOnSuccessListener

                    cargarDetallesRecetaVistaReciente(recetaId)
                } else {
                    mostrarEstadoVacioVistoRecientemente()
                }
            }
            .addOnFailureListener {
                mostrarEstadoVacioVistoRecientemente()
            }
    }

    private fun cargarDetallesRecetaVistaReciente(recetaId: String) {
        db.collection("Receta").document(recetaId).get()
            .addOnSuccessListener { document ->
                val layoutVacio = findViewById<LinearLayout>(R.id.layout_visto_recientemente_vacio)
                val layoutContenido = findViewById<LinearLayout>(R.id.layout_visto_recientemente_contenido)

                layoutVacio.visibility = View.GONE
                layoutContenido.visibility = View.VISIBLE

                val nombre = document.getString("nombre") ?: ""
                val imagenUrl = document.getString("imagenUrl") ?: ""
                val porciones = document.getLong("porciones")?.toString() ?: "0"
                val tiempo = document.getLong("tiempo")?.toString() ?: "0"
                val dificultad = document.getLong("dificultad")?.toString() ?: "1"

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

                cardVistoRecientemente.setOnClickListener {
                    val intent = Intent(this, PantallaPrincipalReceta::class.java).apply {
                        putExtra("receta_id", document.id)
                    }
                    startActivity(intent)
                }
            }
            .addOnFailureListener {
                mostrarEstadoVacioVistoRecientemente()
            }
    }



    private fun mostrarEstadoVacioVistoRecientemente() {
        findViewById<LinearLayout>(R.id.layout_visto_recientemente_vacio).visibility = View.VISIBLE
        findViewById<LinearLayout>(R.id.layout_visto_recientemente_contenido).visibility = View.GONE
    }



    // MENU DEL DIA
    private fun setupMenuDelDia() {
        cargarMenuDelDia()

        cardDesayuno.setOnLongClickListener {
            mostrarMenuContextual("desayuno", it)
            true
        }

        cardAlmuerzo.setOnLongClickListener {
            mostrarMenuContextual("almuerzo", it)
            true
        }

        cardCena.setOnLongClickListener {
            mostrarMenuContextual("cena", it)
            true
        }

        cardDesayuno.setOnClickListener {
            userRef.collection("mi_menu").document("desayuno").get()
                .addOnSuccessListener { doc ->
                    doc.getString("recetaId")?.let { id ->
                        abrirDetallesReceta(id)
                    }
                }
        }

        cardAlmuerzo.setOnClickListener {
            userRef.collection("mi_menu").document("almuerzo").get()
                .addOnSuccessListener { doc ->
                    doc.getString("recetaId")?.let { id ->
                        abrirDetallesReceta(id)
                    }
                }
        }

        cardCena.setOnClickListener {
            userRef.collection("mi_menu").document("cena").get()
                .addOnSuccessListener { doc ->
                    doc.getString("recetaId")?.let { id ->
                        abrirDetallesReceta(id)
                    }
                }
        }
    }

    private fun actualizarVistaMenu(tipoComida: String, recetaId: String?) {
        runOnUiThread {
            when (tipoComida) {
                "desayuno" -> {
                    if (recetaId == null) {
                        tituloDesayunoSin.visibility = View.VISIBLE
                        layoutDesayunoVacio.visibility = View.VISIBLE
                        layoutDesayunoConReceta.visibility = View.GONE
                        cardDesayuno.setOnClickListener { mostrarMenuContextual("desayuno", it) }
                    } else {
                        cargarRecetaParaMenu(recetaId, "desayuno")
                    }
                }
                "almuerzo" -> {
                    if (recetaId == null) {
                        tituloAlmuerzoSin.visibility = View.VISIBLE
                        layoutAlmuerzoVacio.visibility = View.VISIBLE
                        layoutAlmuerzoConReceta.visibility = View.GONE
                        cardAlmuerzo.setOnClickListener { mostrarMenuContextual("almuerzo", it) }
                    } else {
                        cargarRecetaParaMenu(recetaId, "almuerzo")
                    }
                }
                "cena" -> {
                    if (recetaId == null) {
                        tituloCenaSin.visibility = View.VISIBLE
                        layoutCenaVacio.visibility = View.VISIBLE
                        layoutCenaConReceta.visibility = View.GONE
                        cardCena.setOnClickListener { mostrarMenuContextual("cena", it) }
                    } else {
                        cargarRecetaParaMenu(recetaId, "cena")
                    }
                }
            }
        }
    }

    private fun mostrarMenuContextual(tipoComida: String, anchorView: View) {
        userRef.collection("mi_menu").document(tipoComida).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val recetaId = document.getString("recetaId")

                    val menu = PopupMenu(this, anchorView).apply {
                        menuInflater.inflate(R.menu.menu_contextual_receta, menu)

                        menu.findItem(R.id.menu_reemplazar).setOnMenuItemClickListener {
                            abrirSelectorReceta(tipoComida)
                            true
                        }

                        menu.findItem(R.id.menu_eliminar).setOnMenuItemClickListener {
                            mostrarDialogoConfirmacionEliminar(tipoComida)
                            true
                        }
                    }
                    menu.show()
                } else {
                    abrirSelectorReceta(tipoComida)
                }
            }
    }


    private fun eliminarDelMenu(tipoComida: String) {
        userRef.collection("mi_menu").document(tipoComida).delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Receta eliminada", Toast.LENGTH_SHORT).show()
                when(tipoComida) {
                    "desayuno" -> mostrarEstadoVacioMiMenu("desayuno")
                    "almuerzo" -> mostrarEstadoVacioMiMenu("almuerzo")
                    "cena" -> mostrarEstadoVacioMiMenu("cena")
                }
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
        userRef.collection("mi_menu").get()
            .addOnSuccessListener { querySnapshot ->
                querySnapshot.documents.forEach { document ->
                    val tipoComida = document.id
                    val recetaId = document.getString("recetaId")

                    if (recetaId != null) {
                        cargarRecetaParaMenu(recetaId, tipoComida)
                    } else {
                        mostrarEstadoVacioMiMenu(tipoComida)
                    }
                }

                val comidasCargadas = querySnapshot.documents.map { it.id }
                listOf("desayuno", "almuerzo", "cena").forEach { comida ->
                    if (!comidasCargadas.contains(comida)) {
                        mostrarEstadoVacioMiMenu(comida)
                    }
                }
            }
    }

    private fun guardarRecetaEnMenu(recetaId: String, tipoComida: String) {
        val menuData = hashMapOf(
            "recetaId" to recetaId,
            "fechaAgregado" to FieldValue.serverTimestamp()
        )

        userRef.collection("mi_menu").document(tipoComida)
            .set(menuData)
            .addOnSuccessListener {
                Toast.makeText(this, "Receta agregada a tu menú", Toast.LENGTH_SHORT).show()
                cargarRecetaParaMenu(recetaId, tipoComida)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al guardar en menú: ${e.message}", Toast.LENGTH_SHORT).show()
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
                            tituloDesayunoSin.visibility = View.GONE
                            layoutDesayunoVacio.visibility = View.GONE
                            layoutDesayunoConReceta.visibility = View.VISIBLE

                            tvDesayunoNombre.text = r.nombre
                            tvDesayunoTiempo.text = "${r.tiempo} h."
                            tvDesayunoDificultad.text = obtenerDificultad(r.dificultad.toString())

                            if (r.imagenUrl.isNotEmpty()) {
                                Glide.with(this)
                                    .load(r.imagenUrl)
                                    .placeholder(R.drawable.baseline_image_24)
                                    .error(R.drawable.baseline_image_24)
                                    .into(imgDesayuno)
                            } else {
                                imgDesayuno.setImageResource(R.drawable.baseline_image_24)
                            }

                            // Listener simplificado que solo envía el ID
                            cardDesayuno.setOnClickListener {
                                val intent = Intent(this, PantallaPrincipalReceta::class.java).apply {
                                    putExtra("receta_id", recetaId)
                                }
                                startActivity(intent)
                            }
                        }
                        "almuerzo" -> {
                            tituloAlmuerzoSin.visibility = View.GONE
                            layoutAlmuerzoVacio.visibility = View.GONE
                            layoutAlmuerzoConReceta.visibility = View.VISIBLE

                            tvAlmuerzoNombre.text = r.nombre
                            tvAlmuerzoTiempo.text = "${r.tiempo} h."
                            tvAlmuerzoDificultad.text = obtenerDificultad(r.dificultad.toString())

                            if (r.imagenUrl.isNotEmpty()) {
                                Glide.with(this)
                                    .load(r.imagenUrl)
                                    .placeholder(R.drawable.baseline_image_24)
                                    .error(R.drawable.baseline_image_24)
                                    .into(imgAlmuerzo)
                            } else {
                                imgAlmuerzo.setImageResource(R.drawable.baseline_image_24)
                            }

                            // Listener simplificado que solo envía el ID
                            cardAlmuerzo.setOnClickListener {
                                val intent = Intent(this, PantallaPrincipalReceta::class.java).apply {
                                    putExtra("receta_id", recetaId)
                                }
                                startActivity(intent)
                            }
                        }
                        "cena" -> {
                            tituloCenaSin.visibility = View.GONE
                            layoutCenaVacio.visibility = View.GONE
                            layoutCenaConReceta.visibility = View.VISIBLE

                            tvCenaNombre.text = r.nombre
                            tvCenaTiempo.text = "${r.tiempo} h."
                            tvCenaDificultad.text = obtenerDificultad(r.dificultad.toString())

                            if (r.imagenUrl.isNotEmpty()) {
                                Glide.with(this)
                                    .load(r.imagenUrl)
                                    .placeholder(R.drawable.baseline_image_24)
                                    .error(R.drawable.baseline_image_24)
                                    .into(imgCena)
                            } else {
                                imgCena.setImageResource(R.drawable.baseline_image_24)
                            }

                            // Listener simplificado que solo envía el ID
                            cardCena.setOnClickListener {
                                val intent = Intent(this, PantallaPrincipalReceta::class.java).apply {
                                    putExtra("receta_id", recetaId)
                                }
                                startActivity(intent)
                            }
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar receta: ${e.message}", Toast.LENGTH_SHORT).show()
                mostrarEstadoVacioMiMenu(tipoComida)
            }
    }

    private fun mostrarEstadoVacioMiMenu(tipoComida: String) {
        when (tipoComida) {
            "desayuno" -> {
                tituloDesayunoSin.visibility = View.VISIBLE
                layoutDesayunoVacio.visibility = View.VISIBLE
                layoutDesayunoConReceta.visibility = View.GONE
                cardDesayuno.setOnClickListener { abrirSelectorReceta("desayuno") }
            }
            "almuerzo" -> {
                tituloAlmuerzoSin.visibility = View.VISIBLE
                layoutAlmuerzoVacio.visibility = View.VISIBLE
                layoutAlmuerzoConReceta.visibility = View.GONE
                cardAlmuerzo.setOnClickListener { abrirSelectorReceta("almuerzo") }
            }
            "cena" -> {
                tituloCenaSin.visibility = View.VISIBLE
                layoutCenaVacio.visibility = View.VISIBLE
                layoutCenaConReceta.visibility = View.GONE
                cardCena.setOnClickListener { abrirSelectorReceta("cena") }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_SELECCIONAR_RECETA -> {
                if (resultCode == RESULT_OK) {
                    data?.let {
                        val tipoComida = it.getStringExtra("TIPO_COMIDA")
                        val recetaId = it.getStringExtra("RECETA_ID")

                        if (tipoComida != null && recetaId != null) {
                            mostrarDialogoConfirmacion(recetaId, tipoComida)
                        }
                    }
                }
            }
            REQUEST_CODE_AGREGAR_RECETA -> {
                if (resultCode == Activity.RESULT_OK) {
                    cargarMisRecetas(true)
                }
            }
        }
    }
    private fun mostrarDialogoConfirmacion(recetaId: String, tipoComida: String) {
        AlertDialog.Builder(this)
            .setTitle("Agregar al menú")
            .setMessage("¿Deseas agregar esta receta a tu $tipoComida?")
            .setPositiveButton("Sí") { dialog, which ->
                guardarRecetaEnMenu(recetaId, tipoComida)

                db.collection("Receta").document(recetaId).get()
                    .addOnSuccessListener { document ->
                        val receta = document.toObject(Receta::class.java)
                        receta?.let {
                            actualizarVistaMenu(tipoComida, recetaId)
                            abrirDetallesReceta(recetaId)
                        }
                    }
            }
            .setNegativeButton("Cancelar", null)
            .create()
            .show()
    }

    private fun mostrarDialogoConfirmacionEliminar(tipoComida: String) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar receta")
            .setMessage("¿Deseas eliminar esta receta de tu menú?")
            .setPositiveButton("Eliminar") { _, _ ->
                eliminarDelMenu(tipoComida)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    //MIS RECETAS
    private val REQUEST_CODE_AGREGAR_RECETA = 1001


    private fun mostrarEstadoVacioMisRecetas() {
        runOnUiThread {
            layoutMisRecetasVacio.visibility = View.VISIBLE
            layoutMisRecetasContenido.visibility = View.GONE

            btnCrearReceta.setOnClickListener {
                navegarAgregarReceta()
            }

            btnAgregarReceta.setOnClickListener {
                navegarAgregarReceta()
            }
        }
    }

    private fun cargarMisRecetas(forceRefresh: Boolean = false) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            mostrarEstadoVacioMisRecetas()
            return
        }

        val source = if (forceRefresh) Source.SERVER else Source.DEFAULT

        db.collection("Receta")
            .whereEqualTo("userId", currentUserId)
            .orderBy("fechaCreacion", Query.Direction.DESCENDING)
            .limit(1)
            .get(source)
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    mostrarEstadoVacioMisRecetas()
                    return@addOnSuccessListener
                }

                val document = querySnapshot.documents[0]
                actualizarUIconReceta(document)
            }
            .addOnFailureListener { exception ->
                Log.e("MisRecetas", "Error al cargar recetas", exception)
                mostrarEstadoVacioMisRecetas()
                Toast.makeText(this, "Error al cargar tus recetas", Toast.LENGTH_SHORT).show()
            }
    }

    private fun actualizarUIconReceta(document: DocumentSnapshot) {
        runOnUiThread {
            try {
                val receta = document.toObject(Receta::class.java) ?: run {
                    mostrarEstadoVacioMisRecetas()
                    return@runOnUiThread
                }

                layoutMisRecetasVacio.visibility = View.GONE
                layoutMisRecetasContenido.visibility = View.VISIBLE

                tvNombreMisRecetas.text = receta.nombre
                tvTiempoMisRecetas.text = "${receta.tiempo} h"
                tvPorcionesMisRecetas.text = "${receta.porciones} porc."
                tvDificultadMisRecetas.text = obtenerDificultad(receta.dificultad.toString())

                if (receta.imagenUrl.isNotEmpty()) {
                    Glide.with(this)
                        .load(receta.imagenUrl)
                        .transform(CenterCrop(), RoundedCorners(16))
                        .placeholder(R.drawable.baseline_image_24)
                        .error(R.drawable.baseline_image_24)
                        .into(imgMisRecetas)
                } else {
                    imgMisRecetas.setImageResource(R.drawable.baseline_image_24)
                }

                cardMisRecetas.setOnClickListener {
                    val intent = Intent(this, PantallaPrincipalReceta::class.java).apply {
                        putExtra("receta_id", document.id)
                    }
                    startActivity(intent)
                }

            } catch (e: Exception) {
                Log.e("MisRecetas", "Error al actualizar UI", e)
                mostrarEstadoVacioMisRecetas()
            }
        }
    }

    private fun navegarAgregarReceta() {
        val intent = Intent(this, AgregarReceta::class.java)
        startActivityForResult(intent, REQUEST_CODE_AGREGAR_RECETA)
    }

    companion object {
        private const val REQUEST_SELECCIONAR_RECETA = 1001
    }
}
