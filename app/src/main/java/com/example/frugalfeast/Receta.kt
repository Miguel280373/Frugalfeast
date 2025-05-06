package com.example.frugalfeast
data class Receta(
    var id: String = "",
    val nombre: String = "",
    val imagenUrl: String = "",
    val tiempo: String = "",
    val porciones: String = "",
    val dificultad: String = "",
    val preparacion: String = "",
    val ingredientes: List<String> = emptyList()
)

