package com.example.frugalfeast
data class Receta(
    val id: String = "",
    val nombre: String = "",
    val imageUrl: String = "",
    val tiempo: String = "",
    val porciones: String = "",
    val dificultad: String = "",
    val preparacion: String = "",
    val ingredientes: List<String> = emptyList()
)

