package com.example.frugalfeast

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class EditarPerfil : AppCompatActivity() {
    private lateinit var imageViewPerfil: ImageView
    private var selectedImageUri: Uri? = null
    private val storageRef = FirebaseStorage.getInstance().reference
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

        imageViewPerfil.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 1001)
        }


        val imgVolver = findViewById<ImageView>(R.id.imageView24)
        val btnGuardar = findViewById<Button>(R.id.button2)

        val editNombre = findViewById<EditText>(R.id.editTextText)
        val editApodo = findViewById<EditText>(R.id.editTextText2)
        val editFecha = findViewById<EditText>(R.id.editTextText3)
        val editCorreo = findViewById<EditText>(R.id.editTextText4)
        val editTelefono = findViewById<EditText>(R.id.editTextText5)
        val editNewContraseña = findViewById<EditText>(R.id.editTextText6)
        val editConfirmarc = findViewById<EditText>(R.id.editTextText7)

        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid
        if (uid == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Cargar datos existentes desde Firebase
        if (uid != null) {
            db.collection("usuarios").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        editNombre.setText(document.getString("nombre") ?: "")
                        editApodo.setText(document.getString("apodo") ?: "")
                        editCorreo.setText(document.getString("correo") ?: "")
                        // Puedes cargar más campos si lo deseas
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show()
                }
        }

        imgVolver.setOnClickListener {
            finish()
        }

        btnGuardar.setOnClickListener {
            val nuevoNombre = editNombre.text.toString()
            val nuevoApodo = editApodo.text.toString()
            val nuevoCorreo = editCorreo.text.toString()
            val nuevaContrasena = editNewContraseña.text.toString()
            val confirmarContrasena = editConfirmarc.text.toString()

            if (uid != null) {
                val userUpdates = hashMapOf(
                    "nombre" to nuevoNombre,
                    "apodo" to nuevoApodo,
                    "correo" to nuevoCorreo
                )

                db.collection("usuarios").document(uid).update(userUpdates as Map<String, Any>)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error al actualizar perfil", Toast.LENGTH_SHORT).show()
                    }

                // Actualizar contraseña si se ingresaron ambas iguales
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
            }

            //imagen
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

            // Finalizar actividad
            finish()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            selectedImageUri = data?.data
            imageViewPerfil.setImageURI(selectedImageUri) // vista previa
        }
    }

}
