package com.example.frugalfeast

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class EditarPerfil : AppCompatActivity() {
    private lateinit var imageViewPerfil: ImageView
    private var selectedImageUri: Uri? = null
    private val storageRef = FirebaseStorage.getInstance().reference
    private lateinit var editNombre: EditText
    private lateinit var editApodo: EditText
    private lateinit var editFecha: EditText
    private lateinit var editEmail: EditText
    private lateinit var editTelefono: EditText
    private lateinit var editNewContraseña: EditText
    private lateinit var editConfirmarc: EditText
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val uid = auth.currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_perfil)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        imageViewPerfil = findViewById(R.id.imageView332)
        editNombre = findViewById(R.id.editTextText)
        editApodo = findViewById(R.id.editTextText2)
        editFecha = findViewById(R.id.editTextText3)
        editEmail = findViewById(R.id.editTextText4)
        editTelefono = findViewById(R.id.editTextText5)
        editNewContraseña = findViewById(R.id.editTextText6)
        editConfirmarc = findViewById(R.id.editTextText7)
        val imgVolver = findViewById<ImageButton>(R.id.btn_atras_editar_perfil)
        val btnGuardar = findViewById<Button>(R.id.button2)
        val calendar = Calendar.getInstance()

        imageViewPerfil.setOnClickListener {
            seleccionarImagenPerfil(it)
        }

        editFecha.setOnClickListener {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val fechaFormateada = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
                editFecha.setText(fechaFormateada)
            }, year, month, day)
            datePicker.show()
        }

        if (uid == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        cargarDatosUsuario()

        imgVolver.setOnClickListener {
            mostrarDialogoConfirmacionSalida()
        }

        btnGuardar.setOnClickListener {
            guardarCambios()
        }
    }

    // Nueva función para seleccionar la imagen de perfil
    fun seleccionarImagenPerfil(view: View) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 1001)
    }

    private fun cargarDatosUsuario() {
        db.collection("usuarios").document(uid!!).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    editNombre.setText(document.getString("nombre") ?: "")
                    editApodo.setText(document.getString("apodo") ?: "")
                    editEmail.setText(document.getString("email") ?: "")
                    editFecha.setText(document.getString("fechaNacimiento") ?: "")
                    editTelefono.setText(document.getString("telefono") ?: "")
                    val fotoPerfilUrl = document.getString("fotoPerfil")
                    if (!fotoPerfilUrl.isNullOrEmpty()) {
                        Glide.with(this)
                            .load(fotoPerfilUrl)
                            .placeholder(R.drawable.negro)
                            .error(R.drawable.negro)
                            .into(imageViewPerfil)
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show()
            }
    }

    private fun guardarCambios() {
        val nuevoNombre = editNombre.text.toString()
        val nuevoApodo = editApodo.text.toString()
        val nuevoEmail = editEmail.text.toString()
        val nuevoTelefono = editTelefono.text.toString()
        val nuevaFecha = editFecha.text.toString()
        val nuevaContrasena = editNewContraseña.text.toString()
        val confirmarContrasena = editConfirmarc.text.toString()

        val userUpdates = hashMapOf(
            "nombre" to nuevoNombre,
            "apodo" to nuevoApodo,
            "email" to nuevoEmail,
            "telefono" to nuevoTelefono,
            "fechaNacimiento" to nuevaFecha
        )

        db.collection("usuarios").document(uid!!).update(userUpdates as Map<String, Any>)
            .addOnSuccessListener {
                Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al actualizar perfil", Toast.LENGTH_SHORT).show()
            }

        if (nuevaContrasena.isNotEmpty() && nuevaContrasena == confirmarContrasena) {
            auth.currentUser?.updatePassword(nuevaContrasena)
                ?.addOnSuccessListener {
                    Toast.makeText(this, "Contraseña actualizada", Toast.LENGTH_SHORT).show()
                }
                ?.addOnFailureListener {
                    Toast.makeText(this, "Error al actualizar contraseña", Toast.LENGTH_SHORT).show()
                }
        } else if (nuevaContrasena != confirmarContrasena) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
        }

        if (selectedImageUri != null) {
            val imageRef = storageRef.child("fotos_perfil/$uid.jpg")
            imageRef.putFile(selectedImageUri!!)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        db.collection("usuarios").document(uid)
                            .update("fotoPerfil", downloadUri.toString())
                            .addOnSuccessListener {
                                Toast.makeText(this, "Imagen de perfil actualizada", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Error al guardar imagen en Firestore", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al subir imagen", Toast.LENGTH_SHORT).show()
                }
        }

        val nuevo_telefono = editTelefono.text.toString()
        if (!nuevo_telefono.matches(Regex("^\\d{7,10}\$"))) {
            Toast.makeText(this, "Ingrese un número de teléfono válido (solo números)", Toast.LENGTH_SHORT).show()
            return
        }

        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            selectedImageUri = data?.data
            imageViewPerfil.setImageURI(selectedImageUri)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        mostrarDialogoConfirmacionSalida()
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