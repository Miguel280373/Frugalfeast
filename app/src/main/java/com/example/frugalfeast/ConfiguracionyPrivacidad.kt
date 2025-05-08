package com.example.frugalfeast

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ConfiguracionyPrivacidad : AppCompatActivity() {
    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_configuraciony_privacidad)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btnIrpo = findViewById<Button>(R.id.button3)

        btnIrpo.setOnClickListener {
            val intent = Intent(this, Politicadeprivacidad::class.java)
            startActivity(intent)
        }
        val btnIrter = findViewById<Button>(R.id.button4)

        btnIrter.setOnClickListener {
            val intent = Intent(this, TerminosyCondiciones::class.java)
            startActivity(intent)
        }
        val btnIrsob = findViewById<Button>(R.id.button15)

        btnIrsob.setOnClickListener {
            val intent = Intent(this, SobreApp::class.java)
            startActivity(intent)
        }
        val btnIrcon = findViewById<Button>(R.id.button15)

        btnIrcon.setOnClickListener {
            val intent = Intent(this, Contactanos::class.java)
            startActivity(intent)
        }

        val btnVolver = findViewById<ImageButton>(R.id.button18)

        btnVolver.setOnClickListener {
            finish()
        }

        val botonEditar = findViewById<Button>(R.id.button19)
        botonEditar.setOnClickListener {
            val intent = Intent(this, EditarPerfil::class.java)
            startActivity(intent)
        }
    }
}