package com.example.frugalfeast

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class AdaptadorBusqueda(
    private var listaBusqueda: ArrayList<BarraBusqueda>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<AdaptadorBusqueda.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(receta: BarraBusqueda)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivRecipeImage: ImageView = itemView.findViewById(R.id.ivRecipeImage)
        val tvRecipeTitle: TextView = itemView.findViewById(R.id.tvRecipeTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_receta, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listaBusqueda[position]

        Glide.with(holder.itemView.context)
            .load(item.imagenUrl)
            .into(holder.ivRecipeImage)

        holder.tvRecipeTitle.text = item.nombre

        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(item)
        }
    }

    override fun getItemCount(): Int = listaBusqueda.size

    fun updateList(newList: List<BarraBusqueda>) {
        listaBusqueda = ArrayList(newList)
        notifyDataSetChanged()
    }
}