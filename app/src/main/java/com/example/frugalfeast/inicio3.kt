package com.example.frugalfeast

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class inicio3 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_inicio3)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val botonContinuar: Button = findViewById(R.id.botonContinuar3)
        botonContinuar.setOnClickListener {
            Intent(this, Login::class.java)
                .also { welcomeIntent ->
                    //Launch
                    startActivity(welcomeIntent)
                    finish()
                }
        }

        val btnatrasdos: ImageView = findViewById(R.id.imageView10)
        btnatrasdos.setOnClickListener {
            val intent = Intent(this, inicio2::class.java)
            startActivity(intent)
        }
    }
}