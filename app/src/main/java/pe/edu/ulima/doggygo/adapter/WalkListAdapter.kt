package pe.edu.ulima.doggygo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import pe.edu.ulima.doggygo.R
import pe.edu.ulima.doggygo.model.Contract
import pe.edu.ulima.doggygo.model.Walk

class WalkListAdapter(private val fragment: Fragment,
                      private val walkList: MutableList<Walk>,
                      private val listener: (Walk) -> Unit): RecyclerView.Adapter<WalkListAdapter.ViewHolder>() {
    class ViewHolder(view: View, val walkList: List<Walk>,
                     val listener: (Walk) -> Unit): RecyclerView.ViewHolder(view), View.OnClickListener {
        val iviPaseoMascota: ImageView = view.findViewById(R.id.iviPaseoMascota)
        val tviPaseoFullName: TextView = view.findViewById(R.id.tviPaseoFullName)
        val tviPaseoDistrict: TextView = view.findViewById(R.id.tviPaseoDistrict)
        val tviPaseoDogName: TextView = view.findViewById(R.id.tviPaseoDogName)
        val tviPaseoRaza: TextView = view.findViewById(R.id.tviPaseoRaza)
        val tviPaseoActivityLevel: TextView = view.findViewById(R.id.tviPaseoActivityLevel)
        val tviPaseoEdad: TextView = view.findViewById(R.id.tviPaseoEdad)
        val tviPaseoPeso: TextView = view.findViewById(R.id.tviPaseoPeso)
        val tviPaseoDate: TextView = view.findViewById(R.id.tviPaseoDate)
        val tviPaseoTime: TextView = view.findViewById(R.id.tviPaseoTime)
        val fabPaseoNote: FloatingActionButton = view.findViewById<FloatingActionButton>(R.id.fabPaseo)

        override fun onClick(p0: View?) {
            listener(walkList[adapterPosition])
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_paseo, parent, false)
        return WalkListAdapter.ViewHolder(view, walkList, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tviPaseoActivityLevel.text = walkList[position].dogActivityLevel
        holder.tviPaseoDistrict.text = walkList[position].dogOwnerDistrict
        holder.tviPaseoDogName.text = walkList[position].dogName
        holder.tviPaseoEdad.text = walkList[position].dogAge.toString()
        holder.tviPaseoFullName.text = walkList[position].dogOwnerFullName
        holder.tviPaseoPeso.text = walkList[position].dogWeight.toString()
        holder.tviPaseoRaza.text = walkList[position].dogBreed
        holder.tviPaseoDate.text = walkList[position].date
        holder.tviPaseoTime.text = walkList[position].time.toString() + " hrs"
        Glide.with(fragment).load(walkList[position].photoUrl).into(holder.iviPaseoMascota)

        holder.fabPaseoNote.setOnClickListener {
            listener(walkList[position])
        }
    }

    override fun getItemCount(): Int {
        return walkList.size
    }
}