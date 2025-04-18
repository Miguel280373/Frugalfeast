package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class Registro : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)

        auth = FirebaseAuth.getInstance()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val registrarse: Button = findViewById(R.id.registrarse)
        registrarse.setOnClickListener {
            registrarUsuario()
        }
        val textViewIniciar = findViewById<TextView>(R.id.textView11)
        textViewIniciar.setOnClickListener {
            val intent = Intent(this, IniciarSesion::class.java)
            startActivity(intent)
        }

    }
    private fun registrarUsuario() {
        val email = findViewById<EditText>(R.id.correoCampo).text.toString().trim()
        val contraseña = findViewById<EditText>(R.id.contraseñaCampo).text.toString().trim()
        val nombre = findViewById<EditText>(R.id.nombreCampo).text.toString().trim()
        val apodo = findViewById<EditText>(R.id.apodoCampo).text.toString().trim()
        val confirmarContraseña = findViewById<EditText>(R.id.confirmarContraCampo).text.toString().trim()
        val aceptoTerminos = findViewById<CheckBox>(R.id.checkBoxTerminos).isChecked

        if (nombre.isEmpty() || email.isEmpty() || contraseña.isEmpty() || confirmarContraseña.isEmpty() || aceptoTerminos) {
            Toast.makeText(this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (contraseña != confirmarContraseña) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, contraseña)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, Reestablecer::class.java)
                    intent.putExtra("APODO", apodo)
                    startActivity(intent)

                } else {
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}