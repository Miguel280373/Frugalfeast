package com.example.frugalfeast

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.frugalfeast.R
import com.google.firebase.auth.FirebaseAuth

class Recuperar : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var reenviarCorreoBtn: AppCompatButton
    private lateinit var reenviarText: TextView
    private lateinit var correoCampo: EditText
    private var emailIngresado: String = ""
    private var correoEnviado = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        auth = FirebaseAuth.getInstance()
        setContentView(R.layout.activity_recuperar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        reenviarCorreoBtn = findViewById(R.id.reenviarCodigo)
        reenviarText = findViewById(R.id.textReenviar)
        correoCampo = findViewById(R.id.correoCampoRecuperar)

        findViewById<AppCompatButton>(R.id.enviar).setOnClickListener {
            reestablecerContraseña()
        }

        reenviarCorreoBtn.setOnClickListener {
            if (correoEnviado) {
                correoCampo.text.clear()
                reenviarCorreo()

            }
        }

        findViewById<ImageView>(R.id.imageView14).setOnClickListener {
            finish()
        }

        findViewById<AppCompatButton>(R.id.cancelar).setOnClickListener {
            finish()
        }
    }


    private fun reestablecerContraseña() {
        emailIngresado = correoCampo.text.toString().trim()

        if (emailIngresado.isEmpty()) {
            Toast.makeText(this, "Por favor ingresa tu correo", Toast.LENGTH_SHORT).show()
            return
        }

        auth.sendPasswordResetEmail(emailIngresado)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    correoEnviado = true
                    Toast.makeText(
                        this,
                        "Correo de recuperación enviado a $emailIngresado",
                        Toast.LENGTH_SHORT
                    ).show()

                    Handler(Looper.getMainLooper()).postDelayed({
                        reenviarText.visibility = View.VISIBLE
                        reenviarCorreoBtn.visibility = View.VISIBLE
                    }, 3000)
                } else {
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    private fun reenviarCorreo() {
        if (emailIngresado.isEmpty()) {
            Toast.makeText(this, "No hay correo para reenviar", Toast.LENGTH_SHORT).show()
            return
        }

        auth.sendPasswordResetEmail(emailIngresado)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Correo reenviado a $emailIngresado", Toast.LENGTH_SHORT)
                        .show()

                    reenviarText.visibility = View.GONE
                    reenviarCorreoBtn.visibility = View.GONE
                    Handler(Looper.getMainLooper()).postDelayed({
                        reenviarCorreoBtn.visibility = View.VISIBLE
                        reenviarText.visibility = View.VISIBLE
                    }, 3000)
                } else {
                    Toast.makeText(
                        this,
                        "Error al reenviar: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        val imgVolver = findViewById<ImageView>(R.id.imageView14)

        imgVolver.setOnClickListener {
            finish()
        }
    }

}
