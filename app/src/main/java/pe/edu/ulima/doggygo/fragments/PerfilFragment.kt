package pe.edu.ulima.doggygo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputLayout
import pe.edu.ulima.doggygo.R
import pe.edu.ulima.doggygo.model.User

class PerfilFragment: Fragment() {

    private var user: User? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        user = arguments?.getSerializable("user") as User
        return inflater.inflate(R.layout.fragment_perfil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        setUser(view, user!!)
        genSelects(view)
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

    private fun genSelects(view:View){
        val tinGenders = view.findViewById<TextInputLayout>(R.id.tinGenders)
        //Llenar el select de géneros
        val genders = listOf("Masculino", "Femenino", "No especificar")
        val gendersAdapter = ArrayAdapter(requireContext(),R.layout.list_items, genders)
        (tinGenders?.editText as? AutoCompleteTextView)?.setAdapter(gendersAdapter)

        val tinDocType = view.findViewById<TextInputLayout>(R.id.tinDocumentType)
        //Llenar el select de doc types
        val docTypeList = listOf("DNI", "Carné de extranjería")
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