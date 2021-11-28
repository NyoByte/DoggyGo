package pe.edu.ulima.doggygo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import pe.edu.ulima.doggygo.R
import pe.edu.ulima.doggygo.manager.UserManager
import pe.edu.ulima.doggygo.model.DogOwner
import pe.edu.ulima.doggygo.model.DogWalker
import pe.edu.ulima.doggygo.model.User

class UsuarioFragment(private val userType: String): Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_usuario, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var userId: String? = null
        var usernameCurrent: String? = null

        if(userType== "dogWalker"){
            val userWalker = arguments?.getSerializable("user") as DogWalker
            userId = userWalker.userRef
            usernameCurrent = userWalker.username
        }else if(userType=="dogOwner"){
            val userOwner = arguments?.getSerializable("user") as DogOwner
            // TODO ("aun no implementado")
            userId = null
            usernameCurrent = ""
        }

        val userManager = UserManager(requireActivity().applicationContext)

        val tinUsername = view.findViewById<TextInputLayout>(R.id.tinUsername)
        val tinPassword = view.findViewById<TextInputLayout>(R.id.tinPassword)

        tinUsername.editText?.setText(usernameCurrent)

        view.findViewById<Button>(R.id.btnGuardar).setOnClickListener {
            userManager.updateUserAuth(userId!!, tinUsername.editText?.text.toString(), tinPassword.editText?.text.toString())
            tinUsername.editText?.setText("")
            tinPassword.editText?.setText("")
        }
    }
}