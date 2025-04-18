package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CaloriasResultados : AppCompatActivity() {
    private var totalCalories = 0

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_calorias_resultados)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        totalCalories = intent.getIntExtra("TOTAL_CALORIES", 0)
        val totalCaloriesTextView = findViewById<TextView>(R.id.totalCaloriesTextView)
        totalCaloriesTextView.text = "$totalCalories Kcal"

        findViewById<TextView>(R.id.carnesTextView).text =
            "Carnes: ${intent.getStringExtra("CARNES_NOMBRE")} - ${intent.getIntExtra("CARNES_CALORIAS", 0)} gr"

        findViewById<TextView>(R.id.verdurasTextView).text =
            "Verduras: ${intent.getStringExtra("VERDURAS_NOMBRE")} - ${intent.getIntExtra("VERDURAS_CALORIAS", 0)} gr"

        findViewById<TextView>(R.id.frutasTextView).text =
            "Frutas: ${intent.getStringExtra("FRUTAS_NOMBRE")} - ${intent.getIntExtra("FRUTAS_CALORIAS", 0)} gr"

        findViewById<TextView>(R.id.lacteosTextView).text =
            "Lácteos: ${intent.getStringExtra("LACTEOS_NOMBRE")} - ${intent.getIntExtra("LACTEOS_CALORIAS", 0)} gr"

        findViewById<TextView>(R.id.cerealesTextView).text =
            "Cereales: ${intent.getStringExtra("CEREALES_NOMBRE")} - ${intent.getIntExtra("CEREALES_CALORIAS", 0)} gr"

        val backButton = findViewById<Button>(R.id.btnAgregarCalorias)
        backButton.setOnClickListener {
            val caloriasDiarias = totalCalories
            val intent = Intent(this, Usuario::class.java).apply {
                putExtra("TOTAL_CALORIES_TODAY", caloriasDiarias)
                putExtra("TOTAL_CALORIES_WEEK", totalCalories * 7)
            }
            startActivity(intent)
        }
        findViewById<ImageButton>(R.id.button11).setOnClickListener {
            onBackPressed()
        }
    }
}