package com.example.frugalfeast

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity



class CalcularCalorias : AppCompatActivity() {

    data class Alimento(val nombre: String, val caloriasPor100g: Int)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calcular_calorias)

        val spinnerCarnes = findViewById<Spinner>(R.id.spinnerCarnes)
        val etCarnesCantidad = findViewById<EditText>(R.id.etCarnesCantidad)

        val spinnerVerduras = findViewById<Spinner>(R.id.spinnerVerduras)
        val etVerdurasCantidad = findViewById<EditText>(R.id.etVerdurasCantidad)

        val spinnerFrutas = findViewById<Spinner>(R.id.spinnerFrutas)
        val etFrutasCantidad = findViewById<EditText>(R.id.etFrutasCantidad)

        val spinnerLacteos = findViewById<Spinner>(R.id.spinnerLacteos)
        val etLacteosCantidad = findViewById<EditText>(R.id.etLacteosCantidad)

        val spinnerCereales = findViewById<Spinner>(R.id.spinnerCereales)
        val etCerealesCantidad = findViewById<EditText>(R.id.etCerealesCantidad)

        // Datos de los alimentos por categoría
        val carnesList = listOf(
            Alimento("Res", 250),
            Alimento("Pollo", 239),
            Alimento("Pescado", 200)
        )
        val verdurasList = listOf(
            Alimento("Lechuga", 15),
            Alimento("Tomate", 18),
            Alimento("Cebolla", 40)
        )
        val frutasList = listOf(
            Alimento("Manzana", 52),
            Alimento("Banana", 89),
            Alimento("Naranja", 47)
        )
        val lacteosList = listOf(
            Alimento("Leche", 60),
            Alimento("Queso", 350),
            Alimento("Yogur", 59)
        )
        val cerealesList = listOf(
            Alimento("Arroz", 130),
            Alimento("Trigo", 340),
            Alimento("Avena", 389)
        )

        // Configurar adaptadores para cada Spinner
        spinnerCarnes.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, carnesList.map { it.nombre }).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinnerVerduras.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, verdurasList.map { it.nombre }).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinnerFrutas.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, frutasList.map { it.nombre }).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinnerLacteos.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, lacteosList.map { it.nombre }).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinnerCereales.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cerealesList.map { it.nombre }).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        // Botón para calcular
        val btnCalcular = findViewById<Button>(R.id.btnCalcular)
        btnCalcular.setOnClickListener {
            var countFilled = 0
            var totalCalorias = 0.0

            // Función auxiliar para calcular y sumar las calorías, dado el EditText, Spinner y lista
            fun procesarCategoria(etCantidad: EditText, spinner: Spinner, lista: List<Alimento>) {
                val cantidad = etCantidad.text.toString().toDoubleOrNull()
                if (cantidad != null && cantidad > 0) {
                    countFilled++
                    val alimentoSeleccionado = lista[spinner.selectedItemPosition]
                    totalCalorias += alimentoSeleccionado.caloriasPor100g * cantidad / 100.0
                }
            }

            procesarCategoria(etCarnesCantidad, spinnerCarnes, carnesList)
            procesarCategoria(etVerdurasCantidad, spinnerVerduras, verdurasList)
            procesarCategoria(etFrutasCantidad, spinnerFrutas, frutasList)
            procesarCategoria(etLacteosCantidad, spinnerLacteos, lacteosList)
            procesarCategoria(etCerealesCantidad, spinnerCereales, cerealesList)

            if (countFilled < 2) {
                Toast.makeText(this, "Por favor, ingresa cantidad en al menos dos categorías", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, CaloriasResultado::class.java)
            intent.putExtra("totalCalorias", totalCalorias)
            startActivity(intent)
        }
    }
}
