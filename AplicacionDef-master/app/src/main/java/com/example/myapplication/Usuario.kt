package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Usuario : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var nombreUsuario: TextView
    private lateinit var containerMisRecetas: LinearLayout
    private lateinit var containerRecetasGuardadas: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_usuario)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        nombreUsuario = findViewById(R.id.usuario)
        containerMisRecetas = findViewById(R.id.containerMisRecetas)
        containerRecetasGuardadas = findViewById(R.id.containerGuardadas)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        obtenerNombreUsuario()

        val totalCaloriesToday = intent.getIntExtra("TOTAL_CALORIES_TODAY", 0)
        val totalCaloriesWeek = intent.getIntExtra("TOTAL_CALORIES_WEEK", 0)

        findViewById<TextView>(R.id.caloriasDiariasTextView).text = "$totalCaloriesToday kcal"
        findViewById<TextView>(R.id.caloriasSemanalesTextView).text = "$totalCaloriesWeek kcal"

        val bottom_bar = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottom_bar.selectedItemId = R.id.nav_usuario
        bottom_bar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_usuario -> {
                    true
                }
                R.id.nav_inicio -> {
                    startActivity(Intent(this, Recetas::class.java))
                    finish()
                    true
                }
                R.id.nav_recetas_guardadas -> {
                    startActivity(Intent(this, RecetasGuardadas::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
        val botonConf = findViewById<ImageButton>(R.id.imageView19)
        botonConf.setOnClickListener {

            val intent = Intent(this, Configuracion_y_privacidad::class.java)
            startActivity(intent)

        }

        val botonAtras = findViewById<ImageButton>(R.id.button5)
        botonAtras.setOnClickListener {
            finish()
        }

        containerRecetasGuardadas.setOnClickListener{
            startActivity(Intent(this, RecetasGuardadas::class.java))
        }

        containerMisRecetas.setOnClickListener{
            startActivity(Intent(this, MisRecetas::class.java))
        }
    }
    private fun obtenerNombreUsuario() {
        val usuarioId = auth.currentUser?.uid
        val usuario = findViewById<TextView>(R.id.usuario)
        if (usuarioId != null) {
            db.collection("usuarios").document(usuarioId).get()
                .addOnSuccessListener { document ->
                    val apodo = document.getString("apodo")
                    val nombre = document.getString("nombre")
                    usuario.text = apodo ?: nombre ?: "Usuario"
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al obtener nombre", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
