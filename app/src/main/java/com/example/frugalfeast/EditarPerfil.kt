package com.example.frugalfeast

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class EditarPerfil : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_perfil)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val imgVolver = findViewById<ImageView>(R.id.imageView24)
        val btnGuardar = findViewById<Button>(R.id.button2)
        val editNombre = findViewById<EditText>(R.id.editTextText)

        imgVolver.setOnClickListener {
            finish() // Regresa a la actividad anterior
        }

        btnGuardar.setOnClickListener {
            val nuevoNombre = editNombre.text.toString()

            val sharedPref = getSharedPreferences("PerfilUsuario", MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putString("nombre", nuevoNombre)
            editor.apply()

            finish()
        }
    }
}
