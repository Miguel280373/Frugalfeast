package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class Eliminar_Cuenta : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eliminar_cuenta)

        auth = FirebaseAuth.getInstance()

        val buttonAtras: ImageButton = findViewById(R.id.button19)
        val continuarButton: Button = findViewById(R.id.button20)
        val passwordEditText: EditText = findViewById(R.id.editTextNumberPassword)

        // Acción de ir atrás
        buttonAtras.setOnClickListener {
            onBackPressed()  // Vuelve a la pantalla anterior
        }

        // Acción para continuar con la eliminación de la cuenta
        continuarButton.setOnClickListener {
            val password = passwordEditText.text.toString().trim()

            if (password.isEmpty()) {
                Toast.makeText(this, "Por favor ingresa tu contraseña", Toast.LENGTH_SHORT).show()
            } else {
                verificarYEliminarCuenta(password)
            }

        }
    }

    private fun verificarYEliminarCuenta(password: String) {
        val user = auth.currentUser
        if (user != null) {
            // Verificar la contraseña ingresada
            val credential = EmailAuthProvider.getCredential(user.email!!, password)
            user.reauthenticate(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Si la autenticación es exitosa, eliminar la cuenta
                        eliminarCuenta()
                    } else {
                        Toast.makeText(this, "Contraseña incorrecta. Intenta nuevamente.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun eliminarCuenta() {
        val user = auth.currentUser
        user?.delete()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Cuenta eliminada exitosamente", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java) // Redirige a la pantalla principal
                    startActivity(intent)
                    finish() // Finaliza la actividad actual
                } else {
                    Toast.makeText(this, "Error al eliminar la cuenta: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
