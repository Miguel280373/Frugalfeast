package com.example.frugalfeast

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CaloriasResultado : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calorias_resultado)

        val totalCalorias = intent.getDoubleExtra("totalCalorias", 0.0)
        val carnes = intent.getStringArrayListExtra("carnes") ?: arrayListOf()
        val frutas = intent.getStringArrayListExtra("frutas") ?: arrayListOf()
        val verduras = intent.getStringArrayListExtra("verduras") ?: arrayListOf()
        val cereales = intent.getStringArrayListExtra("cereales") ?: arrayListOf()

        val tvResultado = findViewById<TextView>(R.id.totalCaloriesTextView)
        tvResultado.text = String.format("%.0fkcal", totalCalorias)

        val carnesLayout = findViewById<LinearLayout>(R.id.carnesLayout)
        val frutasLayout = findViewById<LinearLayout>(R.id.frutasLayout)
        val verdurasLayout = findViewById<LinearLayout>(R.id.verdurasLayout)
        val cerealesLayout = findViewById<LinearLayout>(R.id.cerealesLayout)

        mostrarAlimentos(carnes, carnesLayout)
        mostrarAlimentos(frutas, frutasLayout)
        mostrarAlimentos(verduras, verdurasLayout)
        mostrarAlimentos(cereales, cerealesLayout)

        val imgVolver = findViewById<ImageView>(R.id.btn_atras_resultado)
        imgVolver.setOnClickListener {
            finish()
        }
    }

    private fun mostrarAlimentos(alimentos: List<String>, layout: LinearLayout) {
        alimentos.forEach { alimento ->
            val alimentoInfo = alimento.split(",")
            if (alimentoInfo.size == 3) {
                val nombre = alimentoInfo[0]
                val cantidad = alimentoInfo[1]
                val calorias = alimentoInfo[2]

                val alimentoTextView = TextView(this)
                alimentoTextView.text = String.format("%s\t\t%sg\t\t%skc", nombre, cantidad, calorias)
                layout.addView(alimentoTextView)
            }
        }
    }
}