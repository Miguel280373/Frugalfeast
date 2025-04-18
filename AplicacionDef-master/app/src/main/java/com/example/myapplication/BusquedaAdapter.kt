package com.example.myapplication

import Receta
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class BusquedaAdapter(
    private val recetas: MutableList<Receta>,
    private val onClick: (Receta, View) -> Unit
) : RecyclerView.Adapter<BusquedaAdapter.BusquedaViewHolder>() {

    inner class BusquedaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val recetaImagenBusqueda: ImageView = view.findViewById(R.id.imagenRecetaChiquito)
        val recetaNombreBusqueda: TextView = view.findViewById(R.id.recetaNombreChiquito)
        val recetaTiempoBusqueda: TextView = view.findViewById(R.id.recetaTiempoChiquito)
        val recetaPorcionesBusqueda: TextView = view.findViewById(R.id.recetaPorcionesChiquito)
        val recetaDificultadBusqueda: TextView = view.findViewById(R.id.recetaDificultadChiquito)

        fun bind(receta: Receta) {
            recetaNombreBusqueda.text = receta.nombreReceta
            recetaTiempoBusqueda.text = receta.tiempoPreparacion.toString()
            recetaPorcionesBusqueda.text = receta.porciones.toString()
            recetaDificultadBusqueda.text = receta.dificultad

            Glide.with(recetaImagenBusqueda.context).load(receta.imagenUrl).into(recetaImagenBusqueda)

            itemView.setOnClickListener { onClick(receta, itemView) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusquedaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_receta_chiquito, parent, false)
        return BusquedaViewHolder(view)
    }

    override fun onBindViewHolder(holder: BusquedaViewHolder, position: Int) {
        holder.bind(recetas[position])
    }

    override fun getItemCount(): Int = recetas.size
}
