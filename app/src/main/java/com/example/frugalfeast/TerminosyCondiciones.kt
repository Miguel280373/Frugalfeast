package com.example.frugalfeast

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class TerminosyCondiciones : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_terminosy_condiciones)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val textViewTerminos = findViewById<TextView>(R.id.textView29)
        val btnatras = findViewById<ImageView>(R.id.btn_atras_terminos)

        btnatras.setOnClickListener() {
            finish()
        }

        // Configurar el texto con formato HTML
        textViewTerminos.text = Html.fromHtml(
            getString(R.string.terminos_y_condiciones_completo),
            Html.FROM_HTML_MODE_COMPACT
        )

        val btnVolver = findViewById<ImageButton>(R.id.btn_atras_terminos)

        btnVolver.setOnClickListener {
            finish()


        }
    }
}