
package com.example.frugalfeast

import android.os.Build
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import android.graphics.Color
import android.util.TypedValue
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.storage.StorageReference
import java.util.UUID


class AgregarReceta: AppCompatActivity() {

    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null
    private lateinit var storageRef: StorageReference
    private lateinit var db: FirebaseFirestore

    private lateinit var btnSubir: Button
    private lateinit var btnAñadirCampo: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_receta)

        storageRef = FirebaseStorage.getInstance().reference
        db = FirebaseFirestore.getInstance()

        findViewById<ImageButton>(R.id.agregarImagen).setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }
        btnSubir = findViewById(R.id.btnSubirReceta)
        btnSubir.setOnClickListener {
            subirRecetaFirestore()
        }
        btnAñadirCampo = findViewById(R.id.agregarIngrediente)
        btnAñadirCampo.setOnClickListener {
            añadirCampoIngrediente()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
            findViewById<ImageView>(R.id.imageView32).setImageURI(imageUri)
        }

    }

    private fun subirRecetaFirestore() {
        val nombre = findViewById<EditText>(R.id.nombreCampoAgregar).text.toString().trim()
        val preparacion = findViewById<EditText>(R.id.preparacionCampoAgregar).text.toString().trim()
        val tiempo = findViewById<EditText>(R.id.tiempoCampoAgregar).text.toString().trim()
        val porciones = findViewById<EditText>(R.id.porcionesCampoAgregar).text.toString().trim()
        val calorias = findViewById<EditText>(R.id.caloriasCampoAgregar).text.toString().trim()
        val dificultad = findViewById<EditText>(R.id.dificultadCampoAgregar).text.toString().trim()
        val ingredientes = mutableListOf<String>()
        val contenedor = findViewById<LinearLayout>(R.id.contenedorIngredientes)
        for (i in 0 until contenedor.childCount) {
            val editText = contenedor.getChildAt(i) as EditText
            val texto = editText.text.toString().trim()
            if (texto.isNotEmpty()) ingredientes.add(texto)
        }

        if (nombre.isEmpty() || preparacion.isEmpty() || tiempo.isEmpty() || porciones.isEmpty() || calorias.isEmpty()
            || dificultad.isEmpty() || imageUri == null || ingredientes.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (dificultad.toIntOrNull() !in 1..3) {
            Toast.makeText(this, "Dificultad debe ser un número entre 1 y 3", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        val recetaId = UUID.randomUUID().toString()
        val imageRef = storageRef.child("recetas/$recetaId.jpg")

        imageUri?.let {
            imageRef.putFile(it).addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val receta = hashMapOf(
                        "nombre" to nombre,
                        "preparacion" to preparacion,
                        "tiempo" to tiempo.toInt(),
                        "porciones" to porciones.toInt(),
                        "calorias" to calorias.toInt(),
                        "dificultad" to dificultad.toInt(),
                        "ingredientes" to ingredientes,
                        "imagenUrl" to uri.toString(),
                        "fechaCreacion" to FieldValue.serverTimestamp(),
                        "userId" to userId,
                        "esPersonalizada" to true
                    )

                    db.collection("Receta").add(receta).addOnSuccessListener {
                        Toast.makeText(this, "Receta agregada", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Toast.makeText(this, "Error al subir receta", Toast.LENGTH_SHORT).show()
                    }
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Error al subir imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun añadirCampoIngrediente() {
        val nuevoIngrediente = EditText(this).apply {

            layoutParams = LinearLayout.LayoutParams(
                300,
                46
            ).apply {
                topMargin = 8
            }

            background = ContextCompat.getDrawable(context, R.drawable.roundededittext2)
            hint = "Editar"
            setTextColor(Color.BLACK)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            inputType = InputType.TYPE_CLASS_TEXT
            setPadding(12, 0, 0, 0)

            layoutDirection = View.LAYOUT_DIRECTION_LTR
        }

        findViewById<LinearLayout>(R.id.contenedorIngredientes).addView(nuevoIngrediente)
    }
}
