package pe.edu.ulima.doggygo.fragments

import android.content.Context
import android.graphics.ColorFilter
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.TintContextWrapper
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import pe.edu.ulima.doggygo.R
import pe.edu.ulima.doggygo.manager.UserManager
import pe.edu.ulima.doggygo.model.DogOwner
import pe.edu.ulima.doggygo.model.DogWalker
import pe.edu.ulima.doggygo.model.User

class UsuarioFragment(private val userType: String): Fragment() {

    interface Actions{
        fun onSaveUser_Usuario(username:String)
    }

    private var listener: UsuarioFragment.Actions? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)

        listener = context as? UsuarioFragment.Actions
    }

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
            userId = userOwner.userRef
            usernameCurrent = userOwner.username
        }

        val userManager = UserManager(requireActivity().applicationContext)

        val tinUsername = view.findViewById<TextInputLayout>(R.id.tinUsername)
        val tinPassword = view.findViewById<TextInputLayout>(R.id.tinPassword)

        tinUsername.editText?.setText(usernameCurrent)

        view.findViewById<Button>(R.id.btnGuardar).setOnClickListener {
            val usernameEdit = tinUsername.editText?.text
            val passwordEdit = tinPassword.editText?.text
            if(saveValidations(usernameEdit, passwordEdit)){
                if(passwordEdit!!.isEmpty()){
                    userManager.existUsername(userId!!, usernameEdit.toString()) { yaExiste ->
                        if(yaExiste){
                            Toast.makeText(context, "Ya existe el username", Toast.LENGTH_SHORT).show()
                        }else{
                            userManager.updateUserAuth(userId, usernameEdit.toString())
                            listener?.onSaveUser_Usuario(usernameEdit.toString())
                        }
                    }
                }else{
                    userManager.existUsername(userId!!, usernameEdit.toString()){ yaExiste ->
                        if(yaExiste){
                            Toast.makeText(context, "Ya existe el username", Toast.LENGTH_SHORT).show()
                        }else{
                            userManager.updateUserAuth(userId, usernameEdit.toString(), passwordEdit.toString())
                            listener?.onSaveUser_Usuario(usernameEdit.toString())
                            tinPassword.editText?.setText("")
                        }
                    }
                }
            }
        }
    }

    private fun saveValidations(username: Editable?, password:Editable?): Boolean {
        if (username.isNullOrBlank()){
            Toast.makeText(context, "Debe escribir el username", Toast.LENGTH_SHORT).show()
            return false
        }else if (password!!.contains(" ") || (password.length<4 && password.isNotEmpty())){
            Toast.makeText(context, "La password no debe tener espacios y debe contener más de 3 caracteres", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}