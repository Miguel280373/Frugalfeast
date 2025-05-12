package com.example.frugalfeast

import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
//import android.content.SharedPreferences
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

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
        val imgVolver = findViewById<ImageView>(R.id.imageView47)
        imgVolver.setOnClickListener {
            finish()
        }
        val miImageView = findViewById<ImageView>(R.id.imageView49)
        miImageView.setOnClickListener {
            val intent = Intent(this, ConfiguracionyPrivacidad::class.java)
            startActivity(intent)
        }
        val textoIrACalorias = findViewById<TextView>(R.id.textoIrACalorias)
        textoIrACalorias.setOnClickListener {
            val intent = Intent(this, CalcularCalorias::class.java)
            startActivity(intent)
        }

        val imageViewPerfil = findViewById<ImageView>(R.id.imageView48)
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        //imagen
        if (uid != null) {
            val db = FirebaseFirestore.getInstance()
            db.collection("usuarios").document(uid).get()
                .addOnSuccessListener { document ->
                    val fotoUrl = document.getString("fotoPerfil")
                    if (!fotoUrl.isNullOrEmpty()) {
                        Glide.with(this)
                            .load(fotoUrl)
                            .into(imageViewPerfil)
                    }
                }
        }

        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        //val uid = auth.currentUser?.uid
        if (uid != null) {
            db.collection("usuarios").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val nombre = document.getString("nombre")
                        val apodo = document.getString("apodo")
                        val correo = document.getString("email")

                        // Mostrar en tu UI
                        findViewById<TextView>(R.id.textView66).text = apodo

                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al cargar perfil", Toast.LENGTH_SHORT).show()
                }
        }

        //mis recetas
        val textViewMisRecetas = findViewById<TextView>(R.id.textView67)
        val imageViewMisRecetas = findViewById<ImageView>(R.id.imageView52)

        val irAMisRecetas = Intent(this, MisRecetas::class.java)

        textViewMisRecetas.setOnClickListener {
            startActivity(irAMisRecetas)
        }

        imageViewMisRecetas.setOnClickListener {
            startActivity(irAMisRecetas)
        }

        //Guardados
        val textViewGuardados = findViewById<TextView>(R.id.textView68)
        val imageViewGuardados = findViewById<ImageView>(R.id.imageView53)

        val irAGuardados = Intent(this, RecetasGuardadas::class.java)

        textViewGuardados.setOnClickListener {
            startActivity(irAGuardados)
        }

        imageViewGuardados.setOnClickListener {
            startActivity(irAGuardados)
        }
        //calorias
        fun obtenerCaloriasSemanales() {
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
            val db = FirebaseFirestore.getInstance()
            val formatoFecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val hoy = Calendar.getInstance()
            val fechaHoyStr = formatoFecha.format(hoy.time)
            val hace7Dias = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -6) }
            val tvCaloriasHoy = findViewById<TextView>(R.id.textView73)
            val tvCaloriasSemana = findViewById<TextView>(R.id.textView74)

            db.collection("usuarios").document(uid).collection("calorias")
                .get()
                .addOnSuccessListener { snapshot ->
                    var totalSemana = 0.0
                    var hayDatosSemana = false
                    var caloriasHoy = -1.0
                    Log.d("FirebaseDebug", "Hoy: $fechaHoyStr")
                    for (document in snapshot.documents) {
                        val fecha = document.getString("fecha")
                        val calorias = document.getDouble("totalCalorias") ?: 0.0

                        if (fecha != null) {
                            val fechaParsed = formatoFecha.parse(fecha)
                            if (fechaParsed != null) {
                                // Calorías de hoy
                                if (fecha == fechaHoyStr) {
                                    caloriasHoy = calorias
                                }
                                // Calorías semana
                                if (fechaParsed >= hace7Dias.time && fechaParsed <= hoy.time) {
                                    totalSemana += calorias
                                    hayDatosSemana = true
                                }
                            }
                        }
                        Log.d("FirebaseDebug", "Fecha: $fecha, Calorías: $calorias")
                    }

                    // Mostrar calorías de hoy
                    if (caloriasHoy >= 0) {
                        tvCaloriasHoy.text = "${caloriasHoy.toInt()} kcal"
                    } else {
                        tvCaloriasHoy.text = "No has ingresado tus calorías hoy"
                    }

                    // Mostrar calorías semanales
                    if (hayDatosSemana) {
                        tvCaloriasSemana.text = "Calorías esta semana: ${"%.0f".format(totalSemana)}"
                    } else {
                        tvCaloriasSemana.text = "Faltan días 😠"
                    }

                }
                .addOnFailureListener {
                    tvCaloriasHoy.text = "Error al cargar"
                    tvCaloriasSemana.text = "Error al cargar"
                }

        }
        obtenerCaloriasSemanales()

    }

    override fun onResume() {
        super.onResume()
        val prefs = getSharedPreferences("frugalfeast_prefs", Context.MODE_PRIVATE)
        val cantidadGuardadas = prefs.getInt("contador_guardadas", 0)

        val contadorTextView = findViewById<TextView>(R.id.contadorGuardadas)
        contadorTextView.text = "$cantidadGuardadas"

        val creadas = prefs.getInt("contador_creadas", 0)

        val circulo = findViewById<TextView>(R.id.circulo_RecetasCreadas)
        circulo.text = creadas.toString()
        circulo.visibility = if (creadas > 0) View.VISIBLE else View.GONE
    }
}