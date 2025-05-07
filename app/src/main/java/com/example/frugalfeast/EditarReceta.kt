package com.example.frugalfeast

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class EditarReceta : AppCompatActivity() {

    // Variables de vista
    private lateinit var imagenReceta: ImageView
    private lateinit var editDificultad: EditText
    private lateinit var editPreparacion: EditText
    private lateinit var editIngredientes: EditText
    private lateinit var editTiempo: EditText
    private lateinit var editPorciones: EditText
    private lateinit var botonTerminado: ImageView
    private lateinit var botonCambiarImagen: ImageButton

    // Variables de control
    private var hayCambios = false
    private var uriImagen: Uri? = null
    private var idReceta: String = ""
    private var urlImagen: String = ""

    // Firebase
    private val baseDatos = FirebaseFirestore.getInstance()
    private val almacenamiento = FirebaseStorage.getInstance()
    private val autenticacion = FirebaseAuth.getInstance()

    // Lanzador para seleccionar imagen
    private val lanzadorSeleccionImagen = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            try {
                uriImagen = it
                Glide.with(this)
                    .load(it)
                    .into(imagenReceta)
                hayCambios = true
            } catch (e: Exception) {
                Log.e("EditarReceta", "Error al cargar imagen: ${e.stackTraceToString()}")
                mostrarToast("Error al procesar imagen")
            }
        } ?: mostrarToast("No se pudo cargar la imagen")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_receta)

        inicializarVistas()
        cargarDatosIniciales()
        configurarListeners()
    }

    private fun inicializarVistas() {
        imagenReceta = findViewById(R.id.imgEditarReceta)
        editDificultad = findViewById(R.id.etDificultad)
        editPreparacion = findViewById(R.id.et_preparacion)
        editIngredientes = findViewById(R.id.et_ingredientes)
        editTiempo = findViewById(R.id.etTiempo)
        editPorciones = findViewById(R.id.etPorciones)
        botonTerminado = findViewById(R.id.btn_terminado)
        botonCambiarImagen = findViewById(R.id.btnEditarImg)
    }

    private fun cargarDatosIniciales() {
        idReceta = intent.getStringExtra("receta_id") ?: ""
        urlImagen = intent.getStringExtra("receta_imagen") ?: ""
        val tiempo = intent.getLongExtra("receta_tiempo", 0)
        val dificultad = intent.getLongExtra("receta_dificultad", 0)
        val porciones = intent.getLongExtra("receta_porciones", 0)
        val preparacion = intent.getStringExtra("receta_preparacion") ?: ""
        val ingredientes = intent.getStringArrayListExtra("receta_ingredientes") ?: emptyList()

        // Configurar vistas con datos iniciales
        findViewById<TextView>(R.id.nombreReceta).text = intent.getStringExtra("receta_nombre") ?: ""
        editPreparacion.setText(preparacion)
        editIngredientes.setText(ingredientes.joinToString("\n"))
        editTiempo.setText(tiempo.toString())
        editPorciones.setText(porciones.toString())
        editDificultad.setText(obtenerTextoDificultad(dificultad.toString()))

        if (urlImagen.isNotEmpty()) {
            Glide.with(this).load(urlImagen).into(imagenReceta)
        }
    }

    private fun configurarListeners() {
        botonCambiarImagen.setOnClickListener {
            lanzadorSeleccionImagen.launch("image/*")
        }

        botonTerminado.setOnClickListener {
            guardarCambios()
        }

        val escuchadorTexto = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                hayCambios = true
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        editPreparacion.addTextChangedListener(escuchadorTexto)
        editIngredientes.addTextChangedListener(escuchadorTexto)
        editTiempo.addTextChangedListener(escuchadorTexto)
        editPorciones.addTextChangedListener(escuchadorTexto)
        editDificultad.addTextChangedListener(escuchadorTexto)
    }

    private fun obtenerTextoDificultad(dificultad: String): String {
        return try {
            when(dificultad.toInt()) {
                1 -> "Fácil"
                2 -> "Media"
                3 -> "Difícil"
                else -> ""
            }
        } catch (e: NumberFormatException) {
            Log.e("EditarReceta", "Error al convertir dificultad: ${e.message}")
            ""
        }
    }

    private fun obtenerNumeroDificultad(textoDificultad: String): Int {
        return when(textoDificultad) {
            "Fácil" -> 1
            "Media" -> 2
            "Difícil" -> 3
            else -> 0
        }
    }

    private fun guardarCambios() {
        val preparacion = editPreparacion.text.toString().trim()
        val ingredientes = editIngredientes.text.toString().trim().split("\n")
        val tiempo = editTiempo.text.toString().trim().toLongOrNull() ?: 0
        val porciones = editPorciones.text.toString().trim().toLongOrNull() ?: 0
        val dificultad = obtenerNumeroDificultad(editDificultad.text.toString().trim())

        if (preparacion.isEmpty() || ingredientes.isEmpty() || tiempo <= 0 || porciones <= 0 || dificultad == 0) {
            mostrarToast("Complete todos los campos correctamente")
            return
        }

        if (uriImagen != null) {
            subirImagenYGuardar(preparacion, ingredientes, tiempo, porciones, dificultad)
        } else {
            actualizarReceta(preparacion, ingredientes, tiempo, porciones, dificultad, urlImagen)
        }
    }

    private fun subirImagenYGuardar(
        preparacion: String,
        ingredientes: List<String>,
        tiempo: Long,
        porciones: Long,
        dificultad: Int
    ) {
        val idUsuario = autenticacion.currentUser?.uid ?: return
        val referenciaAlmacenamiento = almacenamiento.reference
        val referenciaImagen = referenciaAlmacenamiento.child("recetas/${idUsuario}/${UUID.randomUUID()}")

        uriImagen?.let { uri ->
            referenciaImagen.putFile(uri)
                .addOnSuccessListener {
                    referenciaImagen.downloadUrl.addOnSuccessListener { urlDescarga ->
                        actualizarReceta(preparacion, ingredientes, tiempo, porciones, dificultad, urlDescarga.toString())
                    }
                }
                .addOnFailureListener { e ->
                    mostrarToast("Error al subir imagen: ${e.message}")
                }
        }
    }

    private fun actualizarReceta(
        preparacion: String,
        ingredientes: List<String>,
        tiempo: Long,
        porciones: Long,
        dificultad: Int,
        urlImagen: String
    ) {
        val idUsuario = autenticacion.currentUser?.uid ?: return
        val nombre = intent.getStringExtra("receta_nombre") ?: ""

        val recetaActualizada = hashMapOf(
            "nombre" to nombre,
            "imagenUrl" to urlImagen,
            "preparacion" to preparacion,
            "ingredientes" to ingredientes,
            "tiempo" to tiempo,
            "porciones" to porciones,
            "dificultad" to dificultad,
            "userId" to idUsuario,
            "fechaCreacion" to FieldValue.serverTimestamp()
        )

        baseDatos.collection("Receta")
            .document(idReceta)
            .update(recetaActualizada as Map<String, Any>)
            .addOnSuccessListener {
                mostrarToast("Receta actualizada con éxito")
                setResult(Activity.RESULT_OK)
                finish()
            }
            .addOnFailureListener { e ->
                mostrarToast("Error al actualizar receta: ${e.message}")
            }
    }

    private fun mostrarToast(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        if (hayCambios || uriImagen != null) {
            mostrarDialogoConfirmacionSalida()
        } else {
            super.onBackPressed()
        }
    }

    private fun mostrarDialogoConfirmacionSalida() {
        AlertDialog.Builder(this)
            .setTitle("¿Salir sin guardar?")
            .setMessage("Tienes cambios sin guardar. ¿Estás seguro de que quieres salir?")
            .setPositiveButton("Salir") { _, _ ->
                finish()
            }
            .setNegativeButton("Cancelar", null)
            .setNeutralButton("Guardar y salir") { _, _ ->
                guardarCambios()
            }
            .show()
    }
}