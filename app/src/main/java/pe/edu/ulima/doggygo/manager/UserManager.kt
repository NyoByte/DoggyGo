package pe.edu.ulima.doggygo.manager

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pe.edu.ulima.doggygo.model.Review
import java.math.BigInteger
import java.security.MessageDigest

class UserManager(private val context: Context) {

    private val dbFirebase = Firebase.firestore

    fun updateUserAuth(userId: String, newUsername: String, newPassword: String){

        val md5Password = md5(newPassword)

        dbFirebase.collection("Users").document(userId)
            .update(mapOf(
                "username" to newUsername,
                "password" to md5Password
            ))
            .addOnSuccessListener {
                Toast.makeText(context, "Credenciales actualizadas correctamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error al actualizar credenciales", Toast.LENGTH_SHORT).show()
                Log.e("UsuarioFragment", it.message!!)
            }
    }

    private fun md5(input:String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }
}