package com.example.frugalfeast

import java.util.Date

data class Receta(
    var id: String = "",
    val nombre: String = "",
    val imagenUrl: String = "",
    val tiempo: String = "",
    val porciones: String = "",
    val dificultad: String = "",
    val preparacion: String = "",
    val userId: String = "",
    val fechaCreacion: Date? = null,
    val ingredientes: List<String> = emptyList()
)


