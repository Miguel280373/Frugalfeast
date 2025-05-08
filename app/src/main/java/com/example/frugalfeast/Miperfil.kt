package com.example.frugalfeast

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.SharedPreferences
import android.widget.TextView

class Miperfil : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_miperfil)
        val sharedPref = getSharedPreferences("PerfilUsuario", MODE_PRIVATE)
        val nombreGuardado = sharedPref.getString("nombre", "Nombre por defecto")

        val textViewNombre = findViewById<TextView>(R.id.textView66)
        textViewNombre.text = nombreGuardado

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val imgVolver = findViewById<ImageView>(R.id.imageView47)

        imgVolver.setOnClickListener {
            finish()
        }
        val miImageView = findViewById<ImageView>(R.id.imageView49)

        miImageView.setOnClickListener {
            val intent = Intent(this, ConfiguracionyPrivacidad::class.java)
            startActivity(intent)
        }

    }
}