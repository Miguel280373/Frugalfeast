package com.example.frugalfeast

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class RecetaAdapter(
    private var recetas: List<Receta>,
    private val onItemClicked: (Receta) -> Unit
) : RecyclerView.Adapter<RecetaAdapter.RecetaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecetaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_receta, parent, false)
        return RecetaViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecetaViewHolder, position: Int) {
        holder.bind(recetas[position])
    }

    override fun getItemCount(): Int = recetas.size

    fun updateData(newList: List<Receta>) {
        recetas = newList
    }

    inner class RecetaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle = itemView.findViewById<TextView>(R.id.tvRecipeTitle)
        private val ivImage = itemView.findViewById<ImageView>(R.id.ivRecipeImage)
        fun bind(receta: Receta) {
            tvTitle.text = receta.nombre
            // Cargar imagen con Glide
            if (receta.imageUrl.isNotEmpty()) {
                Glide.with(itemView.context)
                    .load(receta.imageUrl)
                    .into(ivImage)
            }

            itemView.setOnClickListener {
                onItemClicked(receta)
            }
        }
    }
}
