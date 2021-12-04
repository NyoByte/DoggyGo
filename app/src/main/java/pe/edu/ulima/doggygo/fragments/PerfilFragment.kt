package pe.edu.ulima.doggygo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import pe.edu.ulima.doggygo.R
import pe.edu.ulima.doggygo.manager.UserManager
import pe.edu.ulima.doggygo.model.DogOwner
import pe.edu.ulima.doggygo.model.DogWalker
import pe.edu.ulima.doggygo.model.User

class PerfilFragment(private val userType: String): Fragment() {

    private var user: User? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        user = arguments?.getSerializable("user") as User
        return inflater.inflate(R.layout.fragment_perfil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        var userId: String? = null

        if(userType== "dogWalker"){
            val userWalker = arguments?.getSerializable("user") as DogWalker
            userId = userWalker.userRef
        }else if(userType=="dogOwner"){
            val userOwner = arguments?.getSerializable("user") as DogOwner
            userId = userOwner.userRef
        }

        val userManager = UserManager(requireActivity().applicationContext)

        setUser(view, user!!)
        genSelects(view)

        view.findViewById<Button>(R.id.btnGuardar).setOnClickListener {
            val userTemp = getUser(view)
            userManager.updateUser(userId!!, userTemp)
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
}