package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class Reestablecer : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var usuarioApodo : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_reestablecer)

        auth = FirebaseAuth.getInstance()


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val reestablecerContra: Button = findViewById(R.id.enviar)
        reestablecerContra.setOnClickListener {
            reestablecerContraseña()
            finish()
        }
        val cancelar: Button = findViewById(R.id.cancelar)
        cancelar.setOnClickListener {
            val intent = Intent(this, Principal::class.java)
            startActivity(intent)
        }
    }


    private fun reestablecerContraseña() {
        val email = findViewById<EditText>(R.id.correoCampoContra).text.toString().trim()

        if (email.isEmpty()) {
            Toast.makeText(this, "Por favor ingresa tu correo", Toast.LENGTH_SHORT).show()
            return
        }

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Correo de recuperación enviado", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
