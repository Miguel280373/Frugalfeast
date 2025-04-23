package com.example.frugalfeast

import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class Registro : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)

        auth = FirebaseAuth.getInstance()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val atraslogin: ImageView = findViewById(R.id.imageView11)
        atraslogin.setOnClickListener {
            finish()
        }

        val checkBoxTerminos = findViewById<CheckBox>(R.id.checkBoxTerminos)
        val textoCompleto = getString(R.string.estoy_de_acuerdo_con_los_terminos_y_condiciones)
        val spannable = SpannableString(textoCompleto)

        val start = textoCompleto.indexOf("términos y condiciones")
        val end = start + "términos y condiciones".length


        spannable.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(this@Registro, TerminosyCondiciones::class.java))
            }
        },  start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        checkBoxTerminos.text = spannable
        checkBoxTerminos.movementMethod = LinkMovementMethod.getInstance()

        val registrarse: Button = findViewById(R.id.registrarse)
        registrarse.setOnClickListener {
            registrarUsuario()
        }
        val textViewIniciar = findViewById<TextView>(R.id.textYaTienesCuenta)
        textViewIniciar.setOnClickListener {
            val intent = Intent(this, IniciarSesion::class.java)
            startActivity(intent)
        }

    }
    private fun registrarUsuario() {
        val email = findViewById<EditText>(R.id.correoCampo).text.toString().trim()
        val contraseña = findViewById<EditText>(R.id.contraseñaCampo).text.toString().trim()
        val nombre = findViewById<EditText>(R.id.nombreCampo).text.toString().trim()
        val confirmarContraseña = findViewById<EditText>(R.id.confirmarContraCampo).text.toString().trim()
        val checkBoxTerminos = findViewById<CheckBox>(R.id.checkBoxTerminos)

        if (nombre.isEmpty() || email.isEmpty() || contraseña.isEmpty() || confirmarContraseña.isEmpty()) {
            Toast.makeText(this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (contraseña != confirmarContraseña) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return
        }

        if (!checkBoxTerminos.isChecked) {
            Toast.makeText(this, "Debes aceptar los términos y condiciones", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, contraseña)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, IniciarSesion::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}