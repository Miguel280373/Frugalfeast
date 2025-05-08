package com.example.frugalfeast

import java.util.Date


data class Receta(
    var id: String = "",
    val nombre: String = "",
    val imagenUrl: String = "",
    val tiempo: Int = 0,
    val porciones: Int = 0,
    val dificultad: Int = 1,
    val calorias: Int = 0,
    val preparacion: String = "",
    var fechaCreacion: Date? = null,
    val userId: String = "",
    val ingredientes: List<String> = emptyList()
)


