package com.example.frugalfeast

import com.squareup.picasso.Picasso
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class Adaptador(
    private var listaBusqueda: ArrayList<BarraBusqueda>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<Adaptador.ViewHolder>() {

    // Crear la interfaz
    interface OnItemClickListener {
        fun onItemClick(receta: BarraBusqueda)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivRecipeImage: ImageView = itemView.findViewById(R.id.ivRecipeImage)
        val tvRecipeTitle: TextView = itemView.findViewById(R.id.tvRecipeTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rv_usuario, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listaBusqueda[position]
        // Carga la imagen usando una librería como Picasso o Glide
        Picasso.get().load(item.imagenUrl).into(holder.ivRecipeImage)
        holder.tvRecipeTitle.text = item.nombre

        // Configura el clic en el item
        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(item)
        }
    }

    override fun getItemCount(): Int = listaBusqueda.size

    // Actualiza la lista filtrada
    fun updateList(newList: List<BarraBusqueda>) {
        listaBusqueda = ArrayList(newList)
        notifyDataSetChanged()
    }
}
