package com.example.frugalfeast

import android.widget.EditText
import android.widget.LinearLayout
import android.util.TypedValue
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.storage.StorageReference
import java.util.UUID


class AgregarRecetaSeg : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null
    private lateinit var storageRef: StorageReference
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_receta)

        try {
            initFirebase()
            setupViews()
            añadirCampoIngrediente()
            rellenarCampos()

        } catch (e: Exception) {
            Log.e("AgregarReceta", "Error en onCreate: ${e.stackTraceToString()}")
            mostrarToast("Error al inicializar la pantalla")
            finish()
        }
    }

    private fun initFirebase() {
        storageRef = FirebaseStorage.getInstance().reference
        db = FirebaseFirestore.getInstance()
    }

    private fun setupViews() {
        findViewById<ImageButton>(R.id.agregarImagen)?.setOnClickListener {
            openImagePicker()
        }

        findViewById<Button>(R.id.btnSubirReceta)?.setOnClickListener {
            subirRecetaFirestore()
        }

        findViewById<ImageButton>(R.id.agregarIngrediente)?.setOnClickListener {
            añadirCampoIngrediente()
        }
        findViewById<MaterialToolbar>(R.id.topAppBar).setNavigationOnClickListener {
            mostrarDialogoConfirmacionSalida()
        }
    }


    private fun openImagePicker() {
        try {
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
            }
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        } catch (e: Exception) {
            Log.e("AgregarReceta", "Error al abrir selector: ${e.stackTraceToString()}")
            mostrarToast("Error al abrir galería")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            try {
                imageUri = data?.data
                imageUri?.let {
                    findViewById<ImageView>(R.id.imgAgregarReceta)?.setImageURI(it)
                } ?: mostrarToast("No se pudo cargar la imagen")
            } catch (e: Exception) {
                Log.e("AgregarReceta", "Error al cargar imagen: ${e.stackTraceToString()}")
                mostrarToast("Error al procesar imagen")
            }
        }
    }

    private fun subirRecetaFirestore() {
        try {
            if (!validarCampos()) return

            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
                mostrarToast("Usuario no autenticado")
                return
            }

            val recetaId = UUID.randomUUID().toString()
            val imageRef = storageRef.child("recetas/$recetaId.jpg")

            imageUri?.let { uri ->
                mostrarToast("Subiendo receta...")

                imageRef.putFile(uri)
                    .addOnSuccessListener { task ->
                        task.storage.downloadUrl.addOnSuccessListener { downloadUri ->
                            subirDatosReceta(userId, recetaId, downloadUri.toString())
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("AgregarReceta", "Error subir imagen: ${e.stackTraceToString()}")
                        mostrarToast("Error al subir imagen")
                    }
            }
        } catch (e: Exception) {
            Log.e("AgregarReceta", "Error inesperado: ${e.stackTraceToString()}")
            mostrarToast("Error inesperado")
        }
    }


    private fun validarCampos(): Boolean {
        val nombre = findViewById<EditText>(R.id.nombreCampoAgregar)?.text?.toString()?.trim() ?: ""
        val preparacion = findViewById<EditText>(R.id.preparacionCampoAgregar)?.text?.toString()?.trim() ?: ""
        val tiempo = findViewById<EditText>(R.id.tiempoCampoAgregar)?.text?.toString()?.trim() ?: ""
        val porciones = findViewById<EditText>(R.id.porcionesCampoAgregar)?.text?.toString()?.trim() ?: ""
        val calorias = findViewById<EditText>(R.id.caloriasCampoAgregar)?.text?.toString()?.trim() ?: ""
        val dificultad = findViewById<EditText>(R.id.dificultadCampoAgregar)?.text?.toString()?.trim() ?: ""
        val ingredientes = obtenerIngredientes()

        if (nombre.isEmpty() || preparacion.isEmpty() || tiempo.isEmpty() ||
            porciones.isEmpty() || calorias.isEmpty() || dificultad.isEmpty() ||
            imageUri == null || ingredientes.isEmpty()) {
            mostrarToast("Complete todos los campos")
            return false
        }

        if (dificultad.toIntOrNull() !in 1..3) {
            mostrarToast("Dificultad debe ser 1, 2 o 3")
            return false
        }

        return true
    }

    private fun obtenerIngredientes(): List<String> {
        val ingredientes = mutableListOf<String>()
        val contenedor = findViewById<LinearLayout>(R.id.contenedorIngredientes) ?: return ingredientes

        for (i in 0 until contenedor.childCount) {
            val view = contenedor.getChildAt(i)
            if (view is EditText) {
                view.text?.toString()?.trim()?.takeIf { it.isNotEmpty() }?.let {
                    ingredientes.add(it)
                }
            }
        }
        return ingredientes
    }

    private fun subirDatosReceta(userId: String, recetaId: String, imageUrl: String) {
        try {
            val receta = hashMapOf<String, Any>(
                "nombre" to (findViewById<EditText>(R.id.nombreCampoAgregar)?.text?.toString()?.trim() ?: ""),
                "preparacion" to (findViewById<EditText>(R.id.preparacionCampoAgregar)?.text?.toString()?.trim() ?: ""),
                "tiempo" to (findViewById<EditText>(R.id.tiempoCampoAgregar)?.text?.toString()?.toIntOrNull()?.takeIf { it >= 0 } ?: 0),
                "porciones" to (findViewById<EditText>(R.id.porcionesCampoAgregar)?.text?.toString()?.toIntOrNull()?.takeIf { it > 0 } ?: 1),
                "calorias" to (findViewById<EditText>(R.id.caloriasCampoAgregar)?.text?.toString()?.toIntOrNull() ?: 0),
                "dificultad" to (findViewById<EditText>(R.id.dificultadCampoAgregar)?.text?.toString()?.toIntOrNull()?.coerceIn(1, 3) ?: 1),
                "ingredientes" to obtenerIngredientes().filter { it.isNotBlank() },
                "imagenUrl" to imageUrl,
                "fechaCreacion" to FieldValue.serverTimestamp(),
                "userId" to userId,
                "esPersonalizada" to true
            )

            db.collection("Receta").document(recetaId).set(receta)
                .addOnSuccessListener {
                    mostrarToast("¡Receta agregada!")
                    setResult(Activity.RESULT_OK)
                    limpiarCampos()
                    finish()
                }
                .addOnFailureListener { e ->
                    Log.e("AgregarReceta", "Error Firestore: ${e.stackTraceToString()}")
                    mostrarToast("Error al guardar receta")
                }
        } catch (e: Exception) {
            Log.e("AgregarReceta", "Error al preparar datos: ${e.stackTraceToString()}")
            mostrarToast("Error al preparar datos")
        }
    }


    private fun añadirCampoIngrediente() {
        try {
            val contenedor = findViewById<LinearLayout>(R.id.contenedorIngredientes) ?: return
            val base = findViewById<EditText>(R.id.ingrediente1) ?: return

            val nuevoIngrediente = EditText(this).apply {
                layoutParams = base.layoutParams
                background = base.background
                hint = base.hint
                setTextColor(base.currentTextColor)
                setTextSize(TypedValue.COMPLEX_UNIT_PX, base.textSize)
                inputType = base.inputType
                setPaddingRelative(
                    base.paddingStart,
                    base.paddingTop,
                    base.paddingEnd,
                    base.paddingBottom
                )
            }

            contenedor.addView(nuevoIngrediente)
        } catch (e: Exception) {
            Log.e("AgregarReceta", "Error añadir campo: ${e.stackTraceToString()}")
            mostrarToast("Error al agregar campo")
        }
    }

    private fun rellenarCampos() {
        val nombre = intent.getStringExtra("nombre")
        val preparacion = intent.getStringExtra("preparacion")
        val tiempo = intent.getStringExtra("tiempo")
        val dificultad = intent.getStringExtra("dificultad")
        val porciones = intent.getStringExtra("porciones")
        val calorias = intent.getStringExtra("calorias")
        val ingredientes = intent.getStringArrayExtra("ingredientes")?.toList() ?: emptyList()

        Log.d("AgregarReceta", "Nombre: $nombre")
        Log.d("AgregarReceta", "Preparación: $preparacion")
        Log.d("AgregarReceta", "Ingredientes: $ingredientes")

        // Aquí puedes asignar los valores a los campos del layout
        findViewById<EditText>(R.id.nombreCampoAgregar)?.setText(nombre)
        findViewById<EditText>(R.id.preparacionCampoAgregar)?.setText(preparacion)
        findViewById<EditText>(R.id.tiempoCampoAgregar)?.setText(tiempo)
        findViewById<EditText>(R.id.dificultadCampoAgregar)?.setText(dificultad)
        findViewById<EditText>(R.id.porcionesCampoAgregar)?.setText(porciones)
        findViewById<EditText>(R.id.caloriasCampoAgregar)?.setText(calorias)

        val contenedorIngredientes = findViewById<LinearLayout>(R.id.contenedorIngredientes)
        contenedorIngredientes.removeAllViews()

        ingredientes.forEach { ingrediente ->
            val editText = EditText(this)
            editText.setText(ingrediente)
            contenedorIngredientes.addView(editText)
        }
    }


    private fun limpiarCampos() {
        try {
            findViewById<EditText>(R.id.nombreCampoAgregar)?.text?.clear()
            findViewById<EditText>(R.id.preparacionCampoAgregar)?.text?.clear()
            findViewById<EditText>(R.id.tiempoCampoAgregar)?.text?.clear()
            findViewById<EditText>(R.id.porcionesCampoAgregar)?.text?.clear()
            findViewById<EditText>(R.id.caloriasCampoAgregar)?.text?.clear()
            findViewById<EditText>(R.id.dificultadCampoAgregar)?.text?.clear()
            findViewById<ImageView>(R.id.imgAgregarReceta)?.setImageDrawable(null)
            imageUri = null

            val contenedor = findViewById<LinearLayout>(R.id.contenedorIngredientes)
            contenedor?.removeAllViews()
            añadirCampoIngrediente()

        } catch (e: Exception) {
            Log.e("AgregarReceta", "Error limpiar campos: ${e.stackTraceToString()}")
        }
    }

    private fun mostrarToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun mostrarDialogoConfirmacionSalida() {
        AlertDialog.Builder(this)
            .setTitle("¿Salir sin guardar?")
            .setMessage("Tienes cambios sin guardar. ¿Estás seguro de que quieres salir?")
            .setPositiveButton("Salir") { _, _ ->
                finish()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }


    override fun onDestroy() {
        super.onDestroy()
        imageUri = null
    }
}
