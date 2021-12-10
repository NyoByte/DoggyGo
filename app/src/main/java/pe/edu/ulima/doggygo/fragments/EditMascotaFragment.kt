package pe.edu.ulima.doggygo.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import pe.edu.ulima.doggygo.R
import pe.edu.ulima.doggygo.adapter.PdfAdapter
import pe.edu.ulima.doggygo.model.DogOwner
import pe.edu.ulima.doggygo.model.DogWalker
import pe.edu.ulima.doggygo.model.Pet
import java.util.*

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

    private lateinit var progressBar: ProgressBar
    private lateinit var iviPetEdit: ImageView
    private lateinit var iviPetName: String
    private lateinit var photoUrl: String
    private val fileResult = 1

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

        progressBar = view.findViewById(R.id.progressBar)
        iviPetEdit = view.findViewById(R.id.iviPetEdit)
        photoUrl = "https://phantom-marca.unidadeditorial.es/252acdd64f48851f815c16049a789f23/resize/1320/f/jpg/assets/multimedia/imagenes/2021/04/19/16188479459744.jpg"

        if(pet != null){
            pet = pet as Pet
            view.findViewById<EditText>(R.id.etePetName).setText(pet.name)
            view.findViewById<EditText>(R.id.etePetWeight).setText(pet.weight.toString())
            view.findViewById<EditText>(R.id.etePetAge).setText(pet.age.toString())
            view.findViewById<EditText>(R.id.etePetNotes).setText(pet.note)
            view.findViewById<TextInputLayout>(R.id.tinPetSex).editText?.setText(pet.sex)
            view.findViewById<TextInputLayout>(R.id.tinPetActivityLevel).editText?.setText(pet.activityLevel)
            view.findViewById<TextInputLayout>(R.id.tinPetBreed).editText?.setText(pet.breed)
            Glide.with(this).load(pet.photoUrl).into(iviPetEdit)
        }
        progressBar.visibility = View.GONE

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

        iviPetEdit.setOnClickListener{
            Log.i("EditMascotaFragment","Click")
            iviPetName = user.id!! + "_" + Date().time.toString()
            fileManager()
            progressBar.visibility = View.VISIBLE
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
                "photo" to photoUrl
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == fileResult){
            if(resultCode == Activity.RESULT_OK && data!= null){
                val clipData = data.clipData
                if(clipData!=null){
                    for(i in 0 until clipData.itemCount){
                        val uri = clipData.getItemAt(i).uri
                        uri?.let {fileUpload(it)}
                    }
                }else{
                    val uri = data.data
                    uri?.let {fileUpload(it)}
                }
            }else{
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun fileUpload(myUri: Uri){
        val folder: StorageReference = FirebaseStorage.getInstance().reference.child("Profiles/Pets")
        //val path = myUri.lastPathSegment.toString()
        //val fileName: StorageReference = folder.child(path.substring(path.lastIndexOf('/')+1))
        val fileName: StorageReference = folder.child("${iviPetName}.jpg")
        fileName.putFile(myUri)
            .addOnSuccessListener{
                fileName.downloadUrl.addOnSuccessListener{ uri ->
                    photoUrl = uri.toString()
                    Glide.with(this).load(uri).into(iviPetEdit)
                    progressBar.visibility = View.GONE
                    //userManager.updateCertificate(user?.id!!,false)
                    Log.i("EditMascotaFragment", "File upload successfully")
                }
            }
            .addOnFailureListener {
                Log.i("EditMascotaFragment","File upload error")
            }
    }

    private fun fileManager(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, fileResult)
    }
}