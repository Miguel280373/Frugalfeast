import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R

class RecientementeAdapter(
    private val recetas: MutableList<Receta>,
    private val onRecetaLongClick: (Receta, View) -> Unit
) : RecyclerView.Adapter<RecientementeAdapter.RecetaViewHolder>() {

    inner class RecetaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre: TextView = itemView.findViewById(R.id.recetaNombreItem)
        val imagen: ImageView = itemView.findViewById(R.id.imgReceta)

        init {
            itemView.setOnLongClickListener {
                onRecetaLongClick(recetas[adapterPosition], it)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecetaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_receta, parent, false)
        return RecetaViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecetaViewHolder, position: Int) {
        val receta = recetas[position]
        holder.nombre.text = receta.nombreReceta

        Glide.with(holder.itemView.context)
            .load(receta.imagenUrl)
            .into(holder.imagen)
    }

    override fun getItemCount(): Int = recetas.size


}
