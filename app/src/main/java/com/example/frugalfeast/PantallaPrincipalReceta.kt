package com.example.frugalfeast

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

        currentReceta = obtenerRecetaDesdeIntent()

        setupViews()
        setupButtonActions()
    }

    private fun initViews() {
        imgReceta = findViewById(R.id.imgReceta)
        nombreReceta = findViewById(R.id.nombreReceta)
        tvTiempoReceta = findViewById(R.id.tv_tiempo_receta_dia)
        tvPorcionesReceta = findViewById(R.id.tv_porciones_receta_dia)
        tvDificultadReceta = findViewById(R.id.tv_dificultad_receta_dia)
        preparacionReceta = findViewById(R.id.preparacionReceta)
        listaIngredientes = findViewById(R.id.listaIngredientes)
        btnAgregarReceta = findViewById(R.id.btn_agregar_receta)
        btnBack = findViewById(R.id.btn_back)
    }

    private fun obtenerRecetaDesdeIntent(): Receta {
        return Receta(
            id = intent.getStringExtra("receta_id") ?: "",
            nombre = intent.getStringExtra("receta_nombre") ?: "",
            imagenUrl = intent.getStringExtra("receta_imagen") ?: "",
            tiempo = intent.getLongExtra("receta_tiempo", 0),
            porciones = intent.getLongExtra("receta_porciones", 0),
            dificultad = intent.getLongExtra("receta_dificultad", 1),
            preparacion = intent.getStringExtra("receta_preparacion") ?: "",
            userId = intent.getStringExtra("receta_userId") ?: "",
            ingredientes = intent.getStringArrayListExtra("receta_ingredientes") ?: emptyList()
        )
    }

    private fun setupViews() {
        isMyRecipe = currentReceta.userId == auth.currentUser?.uid

        nombreReceta.text = currentReceta.nombre.ifEmpty { "Sin nombre" }
        tvTiempoReceta.text = "${currentReceta.tiempo} min."
        tvPorcionesReceta.text = "${currentReceta.porciones} porc."
        tvDificultadReceta.text = obtenerTextoDificultad(currentReceta.dificultad)
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

    private fun setupButtonActions() {
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
            guardarRecetaVista()
        }
    }

    private fun abrirPantallaEdicion() {
        Intent(this, EditarReceta::class.java).apply {
            putExtra("receta_id", currentReceta.id)
            putExtra("receta_nombre", currentReceta.nombre)
            putExtra("receta_imagen", currentReceta.imagenUrl)
            putExtra("receta_tiempo", currentReceta.tiempo)
            putExtra("receta_dificultad", currentReceta.dificultad)
            putExtra("receta_porciones", currentReceta.porciones)
            putExtra("receta_preparacion", currentReceta.preparacion)
            putStringArrayListExtra("receta_ingredientes", ArrayList(currentReceta.ingredientes))
        }.also { startActivity(it) }
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

    private fun obtenerTextoDificultad(nivel: Long): String {
        return when (nivel) {
            1L -> "Fácil"
            2L -> "Media"
            3L -> "Difícil"
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

    private fun guardarRecetaVista() {
        getSharedPreferences("app_prefs", Context.MODE_PRIVATE).edit {
            putString("ultima_receta_vista", currentReceta.id)
        }
    }
}