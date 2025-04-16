package com.example.frugalfeast

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class MisRecetasAdapter(
    private val listaRecetas: List<Receta>,
    private val onItemClick: (Receta) -> Unit) : RecyclerView.Adapter<MisRecetasAdapter.RecetaViewHolder>() {

    class RecetaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imagenReceta: ImageView = itemView.findViewById(R.id.ivRecipeImage)
        val tituloReceta: TextView = itemView.findViewById(R.id.tvRecipeTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecetaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_receta, parent, false)
        return RecetaViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecetaViewHolder, position: Int) {
        val receta = listaRecetas[position]
        holder.tituloReceta.text = receta.nombre

        Glide.with(holder.itemView.context).load(receta.imagenUrl).into(holder.imagenReceta)

        holder.itemView.setOnClickListener {
            onItemClick(receta)
        }
    }

    override fun getItemCount(): Int = listaRecetas.size
}
