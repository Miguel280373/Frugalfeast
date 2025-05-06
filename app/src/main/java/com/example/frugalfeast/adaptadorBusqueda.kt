package com.example.frugalfeast

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class adaptadorBusqueda(
    private var listaBusqueda: ArrayList<BarraBusqueda>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<adaptadorBusqueda.RecetaViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(receta: BarraBusqueda)
    }

    inner class RecetaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivRecipeImage: ImageView = itemView.findViewById(R.id.ivRecipeImage)
        val tvRecipeTitle: TextView = itemView.findViewById(R.id.tvRecipeTitle)
        val tvTiempoReceta: TextView = itemView.findViewById(R.id.tvTiempoReceta)
        val tvPorcionesReceta: TextView = itemView.findViewById(R.id.tvPorcionesReceta)
        val tvDificultadReceta: TextView = itemView.findViewById(R.id.tvDificultadReceta)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(listaBusqueda[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecetaViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_receta, parent, false)
        return RecetaViewHolder(vista)
    }

    override fun onBindViewHolder(holder: RecetaViewHolder, position: Int) {
        val receta = listaBusqueda[position]

        Glide.with(holder.itemView.context)
            .load(receta.imagenUrl)
            .placeholder(R.drawable.baseline_image_24)
            .error(R.drawable.baseline_image_24)
            .transition(DrawableTransitionOptions.withCrossFade())
            .centerCrop()
            .into(holder.ivRecipeImage)

        // Configurar textos
        holder.tvRecipeTitle.text = receta.nombre
        holder.tvTiempoReceta.text = receta.tiempo
        holder.tvPorcionesReceta.text = receta.porciones
        holder.tvDificultadReceta.text = obtenerDificultad(receta.dificultad)
    }

    override fun getItemCount(): Int = listaBusqueda.size

    fun actualizarLista(newList: List<BarraBusqueda>) {
        listaBusqueda = ArrayList(newList)
        notifyDataSetChanged()
    }
    private fun obtenerDificultad(dificultad: String): String {
        return when(dificultad.toInt()) {
            1 -> "Fácil"
            2 -> "Media"
            3 -> "Difícil"
            else -> ""
        }
    }
}
