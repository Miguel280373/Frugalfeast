package com.example.frugalfeast

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class PantallaPrincipalReceta : AppCompatActivity() {

    private lateinit var imgReceta: ImageView
    private lateinit var nombreReceta: TextView
    private lateinit var tvTiempoReceta: TextView
    private lateinit var tvPorcionesReceta: TextView
    private lateinit var tvDificultadReceta: TextView
    private lateinit var tvCaloriasReceta: TextView
    private lateinit var preparacionReceta: TextView
    private lateinit var listaIngredientes: TextView
    private lateinit var btnAgregarReceta: ImageView
    private lateinit var btnBack: ImageView

    private val db = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth

    private var isFavorite = false
    private var isMyRecipe = false
    private lateinit var currentReceta: Receta

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_principal_receta)

        initViews()
        auth = FirebaseAuth.getInstance()

        val recetaId = intent.getStringExtra("receta_id") ?: intent.getStringExtra("recetaId") ?: ""

        if (recetaId.isNotEmpty()) {
            cargarRecetaDesdeFirestore(recetaId)
        } else {
            Toast.makeText(this, "No se encontró la receta", Toast.LENGTH_SHORT).show()
            finish()
        }
    }


    private fun initViews() {
        imgReceta = findViewById(R.id.imgReceta)
        nombreReceta = findViewById(R.id.nombreReceta)
        tvTiempoReceta = findViewById(R.id.tv_tiempo_receta_dia)
        tvPorcionesReceta = findViewById(R.id.tv_porciones_receta_dia)
        tvDificultadReceta = findViewById(R.id.tv_dificultad_receta_dia)
        tvCaloriasReceta = findViewById(R.id.totalCaloriesTextView)
        preparacionReceta = findViewById(R.id.preparacionReceta)
        listaIngredientes = findViewById(R.id.listaIngredientes)
        btnAgregarReceta = findViewById(R.id.btn_agregar_receta)
        btnBack = findViewById(R.id.btn_back)
    }


    private fun cargarRecetaDesdeFirestore(recetaId: String) {
        db.collection("Receta").document(recetaId).get()
            .addOnSuccessListener { document ->
                currentReceta = Receta(
                    id = document.id,
                    nombre = document.getString("nombre") ?: "",
                    imagenUrl = document.getString("imagenUrl") ?: "",
                    tiempo = document.getLong("tiempo")?.toInt() ?: 0,
                    porciones = document.getLong("porciones")?.toInt() ?: 0,
                    dificultad = document.getLong("dificultad")?.toInt() ?: 1,
                    calorias = document.getLong("calorias")?.toInt() ?: 0,
                    preparacion = document.getString("preparacion") ?: "",
                    userId = document.getString("userId") ?: "",
                    ingredientes = document.get("ingredientes") as? List<String> ?: emptyList()
                )
                setupViews()
                configurarBotones()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar receta", Toast.LENGTH_SHORT).show()
                finish()
            }
    }
    private fun setupViews() {
        val currentUser = auth.currentUser
        isMyRecipe = currentUser != null && currentReceta.userId == currentUser.uid

        nombreReceta.text = currentReceta.nombre.ifEmpty { "Sin nombre" }
        tvTiempoReceta.text = "${currentReceta.tiempo} h"
        tvPorcionesReceta.text = "${currentReceta.porciones} porc."
        tvDificultadReceta.text = obtenerTextoDificultad(currentReceta.dificultad)
        tvCaloriasReceta.text = "${currentReceta.calorias} kcal"
        preparacionReceta.text = currentReceta.preparacion.ifEmpty { "No hay instrucciones" }

        listaIngredientes.text = if (currentReceta.ingredientes.isNotEmpty()) {
            currentReceta.ingredientes.joinToString("\n• ", "• ")
        } else {
            "No se especificaron ingredientes"
        }

        if (currentReceta.imagenUrl.isNotEmpty()) {
            Glide.with(this)
                .load(currentReceta.imagenUrl)
                .placeholder(R.drawable.baseline_image_24)
                .error(R.drawable.baseline_image_24)
                .fitCenter()
                .into(imgReceta)
        } else {
            imgReceta.setImageResource(R.drawable.baseline_image_24)
        }
    }

    private fun configurarBotones() {
        btnBack.setOnClickListener { finish() }

        if (isMyRecipe) {
            btnAgregarReceta.setImageResource(R.drawable.edit_24dp_000000_fill0_wght400_grad0_opsz24)
            btnAgregarReceta.setColorFilter(ContextCompat.getColor(this, R.color.black))
            btnAgregarReceta.setOnClickListener {
                abrirPantallaEdicion()
            }
        } else {
            verificarRecetaFavorita()
            btnAgregarReceta.setOnClickListener {
                if (isFavorite) {
                    eliminarRecetaFavorita()
                } else {
                    guardarRecetaFavorita()
                }
            }
            guardarComoVistoRecientemente(currentReceta.id)
        }
    }

    private val REQUEST_EDIT_RECETA = 123

    private fun abrirPantallaEdicion() {
        val intent = Intent(this, EditarReceta::class.java)
        intent.putExtra("receta_id", currentReceta.id)
        startActivityForResult(intent, REQUEST_EDIT_RECETA)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_EDIT_RECETA) {
            if (resultCode == Activity.RESULT_OK) {
                val recetaId = currentReceta.id
                cargarRecetaDesdeFirestore(recetaId)
            }
        }
    }

    private fun verificarRecetaFavorita() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("favoritos")
            .document(userId)
            .collection("recetas_favoritas")
            .document(currentReceta.id)
            .get()
            .addOnSuccessListener { document ->
                isFavorite = document.exists()
                actualizarBotonFavorito()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al verificar favoritos", Toast.LENGTH_SHORT).show()
                Log.e("Receta", "Error al verificar favoritos", e)
            }
    }

    private fun guardarRecetaFavorita() {
        val userId = auth.currentUser?.uid ?: return

        val recetaFavorita = hashMapOf(
            "id" to currentReceta.id,
            "nombre" to currentReceta.nombre,
            "imagenUrl" to currentReceta.imagenUrl,
            "tiempo" to currentReceta.tiempo,
            "dificultad" to currentReceta.dificultad,
            "porciones" to currentReceta.porciones,
            "preparacion" to currentReceta.preparacion,
            "ingredientes" to currentReceta.ingredientes,
            "userId" to userId,
            "fechaGuardado" to FieldValue.serverTimestamp()
        )

        db.collection("favoritos")
            .document(userId)
            .collection("recetas_favoritas")
            .document(currentReceta.id)
            .set(recetaFavorita)
            .addOnSuccessListener {
                isFavorite = true
                actualizarBotonFavorito()
                Toast.makeText(this, "Receta guardada en favoritos", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al guardar en favoritos", Toast.LENGTH_SHORT).show()
                Log.e("Receta", "Error al guardar favorito", e)
            }
    }

    private fun eliminarRecetaFavorita() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("favoritos")
            .document(userId)
            .collection("recetas_favoritas")
            .document(currentReceta.id)
            .delete()
            .addOnSuccessListener {
                isFavorite = false
                actualizarBotonFavorito()
                Toast.makeText(this, "Receta eliminada de favoritos", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al eliminar de favoritos", Toast.LENGTH_SHORT).show()
                Log.e("Receta", "Error al eliminar favorito", e)
            }
    }

    private fun obtenerTextoDificultad(nivel: Int): String {
        return when (nivel) {
            1 -> "Fácil"
            2 -> "Media"
            3 -> "Difícil"
            else -> "No especificada"
        }
    }

    private fun actualizarBotonFavorito() {
        btnAgregarReceta.setImageResource(
            if (isFavorite) R.drawable.bookmark_remove_24dp_e3e3e3_fill0_wght400_grad0_opsz24
            else R.drawable.guardado
        )
        btnAgregarReceta.setColorFilter(
            ContextCompat.getColor(this, if (isFavorite) R.color.red else R.color.black)
        )
    }

    private fun guardarComoVistoRecientemente(recetaId: String) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        val vistoData = hashMapOf(
            "recetaId" to recetaId,
            "fecha" to FieldValue.serverTimestamp()
        )

        FirebaseFirestore.getInstance()
            .collection("usuarios")
            .document(currentUserId)
            .collection("visto_recientemente")
            .add(vistoData)
            .addOnFailureListener { e ->
                Log.e("VistoReciente", "Error al guardar receta vista", e)
            }
    }
}