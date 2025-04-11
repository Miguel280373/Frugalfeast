package com.example.frugalfeast

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
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



class IniciarSesion : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var auth: FirebaseAuth

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        setContentView(R.layout.activity_iniciar_sesion)


        val iniciarsesion1: Button = findViewById(R.id.iniciarsesion1)
        iniciarsesion1.setOnClickListener {
            loginUser()
        }
        val textViewCrearCuenta = findViewById<TextView>(R.id.sinCuentaInicio)
        textViewCrearCuenta.setOnClickListener {
            val intent = Intent(this, Registro::class.java)
            startActivity(intent)
        }
        val textViewOlvide = findViewById<TextView>(R.id.recuperarCuentaInicio)
        textViewOlvide.setOnClickListener {
            val intent = Intent(this, Recuperar::class.java)
            startActivity(intent)
        }
        val principal = findViewById<Button>(R.id.principal)
        principal.setOnClickListener(){
            val intent = Intent(this, PantallaPrincipal::class.java)
            startActivity(intent)        }
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            currentUser.reload()
        }
    }
    private fun loginUser() {
        val email = findViewById<EditText>(R.id.usuarioOcorreo).text.toString().trim()
        val password = findViewById<EditText>(R.id.contraseñaCampoInicio).text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Ingresa un correo válido", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, PantallaPrincipal::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}