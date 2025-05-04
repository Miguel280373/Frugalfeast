package com.example.frugalfeast

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Busqueda : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_busqueda)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val botonpreia: ImageView = findViewById(R.id.imageView58)
        botonpreia.setOnClickListener {
            Intent(this, PresentacionIA::class.java)
                .also { welcomeIntent ->
                    //Launch
                    startActivity(welcomeIntent)
                    finish()
                }
        }
        val imgVolver = findViewById<ImageView>(R.id.imageView57)

        imgVolver.setOnClickListener {
            finish()
        }

    }
}