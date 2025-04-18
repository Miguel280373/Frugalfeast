import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R

class RecetasGuardadasAdapter(private val recetas: List<Receta>) : RecyclerView.Adapter<RecetasGuardadasAdapter.RecetaViewHolder>() {

    inner class RecetaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreReceta: TextView = itemView.findViewById(R.id.recetaNombreChiquito)
        val tiempoReceta: TextView = itemView.findViewById(R.id.recetaTiempoChiquito)
        val porcionesReceta: TextView = itemView.findViewById(R.id.recetaPorcionesChiquito)
        val dificultadReceta: TextView = itemView.findViewById(R.id.recetaDificultadChiquito)
        val imagenReceta: ImageView = itemView.findViewById(R.id.imagenRecetaChiquito)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecetaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_receta, parent, false)
        return RecetaViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecetaViewHolder, position: Int) {
        val receta = recetas[position]
        holder.nombreReceta.text = receta.nombreReceta
        holder.tiempoReceta.text = receta.tiempoPreparacion.toString()
        holder.porcionesReceta.text = receta.porciones.toString()
        holder.dificultadReceta.text = receta.dificultad

        Glide.with(holder.itemView.context).load(receta.imagenUrl).into(holder.imagenReceta)
    }

    override fun getItemCount(): Int {
        return recetas.size
    }
}
