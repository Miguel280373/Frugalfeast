package com.example.frugalfeast

import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class PantallaPrincipalReceta : AppCompatActivity() {
    private lateinit var imgReceta: ImageView
    private lateinit var nombreReceta: TextView
    private lateinit var tvTiempoReceta: TextView
    private lateinit var tvPorcionesReceta: TextView
    private lateinit var tvDificultadReceta: TextView
    private lateinit var preparacionReceta: TextView
    private lateinit var listaIngredientes: TextView
    private lateinit var btnAgregarReceta: ImageView

    private val db = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth
    private var isFavorite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_principal_receta)

        // Inicializar vistas
        imgReceta = findViewById(R.id.imgReceta)
        nombreReceta = findViewById(R.id.nombreReceta)
        tvTiempoReceta = findViewById(R.id.tv_tiempo_receta_dia)
        tvPorcionesReceta = findViewById(R.id.tv_porciones_receta_dia)
        tvDificultadReceta = findViewById(R.id.tv_dificultad_receta_dia)
        preparacionReceta = findViewById(R.id.preparacionReceta)
        listaIngredientes = findViewById(R.id.listaIngredientes)
        btnAgregarReceta = findViewById(R.id.btn_agregar_receta)

        auth = FirebaseAuth.getInstance()
        val currentUserId = auth.currentUser?.uid ?: ""

        val recetaId = intent.getStringExtra("receta_id") ?: ""
        val nombre = intent.getStringExtra("receta_nombre") ?: ""
        val porciones = intent.getStringExtra("receta_porciones") ?: ""
        val imagenUrl = intent.getStringExtra("receta_imagen") ?: ""
        val tiempo = intent.getStringExtra("receta_tiempo") ?: ""
        val dificultad = intent.getStringExtra("receta_dificultad") ?: ""
        val preparacion = intent.getStringExtra("receta_preparacion") ?: ""
        val ingredientes = intent.getStringArrayListExtra("receta_ingredientes") ?: emptyList()
        val recetaUserId = intent.getStringExtra("receta_userId") ?: ""

        nombreReceta.text = nombre

        if (imagenUrl.isNotEmpty()) {
            Glide.with(this)
                .load(imagenUrl)
                .placeholder(R.drawable.baseline_image_24)
                .fitCenter()
                .into(imgReceta)
        }

        tvTiempoReceta.text = tiempo.toString() + " h."
        tvPorcionesReceta.text = porciones.toString() + " porc."
        tvDificultadReceta.text = obtenerDificultad(dificultad)
        preparacionReceta.text = preparacion

        val ingredientesTexto = ingredientes.joinToString("\n• ", "• ")
        listaIngredientes.text = ingredientesTexto

        verificarRecetaFavorita(recetaId)

        if (recetaUserId != currentUserId) {
            guardarRecetaVista(recetaId)
        }

        btnAgregarReceta.setOnClickListener {
            if (isFavorite) {
                eliminarRecetaFavorita(recetaId)
            } else {
                guardarRecetaFavorita(recetaId, nombre, imagenUrl, tiempo, dificultad, preparacion, ingredientes)
            }
        }
    }

    private fun verificarRecetaFavorita(recetaId: String) {
        val userId = auth.currentUser?.uid ?: return

        db.collection("favoritos")
            .document(userId)
            .collection("recetas_favoritas")
            .document(recetaId)
            .get()
            .addOnSuccessListener { document ->
                isFavorite = document.exists()
                actualizarBotonFavorito()
            }
    }

    private fun guardarRecetaFavorita(
        recetaId: String,
        nombre: String,
        imagenUrl: String,
        tiempo: String,
        dificultad: String,
        preparacion: String,
        ingredientes: List<String>
    ) {
        val userId = auth.currentUser?.uid ?: return

        val recetaFavorita = hashMapOf(
            "id" to recetaId,
            "nombre" to nombre,
            "imagenUrl" to imagenUrl,
            "tiempo" to tiempo,
            "dificultad" to dificultad,
            "preparacion" to preparacion,
            "ingredientes" to ingredientes,
            "fechaGuardado" to FieldValue.serverTimestamp()
        )

        db.collection("favoritos")
            .document(userId)
            .collection("recetas_favoritas")
            .document(recetaId)
            .set(recetaFavorita)
            .addOnSuccessListener {
                isFavorite = true
                actualizarBotonFavorito()
                Toast.makeText(this, "Receta guardada en favoritos", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al guardar receta: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun eliminarRecetaFavorita(recetaId: String) {
        val userId = auth.currentUser?.uid ?: return

        db.collection("favoritos")
            .document(userId)
            .collection("recetas_favoritas")
            .document(recetaId)
            .delete()
            .addOnSuccessListener {
                isFavorite = false
                actualizarBotonFavorito()
                Toast.makeText(this, "Receta eliminada de favoritos", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al eliminar receta: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun obtenerDificultad(dificultad: String): String {
        return try {
            when(dificultad.trim().toIntOrNull() ?: 0) {
                1 -> "Fácil"
                2 -> "Media"
                3 -> "Difícil"
                else -> "No especificada"
            }
        } catch (e: Exception) {
            "No especificada"
        }
    }
    private fun actualizarBotonFavorito() {
        if (isFavorite) {
            btnAgregarReceta.setImageResource(R.drawable.bookmark_remove_24dp_e3e3e3_fill0_wght400_grad0_opsz24)
            btnAgregarReceta.setColorFilter(ContextCompat.getColor(this, R.color.red))
        } else {
            btnAgregarReceta.setImageResource(R.drawable.guardado)
            btnAgregarReceta.setColorFilter(ContextCompat.getColor(this, R.color.black))
        }
    }

    private fun guardarRecetaVista(recetaId: String) {
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPref.edit {
            putString("ultima_receta_vista", recetaId)
            apply()
        }
    }
}