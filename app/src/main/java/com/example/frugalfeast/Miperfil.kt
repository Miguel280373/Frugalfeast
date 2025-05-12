package com.example.frugalfeast

import android.content.Context
import android.content.Intent
import android.os.Bundle
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

class Miperfil : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_miperfil)
        /*val sharedPref = getSharedPreferences("PerfilUsuario", MODE_PRIVATE)
        val nombreGuardado = sharedPref.getString("nombre", "Nombre por defecto")

        val textViewNombre = findViewById<TextView>(R.id.textView66)
        textViewNombre.text = nombreGuardado*/

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