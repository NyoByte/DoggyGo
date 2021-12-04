package pe.edu.ulima.doggygo.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import pe.edu.ulima.doggygo.R
import pe.edu.ulima.doggygo.model.Contract
import pe.edu.ulima.doggygo.model.DogWalker

class AnunciosListAdapter(private val fragment: Fragment,
                          private val anunciosList: MutableList<DogWalker>,
                          private val listener: (DogWalker) -> Unit): RecyclerView.Adapter<AnunciosListAdapter.ViewHolder>() {
    class ViewHolder(view: View, val anunciosList: List<DogWalker>,
                     val listener: (DogWalker) -> Unit): RecyclerView.ViewHolder(view), View.OnClickListener {
        val tviAnunciosName = view.findViewById<TextView>(R.id.tviAnunciosName)
        val tviAnunciosDistrict = view.findViewById<TextView>(R.id.tviAnunciosDistrict)
        val rbaAnuncios = view.findViewById<RatingBar>(R.id.rbaAnuncios)
        val tviAnunciosAntiguedad = view.findViewById<TextView>(R.id.tviAnunciosAntiguedad)
        val tviAnunciosResenhas = view.findViewById<TextView>(R.id.tviAnunciosResenhas)
        val tviAnunciosNumPaseos = view.findViewById<TextView>(R.id.tviAnunciosNumPaseos)
        val tviAnunciosNotes = view.findViewById<TextView>(R.id.tviAnunciosNotes)
        val tviAnunciosPrecio = view.findViewById<TextView>(R.id.tviAnunciosPrecio)
        val mbuAnunciosPhone = view.findViewById<MaterialButton>(R.id.mbuAnunciosPhone)
        val mbuContratosAceptar = view.findViewById<MaterialButton>(R.id.mbuContratosAceptar)

        init{
            view.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_anuncio, parent, false)
        return ViewHolder(view, anunciosList, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tviAnunciosName.text = anunciosList[position].firstName + " " + anunciosList[position].lastName
        holder.tviAnunciosDistrict.text = anunciosList[position].district
        holder.rbaAnuncios.rating = anunciosList[position].score.toFloat()
        holder.tviAnunciosAntiguedad.text = anunciosList[position].createdDate
        holder.tviAnunciosNotes.text = anunciosList[position].desc
        holder.tviAnunciosPrecio.text = anunciosList[position].price.toString()
        holder.mbuAnunciosPhone.text = anunciosList[position].telf
        holder.tviAnunciosResenhas.text = anunciosList[position].numReviews.toString()
        holder.tviAnunciosNumPaseos.text = anunciosList[position].numWalks.toString()

        holder.mbuAnunciosPhone.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:<${anunciosList[position].telf}>")
            fragment.startActivity(intent)
        }

        holder.mbuContratosAceptar.setOnClickListener {
            listener(anunciosList[position])
        }
    }

    override fun getItemCount(): Int {
        return anunciosList.size
    }


}