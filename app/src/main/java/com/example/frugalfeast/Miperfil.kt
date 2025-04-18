package com.example.frugalfeast

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button

class Miperfil : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_miperfil)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btnIrConfiguracion = findViewById<Button>(R.id.btn_configuracionprivacidad)

        // Configura el OnClickListener para el botón
        btnIrConfiguracion.setOnClickListener {
            // Cuando el botón sea presionado, iniciar la actividad ConfiguracionyPrivacidad
            val intent = Intent(this, ConfiguracionyPrivacidad::class.java)
            startActivity(intent)
        }
    }
}