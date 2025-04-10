package com.example.frugalfeast

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CaloriasResultado : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calorias_resultado)

        val total = intent.getDoubleExtra("totalCalorias", 0.0)
        val tvResultado = findViewById<TextView>(R.id.totalCaloriesTextView)
        tvResultado.text = "Calorías totales: %.2f".format(total)
    }
}