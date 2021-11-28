package pe.edu.ulima.doggygo.manager

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pe.edu.ulima.doggygo.model.DogWalker
import pe.edu.ulima.doggygo.model.Review
import pe.edu.ulima.doggygo.model.User
import java.math.BigInteger
import java.security.MessageDigest

class UserManager(private val context: Context) {

    private val dbFirebase = Firebase.firestore

    fun getUserDogWalkerById(userId: String, callbackOK: (DogWalker) -> Unit, callbackERROR: (String) -> Unit){
        dbFirebase.collection("DogWalkers").document(userId)
            .get()
            .addOnSuccessListener { docWalker ->
                println("==>"+userId)
                println("--->>"+(docWalker.data?.get("userRef") as DocumentReference).id)
                (docWalker.data?.get("userRef") as DocumentReference)
                    .get()
                    .addOnSuccessListener { docUser ->
                        val dogWalker = DogWalker(
                            id = docWalker.id,
                            firstName = docUser.data?.get("firstName").toString(),
                            lastName = docUser.data?.get("lastName").toString(),
                            type = docUser.data?.get("type").toString(),
                            address = docUser.data?.get("address").toString(),
                            age = docUser.data?.get("age").toString(),
                            nroDoc = docUser.data?.get("nroDoc").toString(),
                            docType = docUser.data?.get("docType").toString(),
                            email = docUser.data?.get("email").toString(),
                            telf = docUser.data?.get("telf").toString(),
                            gender = docUser.data?.get("gender").toString(),
                            createdDate = docUser.data?.get("createdDate").toString(),
                            province = docUser.data?.get("province").toString(),
                            district = docUser.data?.get("district").toString(),
                            username = docUser.data?.get("username").toString(),
                            password = null,
                            active = docUser.data?.get("active").toString().toBoolean(),
                            desc = docWalker.get("desc").toString(),
                            price = docWalker.get("price").toString().toInt(),
                            score = docWalker.get("score").toString().toInt(),
                            userRef = docUser.id
                        )
                        Log.d("UserManager","Loaded user from Firebase")
                        callbackOK(dogWalker)

                    }
                    .addOnFailureListener {
                        callbackERROR(it.message!!)
                    }
            }
            .addOnFailureListener {
                callbackERROR(it.message!!)
            }
    }

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

    fun updateUser(userId: String, user: User){
        dbFirebase.collection("Users").document(userId)
            .update(mapOf(
                "firstName" to user.firstName,
                "lastName" to user.lastName,
                "gender" to user.gender,
                "telf" to user.telf,
                "age" to user.age.toInt(),
                "email" to user.email,
                "docType" to user.docType,
                "nroDoc" to user.nroDoc,
                "province" to user.province,
                "district" to user.district,
                "address" to user.address
            ))
            .addOnSuccessListener {
                Toast.makeText(context, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error al actualizar sus datos", Toast.LENGTH_SHORT).show()
                Log.e("PerfilFragment", it.message!!)
            }
    }

    private fun md5(input:String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }
}