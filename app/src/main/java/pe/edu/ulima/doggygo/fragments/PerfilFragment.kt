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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import pe.edu.ulima.doggygo.R
import pe.edu.ulima.doggygo.manager.UserManager
import pe.edu.ulima.doggygo.model.DogOwner
import pe.edu.ulima.doggygo.model.DogWalker
import pe.edu.ulima.doggygo.model.Pet
import pe.edu.ulima.doggygo.model.User
import java.util.*

class PerfilFragment(private val userType: String): Fragment() {

    interface Actions{
        fun onSaveUser_Perfil(email:String)
    }

    private var user: User? = null

    private var listener:Actions? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)

        listener = context as? Actions
    }

    private lateinit var progressBar: ProgressBar
    private lateinit var iviProfile: ImageView
    private lateinit var ProfileName: String
    private lateinit var photoUrl: String
    private val fileResult = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        user = arguments?.getSerializable("user") as User
        return inflater.inflate(R.layout.fragment_perfil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        var userId: String? = null
        progressBar = view.findViewById(R.id.progressBar)
        iviProfile = view.findViewById(R.id.iviProfile)
        photoUrl = "https://firebasestorage.googleapis.com/v0/b/doggywalk-nyobyte.appspot.com/o/Profiles%2FUsers%2FLogo.png?alt=media&token=222ea69f-f29c-4379-94af-babacc03d59b"

        if(userType== "dogWalker"){
            val userWalker = arguments?.getSerializable("user") as DogWalker
            userId = userWalker.userRef
        }else if(userType=="dogOwner"){
            val userOwner = arguments?.getSerializable("user") as DogOwner
            userId = userOwner.userRef
        }
        progressBar.visibility = View.GONE

        val userManager = UserManager(requireActivity().applicationContext)
        userManager.getPhotoUrl(userId!!) {
            photoUrl = it
            println(photoUrl)
            Glide.with(this).load(photoUrl).into(iviProfile)
        }
        setUser(view, user!!)
        genSelects(view)

        iviProfile.setOnClickListener{
            ProfileName = user!!.id!! + "_" + Date().time.toString()
            fileManager()
            progressBar.visibility = View.VISIBLE
        }

        view.findViewById<Button>(R.id.btnGuardar).setOnClickListener {
            val userTemp = getUser(view)
            userManager.updateUser(userId!!, userTemp)
            userManager.updatePhoto(userId!!, photoUrl)
            listener?.onSaveUser_Perfil(view.findViewById<TextInputLayout>(R.id.tinEmail).editText?.text.toString())
        }
    }

    private fun setUser(view: View, user: User){
        view.findViewById<TextInputLayout>(R.id.tinFirstName).editText?.setText(user.firstName)
        view.findViewById<TextInputLayout>(R.id.tinLastName).editText?.setText(user.lastName)
        view.findViewById<TextInputLayout>(R.id.tinTelf).editText?.setText(user.telf)
        view.findViewById<TextInputLayout>(R.id.tinAge).editText?.setText(user.age)
        view.findViewById<TextInputLayout>(R.id.tinEmail).editText?.setText(user.email)
        view.findViewById<TextInputLayout>(R.id.tinDocument).editText?.setText(user.nroDoc)
        view.findViewById<TextInputLayout>(R.id.tinAddress).editText?.setText(user.address)

        view.findViewById<TextInputLayout>(R.id.tinGenders).editText?.setText(user.gender)
        view.findViewById<TextInputLayout>(R.id.tinDocumentType).editText?.setText(user.docType)
        view.findViewById<TextInputLayout>(R.id.tinProvince).editText?.setText(user.province)
        view.findViewById<TextInputLayout>(R.id.tinDistrict).editText?.setText(user.district)
    }

    private fun getUser(view: View): User {
        return User(
            null,
            view.findViewById<TextInputLayout>(R.id.tinFirstName).editText?.text.toString(),
            view.findViewById<TextInputLayout>(R.id.tinLastName).editText?.text.toString(),
            view.findViewById<TextInputLayout>(R.id.tinGenders).editText?.text.toString(),
            view.findViewById<TextInputLayout>(R.id.tinTelf).editText?.text.toString(),
            view.findViewById<TextInputLayout>(R.id.tinAge).editText?.text.toString(),
            view.findViewById<TextInputLayout>(R.id.tinEmail).editText?.text.toString(),
            view.findViewById<TextInputLayout>(R.id.tinDocumentType).editText?.text.toString(),
            view.findViewById<TextInputLayout>(R.id.tinDocument).editText?.text.toString(),
            view.findViewById<TextInputLayout>(R.id.tinProvince).editText?.text.toString(),
            view.findViewById<TextInputLayout>(R.id.tinDistrict).editText?.text.toString(),
            view.findViewById<TextInputLayout>(R.id.tinAddress).editText?.text.toString(),
            "", "", "", ""
        )
    }

    private fun genSelects(view:View){
        val tinGenders = view.findViewById<TextInputLayout>(R.id.tinGenders)
        //Llenar el select de géneros
        val genders = listOf("Masculino", "Femenino", "No especificar")
        val gendersAdapter = ArrayAdapter(requireContext(),R.layout.list_items, genders)
        (tinGenders?.editText as? AutoCompleteTextView)?.setAdapter(gendersAdapter)

        val tinDocType = view.findViewById<TextInputLayout>(R.id.tinDocumentType)
        //Llenar el select de doc types
        val docTypeList = listOf("DNI", "CE")
        val docTypeAdapter = ArrayAdapter(requireContext(),R.layout.list_items, docTypeList)
        (tinDocType?.editText as? AutoCompleteTextView)?.setAdapter(docTypeAdapter)

        val tinProvince = view.findViewById<TextInputLayout>(R.id.tinProvince)
        //Llenar el select de provincias
        val provinceList = listOf("Lima")
        val provinceAdapter = ArrayAdapter(requireContext(),R.layout.list_items, provinceList)
        (tinProvince?.editText as? AutoCompleteTextView)?.setAdapter(provinceAdapter)

        val tinDistrict = view.findViewById<TextInputLayout>(R.id.tinDistrict)
        //Llenar el select de provincias
        val districtList = listOf("La Molina", "Miraflores", "San Borja", "San Isidro","Surco")
        val districtAdapter = ArrayAdapter(requireContext(),R.layout.list_items, districtList)
        (tinDistrict?.editText as? AutoCompleteTextView)?.setAdapter(districtAdapter)
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
        val folder: StorageReference = FirebaseStorage.getInstance().reference.child("Profiles/Users")
        //val path = myUri.lastPathSegment.toString()
        //val fileName: StorageReference = folder.child(path.substring(path.lastIndexOf('/')+1))
        val fileName: StorageReference = folder.child("${ProfileName}.jpg")
        fileName.putFile(myUri)
            .addOnSuccessListener{
                fileName.downloadUrl.addOnSuccessListener{ uri ->
                    photoUrl = uri.toString()
                    Glide.with(this).load(uri).into(iviProfile)
                    progressBar.visibility = View.GONE
                    //userManager.updateCertificate(user?.id!!,false)
                    Log.i("PerfilFragment", "File upload successfully")
                }
            }
            .addOnFailureListener {
                Log.i("PerfilFragment","File upload error")
            }
    }

    private fun fileManager(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, fileResult)
    }
}