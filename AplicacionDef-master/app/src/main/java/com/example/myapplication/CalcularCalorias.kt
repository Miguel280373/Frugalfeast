package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CalcularCalorias : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_calcular_calorias)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val carnesEditText = findViewById<EditText>(R.id.carnesEditText)
        val verdurasEditText = findViewById<EditText>(R.id.verdurasEditText)
        val frutasEditText = findViewById<EditText>(R.id.frutasEditText)
        val lacteosEditText = findViewById<EditText>(R.id.lacteosEditText)
        val cerealesEditText = findViewById<EditText>(R.id.cerealesEditText)

        val carnesNombreEditText = findViewById<EditText>(R.id.carnesNombreEditText)
        val verdurasNombreEditText = findViewById<EditText>(R.id.verdurasNombreEditText)
        val frutasNombreEditText = findViewById<EditText>(R.id.frutasNombreEditText)
        val lacteosNombreEditText = findViewById<EditText>(R.id.lacteosNombreEditText)
        val cerealesNombreEditText = findViewById<EditText>(R.id.cerealesNombreEditText)

        val calcularButton = findViewById<Button>(R.id.calcularButton)
        val atrasButton = findViewById<ImageButton>(R.id.button9)

        calcularButton.setOnClickListener {
            val carnesCalories = carnesEditText.text.toString().toIntOrNull() ?: 0
            val verdurasCalories = verdurasEditText.text.toString().toIntOrNull() ?: 0
            val frutasCalories = frutasEditText.text.toString().toIntOrNull() ?: 0
            val lacteosCalories = lacteosEditText.text.toString().toIntOrNull() ?: 0
            val cerealesCalories = cerealesEditText.text.toString().toIntOrNull() ?: 0

            val totalCalories = carnesCalories + verdurasCalories + frutasCalories + lacteosCalories + cerealesCalories

            val intent = Intent(this, CaloriasResultados::class.java).apply {
                putExtra("TOTAL_CALORIES", totalCalories)
                putExtra("CARNES_NOMBRE", carnesNombreEditText.text.toString())
                putExtra("VERDURAS_NOMBRE", verdurasNombreEditText.text.toString())
                putExtra("FRUTAS_NOMBRE", frutasNombreEditText.text.toString())
                putExtra("LACTEOS_NOMBRE", lacteosNombreEditText.text.toString())
                putExtra("CEREALES_NOMBRE", cerealesNombreEditText.text.toString())
                putExtra("CARNES_CALORIAS", carnesCalories)
                putExtra("VERDURAS_CALORIAS", verdurasCalories)
                putExtra("FRUTAS_CALORIAS", frutasCalories)
                putExtra("LACTEOS_CALORIAS", lacteosCalories)
                putExtra("CEREALES_CALORIAS", cerealesCalories)
            }

            startActivity(intent)
        }

        atrasButton.setOnClickListener {
            finish()
        }
    }
}
