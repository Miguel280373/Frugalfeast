package com.example.frugalfeast

import java.util.Date

data class Receta(
    var id: String = "",
    val nombre: String = "",
    val imagenUrl: String = "",
    val tiempo: Long = 0,
    val porciones: Long = 0,
    val dificultad: Long = 0,
    val preparacion: String = "",
    val userId: String = "",
    var fechaCreacion: Date? = null,
    val ingredientes: List<String> = emptyList()
)


