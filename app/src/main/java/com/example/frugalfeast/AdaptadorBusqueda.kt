package com.example.frugalfeast

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class AdaptadorBusqueda(
    private var listaBusqueda: ArrayList<Receta>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<AdaptadorBusqueda.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(receta: Receta)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivRecipeImage: ImageView = itemView.findViewById(R.id.ivRecipeImage)
        val tvRecipeTitle: TextView = itemView.findViewById(R.id.tvRecipeTitle)
        val tvRecipeTiempo: TextView = itemView.findViewById(R.id.tvTiempoReceta)
        val tvRecipeDificultad: TextView = itemView.findViewById(R.id.tvDificultadReceta)
        val tvRecipePorciones: TextView = itemView.findViewById(R.id.tvPorcionesReceta)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_receta, parent, false)
        return ViewHolder(vista)
    }

    private fun obtenerDificultad(dificultad: Int): String {
        return when(dificultad) {
            1 -> "Fácil"
            2 -> "Media"
            3 -> "Difícil"
            else -> "Desconocida"
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listaBusqueda[position]

        Glide.with(holder.itemView.context)
            .load(item.imagenUrl)
            .into(holder.ivRecipeImage)

        holder.tvRecipeTitle.text = item.nombre
        holder.tvRecipeTiempo.text = "${item.tiempo} h"
        holder.tvRecipeDificultad.text = obtenerDificultad(item.dificultad)
        holder.tvRecipePorciones.text = "${item.porciones} porc."

        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(item)
        }
    }

    override fun getItemCount(): Int = listaBusqueda.size

    fun updateList(newList: List<Receta>) {
        listaBusqueda.clear()
        listaBusqueda.addAll(newList)
        notifyDataSetChanged()
    }
}