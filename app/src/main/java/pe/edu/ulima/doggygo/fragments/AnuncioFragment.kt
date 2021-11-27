package pe.edu.ulima.doggygo.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.chip.Chip
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pe.edu.ulima.doggygo.R
import pe.edu.ulima.doggygo.model.User

class AnuncioFragment: Fragment() {

    private val dbFirebase = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_anuncio, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val user = arguments?.getSerializable("user") as User

        view.findViewById<TextView>(R.id.tviAnuncioName).text = "${user.firstName} ${user.lastName}"
        view.findViewById<TextView>(R.id.tviAnuncioDistrict).text = user.district
        view.findViewById<TextView>(R.id.tviAnuncioAntiguedad).text = user.createdDate
        val eteAnuncioDesc = view.findViewById<TextView>(R.id.eteAnuncioDesc)
        eteAnuncioDesc.text = user.desc
        val eteAnuncioPrice = view.findViewById<TextView>(R.id.eteAnuncioPrice)
        eteAnuncioPrice.text = user.price.toString()
        view.findViewById<RatingBar>(R.id.rbaAnuncio).rating = user.score.toFloat()

        if(user.active){
            view.findViewById<Chip>(R.id.chpAnuncioActivo).isChecked = true
            view.findViewById<Chip>(R.id.chpAnuncioInactivo).isChecked = false
        }else{
            view.findViewById<Chip>(R.id.chpAnuncioActivo).isChecked = false
            view.findViewById<Chip>(R.id.chpAnuncioInactivo).isChecked = true
        }

        view.findViewById<Button>(R.id.btnAnuncioGuardar).setOnClickListener {

            var anuncioActivo = false
            if(view.findViewById<Chip>(R.id.chpAnuncioActivo).isChecked){
                anuncioActivo = true
            }

            dbFirebase.collection("Users").document(user.id!!)
                .update(mapOf(
                    "desc" to eteAnuncioDesc.text.toString(),
                    "price" to eteAnuncioPrice.text.toString().toInt(),
                    "active" to anuncioActivo
                ))
                .addOnSuccessListener {
                    Toast.makeText(context, "Anuncio actualizado correctamente", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Error al actualizar el anuncio", Toast.LENGTH_LONG).show()
                    Log.e("AnuncioFragment", it.message!!)
                }
        }
    }
}