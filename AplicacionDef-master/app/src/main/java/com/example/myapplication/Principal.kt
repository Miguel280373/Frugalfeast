package com.example.myapplication

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth



class Principal : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_principal)

        // Initialize Firebase Auth
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val iniciarsesion: Button = findViewById(R.id.iniciarsesion)
        iniciarsesion.setOnClickListener {
            Intent(this, IniciarSesion::class.java)
                .also { welcomeIntent ->
                    //Launch
                    startActivity(welcomeIntent)
                }
        }
        val crearCuenta = findViewById<TextView>(R.id.crearCuentaBtnInicio)
        crearCuenta.setOnClickListener {
            val intent = Intent(this, Registro::class.java)
            startActivity(intent)
        }
        val olvideContraseña = findViewById<TextView>(R.id.olvideBtnInicio1)
        olvideContraseña.setOnClickListener {
            val intent = Intent(this, Reestablecer::class.java)
            startActivity(intent)
        }
    }
}