package com.example.frugalfeast

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class inicio1 : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_inicio1)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()

        verificarAutenticacion()

        val botonContinuar: Button = findViewById(R.id.botonContinuar)
        botonContinuar.setOnClickListener {
            Intent(this, inicio2::class.java).also {
                startActivity(it)
                finish()
            }
        }
    }

    private fun verificarAutenticacion() {
        val currentUser = auth.currentUser

        Handler(Looper.getMainLooper()).postDelayed({
            if (currentUser != null) {
                Intent(this, PantallaPrincipal::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(this)
                }
                finish()
            }
        }, 1500)
    }
}