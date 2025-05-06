package com.example.frugalfeast

data class BarraBusqueda(
    var nombre: String = "",
    var imagenUrl: String = "",
    var id: String = "",
    val tiempo: String = "",
    val porciones: String = "",
    val dificultad: String = ""
){
    constructor() : this("", "", "", "", "","") // Constructor vacío requerido por Firebase
}