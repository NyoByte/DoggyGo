package pe.edu.ulima.doggygo.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pe.edu.ulima.doggygo.R
import pe.edu.ulima.doggygo.model.DogOwner
import pe.edu.ulima.doggygo.model.DogWalker
import pe.edu.ulima.doggygo.model.Pet

class EditMascotaFragment: Fragment() {

    private val dbFirestore = Firebase.firestore

    interface Actions{
        fun onPetCreated()
    }

    private lateinit var listener: Actions

    override fun onAttach(context: Context) {
        super.onAttach(context)

        listener = context as Actions
        if(listener == null){
            Log.e("MascotasFragment", "Activity no implementa la interfaz MascotasFragment.Actions")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_mascota, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = arguments?.getSerializable("user") as DogOwner
        var pet = arguments?.getSerializable("pet")

        val tinPetSex = view.findViewById<TextInputLayout>(R.id.tinPetSex)
        val sexTypeList = listOf("Masculino", "Femenino")
        val sexTypeAdapter = ArrayAdapter(view.context,R.layout.list_items, sexTypeList)
        (tinPetSex?.editText as? AutoCompleteTextView)?.setAdapter(sexTypeAdapter)

        val tinPetActivityLevel = view.findViewById<TextInputLayout>(R.id.tinPetActivityLevel)
        val activityLevelList = listOf("Alta", "Media", "Baja")
        val activityLevelAdapter = ArrayAdapter(view.context,R.layout.list_items, activityLevelList)
        (tinPetActivityLevel?.editText as? AutoCompleteTextView)?.setAdapter(activityLevelAdapter)

        val tinPetBreed = view.findViewById<TextInputLayout>(R.id.tinPetBreed)
        val breedList = listOf("Pitbull", "Shiba Inu")
        val breedAdapter = ArrayAdapter(view.context,R.layout.list_items, breedList)
        (tinPetBreed?.editText as? AutoCompleteTextView)?.setAdapter(breedAdapter)

        if(pet != null){
            pet = pet as Pet
            view.findViewById<EditText>(R.id.etePetName).setText(pet.name)
            view.findViewById<EditText>(R.id.etePetWeight).setText(pet.weight.toString())
            view.findViewById<EditText>(R.id.etePetAge).setText(pet.age.toString())
            view.findViewById<EditText>(R.id.etePetNotes).setText(pet.note)
            view.findViewById<TextInputLayout>(R.id.tinPetSex).editText?.setText(pet.sex)
            view.findViewById<TextInputLayout>(R.id.tinPetActivityLevel).editText?.setText(pet.activityLevel)
            view.findViewById<TextInputLayout>(R.id.tinPetBreed).editText?.setText(pet.breed)
        }

        view.findViewById<Button>(R.id.btnPetGuardar).setOnClickListener {
            val name = view.findViewById<EditText>(R.id.etePetName).text.toString()
            val weight = view.findViewById<EditText>(R.id.etePetWeight).text.toString().toInt()
            val age = view.findViewById<EditText>(R.id.etePetAge).text.toString().toInt()
            val notes = view.findViewById<EditText>(R.id.etePetNotes).text.toString()
            val sex = view.findViewById<TextInputLayout>(R.id.tinPetSex).editText?.text.toString()
            val activityLevel = view.findViewById<TextInputLayout>(R.id.tinPetActivityLevel).editText?.text.toString()
            val breed = view.findViewById<TextInputLayout>(R.id.tinPetBreed).editText?.text.toString()

            val newPet = hashMapOf<String, Any>(
                "name" to name,
                "age" to age,
                "gender" to sex,
                "weight" to weight,
                "note" to notes,
                "activityLevel" to activityLevel,
                "breed" to breed,
                "photo" to "https://phantom-marca.unidadeditorial.es/252acdd64f48851f815c16049a789f23/resize/1320/f/jpg/assets/multimedia/imagenes/2021/04/19/16188479459744.jpg"
            )
            if(pet != null){
                dbFirestore.collection("Dogs")
                    .document((pet as Pet).id!!)
                    .update(newPet)
                    .addOnSuccessListener {
                        Toast.makeText(view.context,"Mascota actualizada correctamente", Toast.LENGTH_SHORT).show()
                        listener?.onPetCreated()
                    }
                    .addOnFailureListener {
                        Toast.makeText(view.context,"Ocurrió un error al actualizar la mascota", Toast.LENGTH_SHORT).show()
                        Log.e("EditMascotaFragment", it.message!!)
                    }
            }else{
                dbFirestore.collection("Dogs")
                    .add(newPet)
                    .addOnSuccessListener {
                        dbFirestore.collection("DogOwners")
                            .document(user.id!!)
                            .update("dogsRef", FieldValue.arrayUnion(it))
                            .addOnSuccessListener {
                                Toast.makeText(view.context,"Mascota creada correctamente", Toast.LENGTH_SHORT).show()
                                listener?.onPetCreated()
                            }
                            .addOnFailureListener {
                                Toast.makeText(view.context,"Ocurrió un error al crear la mascota", Toast.LENGTH_SHORT).show()
                                Log.e("EditMascotaFragment", it.message!!)
                            }
                    }
                    .addOnFailureListener {
                        Toast.makeText(view.context,"Ocurrió un error al crear la mascota", Toast.LENGTH_SHORT).show()
                        Log.e("EditMascotaFragment", it.message!!)
                    }
            }
        }
    }
}