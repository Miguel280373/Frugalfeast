
package com.example.frugalfeast

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue



class AgregarReceta: AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance().getReference("recipeImages")
    private val currentUser = FirebaseAuth.getInstance().currentUser

    private lateinit var etNombre: EditText
    private lateinit var etPreparacion: EditText
    private lateinit var etIngredientes: EditText
    private lateinit var btnSubir: Button
    private lateinit var ivRecipe: ImageView
    // agregar lo necesario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_receta)

    }

    private fun subirRecetaAFirestore(nombre: String, preparacion: String, ingredientes: String, imageUri: Uri?) {
        if (imageUri != null) {
            val fileName = "${System.currentTimeMillis()}.jpg"
            val ref = storage.child(fileName)
            ref.putFile(imageUri).addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { uri ->
                    val recetaMap = mapOf(
                        "title" to nombre,
                        "preparation" to preparacion,
                        "ingredients" to ingredientes.split("\n"),
                        "imageUrl" to uri.toString(),
                        "authorId" to currentUser?.uid,
                        "createdAt" to FieldValue.serverTimestamp()
                    )
                    db.collection("recipes").add(recetaMap).addOnSuccessListener { docRef ->
                        currentUser?.let { user ->
                            db.collection("users").document(user.uid)
                                .update("myRecipes", FieldValue.arrayUnion(docRef.id))
                        }
                        finish()
                    }
                }
            }
        } else {
            //agregar error
        }
    }
}
