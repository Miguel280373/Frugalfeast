package com.example.frugalfeast

import android.app.Activity
import android.app.AlertDialog
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

    private lateinit var imagenReceta: ImageView
    private lateinit var editDificultad: EditText
    private lateinit var editPreparacion: EditText
    private lateinit var editIngredientes: EditText
    private lateinit var editTiempo: EditText
    private lateinit var editPorciones: EditText
    private lateinit var editCalorias: EditText
    private lateinit var botonTerminado: ImageView
    private lateinit var botonCambiarImagen: ImageButton
    private lateinit var botonAtras: ImageView
    private lateinit var nombreReceta: TextView

    private var hayCambios = false
    private var uriImagen: Uri? = null
    private var idReceta: String = ""
    private var urlImagen: String = ""

    // Firebase
    private val baseDatos = FirebaseFirestore.getInstance()
    private val almacenamiento = FirebaseStorage.getInstance()
    private val autenticacion = FirebaseAuth.getInstance()

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
        obtenerIdReceta()
        configurarListeners()
    }

    private fun inicializarVistas() {
        imagenReceta = findViewById(R.id.imgEditarReceta)
        editDificultad = findViewById(R.id.etDificultad)
        editPreparacion = findViewById(R.id.et_preparacion)
        editIngredientes = findViewById(R.id.et_ingredientes)
        editTiempo = findViewById(R.id.etTiempo)
        editPorciones = findViewById(R.id.etPorciones)
        editCalorias = findViewById(R.id.etCalorias)
        botonTerminado = findViewById(R.id.btn_terminado)
        botonCambiarImagen = findViewById(R.id.btnEditarImg)
        botonAtras = findViewById(R.id.btnAtrasEditar)
        nombreReceta = findViewById(R.id.nombreReceta)
    }

    private fun obtenerIdReceta() {
        idReceta = intent.getStringExtra("receta_id") ?: ""
        if (idReceta.isEmpty()) {
            mostrarToast("No se encontró la receta")
            finish()
            return
        }
        cargarDatosReceta()
        configurarListeners()
    }

    private fun cargarDatosReceta() {
        baseDatos.collection("Receta").document(idReceta).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    urlImagen = document.getString("imagenUrl") ?: ""
                    val tiempo = document.getLong("tiempo") ?: 0
                    val dificultad = document.getLong("dificultad") ?: 0
                    val porciones = document.getLong("porciones") ?: 0
                    val calorias = document.getLong("calorias") ?: 0
                    val preparacion = document.getString("preparacion") ?: ""
                    val ingredientes = document.get("ingredientes") as? List<String> ?: emptyList()
                    val nombre = document.getString("nombre") ?: ""

                    nombreReceta.text = nombre
                    editPreparacion.setText(preparacion)
                    editIngredientes.setText(ingredientes.joinToString("\n"))
                    editTiempo.setText(tiempo.toString())
                    editPorciones.setText(porciones.toString())
                    editCalorias.setText(calorias.toString())
                    editDificultad.setText(dificultad.toString()) // Mostrar dificultad como número

                    if (urlImagen.isNotEmpty()) {
                        Glide.with(this).load(urlImagen).into(imagenReceta)
                    }
                } else {
                    mostrarToast("La receta no existe")
                    finish()
                }
            }
            .addOnFailureListener { e ->
                mostrarToast("Error al cargar receta: ${e.message}")
                finish()
            }
    }



    private fun configurarListeners() {
        botonCambiarImagen.setOnClickListener {
            lanzadorSeleccionImagen.launch("image/*")
        }

        botonTerminado.setOnClickListener {
            guardarCambios()
        }

        botonAtras.setOnClickListener {
            onBackPressed()
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
        editCalorias.addTextChangedListener(escuchadorTexto)
    }


    private fun guardarCambios() {
        val preparacion = editPreparacion.text.toString().trim()
        val ingredientes = editIngredientes.text.toString().trim().split("\n").filter { it.isNotBlank() }
        val tiempo = editTiempo.text.toString().trim().toLongOrNull() ?: 0
        val porciones = editPorciones.text.toString().trim().toLongOrNull() ?: 0
        val calorias = editCalorias.text.toString().trim().toLongOrNull() ?: 0
        val dificultadTexto = editDificultad.text.toString().trim()
        val dificultad = dificultadTexto.toIntOrNull() ?: 0

        if (preparacion.isEmpty() || ingredientes.isEmpty() || tiempo <= 0 ||
            porciones <= 0 || dificultad !in 1..3 || calorias <= 0) {
            mostrarToast("Complete todos los campos correctamente")
            return
        }

        if (uriImagen != null) {
            subirImagenYGuardar(preparacion, ingredientes, tiempo, porciones, dificultad, calorias)
        } else {
            actualizarReceta(preparacion, ingredientes, tiempo, porciones, dificultad, calorias, urlImagen)
        }
    }

    private fun subirImagenYGuardar(
        preparacion: String,
        ingredientes: List<String>,
        tiempo: Long,
        porciones: Long,
        dificultad: Int,
        calorias: Long
    ) {
        val idUsuario = autenticacion.currentUser?.uid ?: return
        val referenciaAlmacenamiento = almacenamiento.reference
        val referenciaImagen = referenciaAlmacenamiento.child("recetas/${idUsuario}/${UUID.randomUUID()}")

        uriImagen?.let { uri ->
            referenciaImagen.putFile(uri)
                .addOnSuccessListener {
                    referenciaImagen.downloadUrl.addOnSuccessListener { urlDescarga ->
                        actualizarReceta(preparacion, ingredientes, tiempo, porciones, dificultad, calorias, urlDescarga.toString())
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
        calorias: Long,
        urlImagen: String
    ) {
        val nombre = nombreReceta.text.toString()

        val recetaActualizada = hashMapOf(
            "nombre" to nombre,
            "imagenUrl" to urlImagen,
            "preparacion" to preparacion,
            "ingredientes" to ingredientes,
            "tiempo" to tiempo,
            "porciones" to porciones,
            "dificultad" to dificultad,
            "calorias" to calorias,
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
            .show()
    }
}