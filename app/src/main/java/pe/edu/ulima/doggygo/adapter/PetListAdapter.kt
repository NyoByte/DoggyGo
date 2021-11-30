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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import pe.edu.ulima.doggygo.R
import pe.edu.ulima.doggygo.model.Contract
import pe.edu.ulima.doggygo.model.Pet

class PetListAdapter(private val fragment: Fragment,
                     private val petList: MutableList<Pet>,
                     private val listener: (Pet) -> Unit): RecyclerView.Adapter<PetListAdapter.ViewHolder>() {
    class ViewHolder(view: View, val petList: List<Pet>,
                     val listener: (Pet) -> Unit): RecyclerView.ViewHolder(view), View.OnClickListener  {
        val iviPetMascota: ImageView = view.findViewById(R.id.iviPetMascota)
        val tviPetName: TextView = view.findViewById(R.id.tviPetName)
        val tviPetRaza: TextView = view.findViewById(R.id.tviPetRaza)
        val tviPetActivityLevel: TextView = view.findViewById(R.id.tviPetActivityLevel)
        val tviPetEdad: TextView = view.findViewById(R.id.tviPetEdad)
        val tviPetPeso: TextView = view.findViewById(R.id.tviPetPeso)
        val fabPetNotes: FloatingActionButton = view.findViewById<FloatingActionButton>(R.id.fabPetNotes)
        val fabPetEdit: FloatingActionButton = view.findViewById<FloatingActionButton>(R.id.fabPetEdit)

        init{
            view.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mascota, parent, false)
        return PetListAdapter.ViewHolder(view, petList, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tviPetActivityLevel.text = petList[position].activityLevel
        holder.tviPetName.text = petList[position].name
        holder.tviPetEdad.text = petList[position].age.toString()
        holder.tviPetPeso.text = petList[position].weight.toString()
        holder.tviPetRaza.text = petList[position].breed
        Glide.with(fragment).load(petList[position].photoUrl).into(holder.iviPetMascota)

        holder.fabPetNotes.setOnClickListener{
            fragment.context?.let { it1 ->
                MaterialAlertDialogBuilder(it1)
                    .setMessage(petList[position].note)
                    .setPositiveButton("Ok") { _, _ ->
                        println("Ok")
                    }
                    .show()
            }
        }

        holder.fabPetEdit.setOnClickListener{
            listener(petList[position])
        }
    }

    override fun getItemCount(): Int {
        return petList.size
    }
}