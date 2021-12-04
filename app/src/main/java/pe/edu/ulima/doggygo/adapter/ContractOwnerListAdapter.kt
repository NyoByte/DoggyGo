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

class ContractOwnerListAdapter(private val fragment: Fragment,
                               private val contractList: MutableList<Contract>,
                               private val listener: (Contract) -> Unit): RecyclerView.Adapter<ContractOwnerListAdapter.ViewHolder>() {
    class ViewHolder(view: View, val contractList: List<Contract>,
                     val listener: (Contract) -> Unit): RecyclerView.ViewHolder(view), View.OnClickListener {
        val iviContratoMascota: ImageView = view.findViewById(R.id.iviContratoMascota)
        val tviContratoFullName: TextView = view.findViewById(R.id.tviContratoFullName)
        val tviContratoDistrict: TextView = view.findViewById(R.id.tviContratoDistrict)
        val tviContratoDogName: TextView = view.findViewById(R.id.tviContratoDogName)
        val tviContratoRaza: TextView = view.findViewById(R.id.tviContratoRaza)
        val tviContratoActivityLevel: TextView = view.findViewById(R.id.tviContratoActivityLevel)
        val tviContratoEdad: TextView = view.findViewById(R.id.tviContratoEdad)
        val tviContratoPeso: TextView = view.findViewById(R.id.tviContratoPeso)
        val tviContratoDate: TextView = view.findViewById(R.id.tviContratoDate)
        val tviContratoTime: TextView = view.findViewById(R.id.tviContratoTime)
        val fabContratoNote: FloatingActionButton = view.findViewById<FloatingActionButton>(R.id.fabContratoNote)
        val mbuContratoCancelar: MaterialButton = view.findViewById(R.id.mbuContratoCancelar)
        val tviContratoOwnerPrice: TextView = view.findViewById(R.id.tviContratoOwnerPrice)

        init{
            view.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_contrato_owner, parent, false)
        return ViewHolder(view, contractList, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tviContratoActivityLevel.text = contractList[position].dogActivityLevel
        holder.tviContratoDistrict.text = contractList[position].dogOwnerDistrict
        holder.tviContratoDogName.text = contractList[position].dogName
        holder.tviContratoEdad.text = contractList[position].dogAge.toString()
        holder.tviContratoFullName.text = contractList[position].dogOwnerFullName
        holder.tviContratoPeso.text = contractList[position].dogWeight.toString()
        holder.tviContratoRaza.text = contractList[position].dogBreed
        holder.tviContratoDate.text = contractList[position].date
        holder.tviContratoTime.text = contractList[position].time.toString() + " hrs"
        holder.tviContratoOwnerPrice.text = "Precio: S/ ${contractList[position].price.toString()}"
        Glide.with(fragment).load(contractList[position].photoUrl).into(holder.iviContratoMascota)

        holder.fabContratoNote.setOnClickListener{
            fragment.context?.let { it1 ->
                MaterialAlertDialogBuilder(it1)
                    .setMessage(contractList[position].note)
                    .setPositiveButton("Ok") { dialog, which ->
                        println("Ok")
                    }
                    .show()
            }
        }

        holder.mbuContratoCancelar.setOnClickListener {
            listener(contractList[position])
        }
    }

    override fun getItemCount(): Int {
        return contractList.size
    }
}