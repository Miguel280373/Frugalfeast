package com.example.frugalfeast

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class PresentacionIA : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_presentacion_ia)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val botoninteria: Button = findViewById(R.id.buttonpreia)
        botoninteria.setOnClickListener {
            val intent = Intent(this, InteraccionIA::class.java)
            startActivity(intent)
            finish()
        }
        val imgVolver = findViewById<ImageView>(R.id.imageView17)

        imgVolver.setOnClickListener {
            finish()
        }

    }
}
