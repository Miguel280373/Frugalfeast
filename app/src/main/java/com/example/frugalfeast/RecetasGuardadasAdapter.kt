package com.example.frugalfeast

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide



class RecetasGuardadasAdapter(
    private val mode: RecetaMode,
    private val recetas: List<Receta>,
    private val onItemClick: (Receta) -> Unit = {},
    private val onSelectForMenu: (Receta) -> Unit = {}
) : RecyclerView.Adapter<RecetasGuardadasAdapter.RecetaViewHolder>() {

    class RecetaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivImagen: ImageView = view.findViewById(R.id.ivRecipeImage)
        val tvTitulo: TextView = view.findViewById(R.id.tvRecipeTitle)
        val tvTiempo: TextView = view.findViewById(R.id.tvTiempoReceta)
        val tvPorciones: TextView = view.findViewById(R.id.tvPorcionesReceta)
        val tvDificultad: TextView = view.findViewById(R.id.tvDificultadReceta)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecetaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_receta, parent, false)
        return RecetaViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecetaViewHolder, position: Int) {
        val receta = recetas[position]
        holder.tvTitulo.text = receta.nombre
        holder.tvTiempo.text = "${receta.tiempo} h."
        holder.tvPorciones.text = "${receta.porciones} porc."
        holder.tvDificultad.text = obtenerDificultad(receta.dificultad.toString())

        Glide.with(holder.itemView.context)
            .load(receta.imagenUrl)
            .placeholder(R.drawable.icono_imagen)
            .error(R.drawable.icono_imagen)
            .into(holder.ivImagen)
        holder.itemView.setOnClickListener {
            when(mode) {
                RecetaMode.VIEW_DETAILS -> onItemClick(receta)
                RecetaMode.SELECT_FOR_MENU -> onSelectForMenu(receta)
            }
        }
    }
    private fun obtenerDificultad(dificultad: String): String {
        return when(dificultad.toInt()) {
            1 -> "Fácil"
            2 -> "Media"
            3 -> "Difícil"
            else -> ""
        }
    }

    override fun getItemCount(): Int = recetas.size
}
