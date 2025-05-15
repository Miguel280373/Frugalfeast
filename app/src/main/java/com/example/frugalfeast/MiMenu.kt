package com.example.frugalfeast

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MiMenu : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private lateinit var layoutDesayunoVacio: LinearLayout
    private lateinit var layoutDesayunoConReceta: LinearLayout
    private lateinit var tvDesayunoNombre: TextView
    private lateinit var tvDesayunoTiempo: TextView
    private lateinit var tvDesayunoDificultad: TextView
    private lateinit var imgDesayuno: ImageView

    private lateinit var layoutAlmuerzoVacio: LinearLayout
    private lateinit var layoutAlmuerzoConReceta: LinearLayout
    private lateinit var tvAlmuerzoNombre: TextView
    private lateinit var tvAlmuerzoTiempo: TextView
    private lateinit var tvAlmuerzoDificultad: TextView
    private lateinit var imgAlmuerzo: ImageView

    private lateinit var layoutCenaVacio: LinearLayout
    private lateinit var layoutCenaConReceta: LinearLayout
    private lateinit var tvCenaNombre: TextView
    private lateinit var tvCenaTiempo: TextView
    private lateinit var tvCenaDificultad: TextView
    private lateinit var imgCena: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mi_menu)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = FirebaseFirestore.getInstance()

        val btnVolver = findViewById<ImageButton>(R.id.btn_atras_mi_menu)
        btnVolver.setOnClickListener {
            finish()
        }

        layoutDesayunoVacio = findViewById(R.id.layout_desayuno_vacio)
        layoutDesayunoConReceta = findViewById(R.id.layout_desayuno_con_receta)
        tvDesayunoNombre = findViewById(R.id.tv_desayuno_nombre)
        tvDesayunoTiempo = findViewById(R.id.tv_desayuno_tiempo)
        tvDesayunoDificultad = findViewById(R.id.tv_desayuno_dificultad)
        imgDesayuno = findViewById(R.id.img_desayuno)

        layoutAlmuerzoVacio = findViewById(R.id.layout_almuerzo_vacio)
        layoutAlmuerzoConReceta = findViewById(R.id.layout_almuerzo_con_receta)
        tvAlmuerzoNombre = findViewById(R.id.tv_almuerzo_nombre)
        tvAlmuerzoTiempo = findViewById(R.id.tv_almuerzo_tiempo)
        tvAlmuerzoDificultad = findViewById(R.id.tv_almuerzo_dificultad)
        imgAlmuerzo = findViewById(R.id.img_almuerzo)

        layoutCenaVacio = findViewById(R.id.layout_cena_vacio)
        layoutCenaConReceta = findViewById(R.id.layout_cena_con_receta)
        tvCenaNombre = findViewById(R.id.tv_cena_nombre)
        tvCenaTiempo = findViewById(R.id.tv_cena_tiempo)
        tvCenaDificultad = findViewById(R.id.tv_cena_dificultad)
        imgCena = findViewById(R.id.img_cena)

        cargarMenuDelDia()
    }

    private fun cargarMenuDelDia() {
        if (currentUserId.isNotEmpty()) {
            db.collection("usuarios").document(currentUserId).collection("mi_menu").get()
                .addOnSuccessListener { querySnapshot ->
                    val recetasCargadas = mutableMapOf<String, Boolean>()
                    querySnapshot.documents.forEach { document ->
                        val tipoComida = document.id
                        val recetaId = document.getString("recetaId")

                        if (recetaId != null) {
                            cargarRecetaParaMenu(recetaId, tipoComida)
                            recetasCargadas[tipoComida] = true
                        } else {
                            mostrarEstadoVacioMiMenu(tipoComida)
                            recetasCargadas[tipoComida] = false
                        }
                    }

                    if (!recetasCargadas.containsKey("desayuno")) {
                        mostrarEstadoVacioMiMenu("desayuno")
                    }
                    if (!recetasCargadas.containsKey("almuerzo")) {
                        mostrarEstadoVacioMiMenu("almuerzo")
                    }
                    if (!recetasCargadas.containsKey("cena")) {
                        mostrarEstadoVacioMiMenu("cena")
                    }
                }
                .addOnFailureListener { e ->
                    // Manejar el error de carga del menú
                    mostrarEstadoVacioMiMenu("desayuno")
                    mostrarEstadoVacioMiMenu("almuerzo")
                    mostrarEstadoVacioMiMenu("cena")
                    Log.e("MiMenuActivity", "Error al cargar el menú del día.", e)
                }
        } else {
            mostrarEstadoVacioMiMenu("desayuno")
            mostrarEstadoVacioMiMenu("almuerzo")
            mostrarEstadoVacioMiMenu("cena")
        }
    }

    private fun cargarRecetaParaMenu(recetaId: String, tipoComida: String) {
        db.collection("Receta").document(recetaId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val nombre = documentSnapshot.getString("nombre") ?: ""
                    val tiempo = documentSnapshot.getLong("tiempo")?.toString() ?: ""
                    val dificultad = documentSnapshot.getLong("dificultad")?.toString() ?: ""
                    val imageUrl = documentSnapshot.getString("imagenUrl") ?: ""

                    when (tipoComida) {
                        "desayuno" -> {
                            mostrarRecetaDesayuno(nombre, tiempo, obtenerTextoDificultad(dificultad.toIntOrNull() ?: 1), imageUrl)
                        }
                        "almuerzo" -> {
                            mostrarRecetaAlmuerzo(nombre, tiempo, obtenerTextoDificultad(dificultad.toIntOrNull() ?: 1), imageUrl)
                        }
                        "cena" -> {
                            mostrarRecetaCena(nombre, tiempo, obtenerTextoDificultad(dificultad.toIntOrNull() ?: 1), imageUrl)
                        }
                    }
                } else {
                    mostrarEstadoVacioMiMenu(tipoComida)
                }
            }
            .addOnFailureListener { e ->
                mostrarEstadoVacioMiMenu(tipoComida)
                Log.e("MiMenuActivity", "Error al cargar la receta: $recetaId para $tipoComida", e)
            }
    }

    private fun mostrarEstadoVacioMiMenu(tipoComida: String) {
        when (tipoComida) {
            "desayuno" -> {
                layoutDesayunoVacio.visibility = View.VISIBLE
                layoutDesayunoConReceta.visibility = View.GONE
            }
            "almuerzo" -> {
                layoutAlmuerzoVacio.visibility = View.VISIBLE
                layoutAlmuerzoConReceta.visibility = View.GONE
            }
            "cena" -> {
                layoutCenaVacio.visibility = View.VISIBLE
                layoutCenaConReceta.visibility = View.GONE
            }
        }
    }

    private fun mostrarRecetaDesayuno(nombre: String, tiempo: String, dificultad: String, imageUrl: String) {
        layoutDesayunoVacio.visibility = View.GONE
        layoutDesayunoConReceta.visibility = View.VISIBLE
        tvDesayunoNombre.text = nombre
        tvDesayunoTiempo.text = "$tiempo h"
        tvDesayunoDificultad.text = dificultad
        cargarImagen(imageUrl, imgDesayuno)
    }

    private fun mostrarRecetaAlmuerzo(nombre: String, tiempo: String, dificultad: String, imageUrl: String) {
        layoutAlmuerzoVacio.visibility = View.GONE
        layoutAlmuerzoConReceta.visibility = View.VISIBLE
        tvAlmuerzoNombre.text = nombre
        tvAlmuerzoTiempo.text = "$tiempo h"
        tvAlmuerzoDificultad.text = dificultad
        cargarImagen(imageUrl, imgAlmuerzo)
    }

    private fun mostrarRecetaCena(nombre: String, tiempo: String, dificultad: String, imageUrl: String) {
        layoutCenaVacio.visibility = View.GONE
        layoutCenaConReceta.visibility = View.VISIBLE
        tvCenaNombre.text = nombre
        tvCenaTiempo.text = "$tiempo h"
        tvCenaDificultad.text = dificultad
        cargarImagen(imageUrl, imgCena)
    }

    private fun cargarImagen(imageUrl: String, imageView: ImageView) {
        if (imageUrl.isNotEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.baseline_image_24)
                .error(R.drawable.baseline_image_24)
                .into(imageView)
        } else {
            imageView.setImageResource(R.drawable.baseline_image_24)
        }
    }

    private fun obtenerTextoDificultad(nivel: Int): String {
        return when (nivel) {
            1 -> "Fácil"
            2 -> "Media"
            3 -> "Difícil"
            else -> "Desconocida"
        }
    }
}