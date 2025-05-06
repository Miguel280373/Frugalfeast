    package com.example.frugalfeast

    import android.content.Context
    import android.content.Intent
    import android.os.Bundle
    import android.widget.ImageButton
    import android.widget.ImageView
    import android.widget.Toast
    import androidx.activity.enableEdgeToEdge
    import androidx.appcompat.app.AlertDialog
    import androidx.appcompat.app.AppCompatActivity
    import androidx.core.content.edit
    import androidx.core.view.ViewCompat
    import androidx.core.view.WindowInsetsCompat
    import androidx.recyclerview.widget.LinearLayoutManager
    import androidx.recyclerview.widget.RecyclerView
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.firestore.FirebaseFirestore
    import com.google.firebase.firestore.toObject

    class RecetasGuardadas : AppCompatActivity() {
        private val db = FirebaseFirestore.getInstance()
        private lateinit var recyclerGuardadas: RecyclerView
        private lateinit var adapterGuardadas: RecetasGuardadasAdapter
        private val recetasGuardadas = mutableListOf<Receta>()

        // Variables para modo selección
        private var modoSeleccion: Boolean = false
        private var tipoComida: String? = null

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            enableEdgeToEdge()
            setContentView(R.layout.activity_recetas_guardadas)

            // Verificar si estamos en modo selección
            modoSeleccion = intent.getBooleanExtra("MODO_SELECCION", false)
            tipoComida = intent.getStringExtra("TIPO_COMIDA")

            recyclerGuardadas = findViewById(R.id.recyclerRecetasGuardadas)
            recyclerGuardadas.layoutManager = LinearLayoutManager(this)

            configurarAdaptador()

            cargarRecetasGuardadas()

            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }

            val imgVolver = findViewById<ImageButton>(R.id.btn_atras_guardadas)
            imgVolver.setOnClickListener { finish() }
        }

        private fun configurarAdaptador() {
            adapterGuardadas = RecetasGuardadasAdapter(
                mode = if (modoSeleccion) RecetaMode.SELECT_FOR_MENU else RecetaMode.VIEW_DETAILS,
                recetas = recetasGuardadas,
                onItemClick = { receta -> abrirDetallesReceta(receta) },
                onSelectForMenu = { receta -> mostrarDialogoConfirmacion(receta) }
            )

            recyclerGuardadas.adapter = adapterGuardadas
        }

        private fun cargarRecetasGuardadas() {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

            db.collection("favoritos").document(userId).collection("recetas_favoritas")
                .get()
                .addOnSuccessListener { documents ->
                    recetasGuardadas.clear()
                    for (document in documents) {
                        val receta = document.toObject<Receta>().apply {
                            id = document.id
                        }
                        recetasGuardadas.add(receta)
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al cargar las recetas: $e", Toast.LENGTH_SHORT).show()
                }
        }

        private fun abrirDetallesReceta(receta: Receta) {
            val intent = Intent(this, PantallaPrincipalReceta::class.java).apply {
                putExtra("receta_id", receta.id)
                putExtra("receta_nombre", receta.nombre)
                putExtra("receta_imagen", receta.imagenUrl)
                // Agregar más datos según necesites
            }
            startActivity(intent)
        }

        private fun mostrarDialogoConfirmacion(receta: Receta) {
            AlertDialog.Builder(this)
                .setTitle("Agregar al menú")
                .setMessage("¿Deseas agregar '${receta.nombre}' a tu menú de $tipoComida?")
                .setPositiveButton("Sí") { _, _ ->
                    guardarRecetaEnMenu(receta)
                    Toast.makeText(this, "Receta agregada al menú", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        private fun guardarRecetaEnMenu(receta: Receta) {
            val sharedPref = getSharedPreferences("menu_del_dia", Context.MODE_PRIVATE)
            sharedPref.edit {
                putString("${tipoComida}_id", receta.id)
                apply()
            }

            setResult(RESULT_OK, Intent().apply {
                putExtra("RECETA_ID", receta.id)
                putExtra("TIPO_COMIDA", tipoComida)
            })
        }
    }