package com.example.frugalfeast

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RecetasGuardadasAdapter(
    private val recetas: List<Receta>
) : RecyclerView.Adapter<RecetasGuardadasAdapter.RecetaViewHolder>() {

    class RecetaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivImagen: ImageView = view.findViewById(R.id.ivRecipeImage)
        val tvTitulo: TextView = view.findViewById(R.id.tvRecipeTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecetaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_receta, parent, false)
        return RecetaViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecetaViewHolder, position: Int) {
        val receta = recetas[position]
        holder.tvTitulo.text = receta.nombre

        Glide.with(holder.itemView.context)
            .load(receta.imageUrl)
            .placeholder(R.drawable.icono_imagen)
            .error(R.drawable.icono_imagen)
            .into(holder.ivImagen)
    }

    override fun getItemCount(): Int = recetas.size
}
