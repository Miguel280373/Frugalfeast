package com.example.frugalfeast

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

        val spinnerCereales = findViewById<Spinner>(R.id.spinnerCereales)
        val etCerealesCantidad = findViewById<EditText>(R.id.etCerealesCantidad)

        val spinnerLacteos = findViewById<Spinner>(R.id.spinnerLacteos)
        val etLacteosCantidad = findViewById<EditText>(R.id.etLacteosCantidad)

        val carnesList = listOf(
            Alimento("Pollo", 239),
            Alimento("Res", 250),
            Alimento("Pescado", 200)
        )
        val verdurasList = listOf(
            Alimento("Cebolla", 40),
            Alimento("Lechuga", 15),
            Alimento("Tomate", 18)
        )
        val frutasList = listOf(
            Alimento("Manzana", 52),
            Alimento("Banana", 89),
            Alimento("Naranja", 47)
        )
        val cerealesList = listOf(
            Alimento("Arroz", 130),
            Alimento("Trigo", 340),
            Alimento("Avena", 389)
        )
        val lacteosList = listOf(
            Alimento("Queso", 400),
            Alimento("Yogurt", 70),
            Alimento("Leche", 60)
        )

        spinnerCarnes.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, carnesList.map { it.nombre }).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinnerVerduras.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, verdurasList.map { it.nombre }).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinnerFrutas.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, frutasList.map { it.nombre }).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinnerCereales.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cerealesList.map { it.nombre }).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinnerLacteos.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, lacteosList.map { it.nombre }).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        val btnCalcular = findViewById<Button>(R.id.btnCalcular)
        btnCalcular.setOnClickListener {
            var countFilled = 0
            var totalCalorias = 0.0
            val alimentosSeleccionadosCarnes = mutableListOf<String>()
            val alimentosSeleccionadosFrutas = mutableListOf<String>()
            val alimentosSeleccionadosVerduras = mutableListOf<String>()
            val alimentosSeleccionadosCereales = mutableListOf<String>()
            val alimentosSeleccionadosLacteos = mutableListOf<String>()

            fun procesarCategoria(etCantidad: EditText, spinner: Spinner, lista: List<Alimento>, listaSeleccionados: MutableList<String>) {
                val cantidad = etCantidad.text.toString().toDoubleOrNull()
                if (cantidad != null && cantidad > 0) {
                    countFilled++
                    val alimentoSeleccionado = lista[spinner.selectedItemPosition]
                    val calorias = alimentoSeleccionado.caloriasPor100g * cantidad / 100.0
                    totalCalorias += calorias
                    listaSeleccionados.add("${alimentoSeleccionado.nombre},${cantidad.toInt()},${calorias.toInt()}")
                }
            }

            procesarCategoria(etCarnesCantidad, spinnerCarnes, carnesList, alimentosSeleccionadosCarnes)
            procesarCategoria(etVerdurasCantidad, spinnerVerduras, verdurasList, alimentosSeleccionadosVerduras)
            procesarCategoria(etFrutasCantidad, spinnerFrutas, frutasList, alimentosSeleccionadosFrutas)
            procesarCategoria(etCerealesCantidad, spinnerCereales, cerealesList, alimentosSeleccionadosCereales)
            procesarCategoria(etLacteosCantidad, spinnerLacteos, lacteosList, alimentosSeleccionadosLacteos) // Process lacteos

            if (countFilled < 1) {
                Toast.makeText(this, "Por favor, ingresa cantidad en al menos una categoría", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, CaloriasResultado::class.java)
            intent.putExtra("totalCalorias", totalCalorias)
            intent.putStringArrayListExtra("carnes", ArrayList(alimentosSeleccionadosCarnes))
            intent.putStringArrayListExtra("frutas", ArrayList(alimentosSeleccionadosFrutas))
            intent.putStringArrayListExtra("verduras", ArrayList(alimentosSeleccionadosVerduras))
            intent.putStringArrayListExtra("cereales", ArrayList(alimentosSeleccionadosCereales))
            intent.putStringArrayListExtra("lacteos", ArrayList(alimentosSeleccionadosLacteos)) // Pass lacteos
            startActivity(intent)

            val uid = FirebaseAuth.getInstance().currentUser?.uid
            val fechaActual = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            if (uid != null) {
                val db = FirebaseFirestore.getInstance()
                val caloriasData = hashMapOf(
                    "fecha" to fechaActual,
                    "totalCalorias" to totalCalorias
                )

                db.collection("usuarios")
                    .document(uid)
                    .collection("calorias")
                    .document(fechaActual)
                    .set(caloriasData)
                    .addOnSuccessListener {
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error al guardar calorías", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        val imgVolver = findViewById<ImageView>(R.id.btn_atras_calorias)
        imgVolver.setOnClickListener {
            finish()
        }
    }
}