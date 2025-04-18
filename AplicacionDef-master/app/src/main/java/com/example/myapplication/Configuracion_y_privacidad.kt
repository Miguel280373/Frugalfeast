package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Configuracion_y_privacidad : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_configuracion_yprivacidad)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        findViewById<ImageButton>(R.id.button18).setOnClickListener {
            onBackPressed() // Cierra la actividad actual y vuelve a la anterior
        }
        val botonterm = findViewById<Button>(R.id.button4)
        botonterm.setOnClickListener {
            val intent = Intent(this, Terminos_y_condiciones::class.java)
            startActivity(intent)
        }
        val botonidi = findViewById<Button>(R.id.button10)
        botonidi.setOnClickListener {
            val intent = Intent(this, Idioma::class.java)
            startActivity(intent)
        }
        val botonso = findViewById<Button>(R.id.button15)
        botonso.setOnClickListener {
            val intent = Intent(this, SobreApp::class.java)
            startActivity(intent)
        }
        val botoncon = findViewById<Button>(R.id.button16)
        botoncon.setOnClickListener {
            val intent = Intent(this, Contactos::class.java)
            startActivity(intent)
        }
        val botonel = findViewById<Button>(R.id.button17)
        botonel.setOnClickListener {
            val intent = Intent(this, Eliminar_Cuenta::class.java)
            startActivity(intent)
        }
    }
}