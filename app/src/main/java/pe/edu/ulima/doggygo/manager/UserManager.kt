package pe.edu.ulima.doggygo.manager

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pe.edu.ulima.doggygo.model.DogOwner
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
                            score = docWalker.get("score").toString().toFloat(),
                            userRef = docUser.id,
                            numReviews = docWalker.get("numReviews").toString().toInt(),
                            numWalks = docWalker.get("numWalks").toString().toInt(),
                            certificateAccepted = docWalker.get("certificateAccepted").toString().toBoolean()
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

    fun getTelfWalkerByWalkId(walkId: String, callbackOK: (String) -> Unit, callbackERROR: (String) -> Unit){
        println("==1==")
        dbFirebase.collection("Contracts")
            .whereEqualTo("walkRef", dbFirebase.collection("Walks").document(walkId))
            .get()
            .addOnSuccessListener { res ->
                println("==2==")
                //println(res.documents.get(0).id)
                for (document in res) {
                    (document.data["dogWalkerRef"] as DocumentReference)
                        .get()
                        .addOnSuccessListener { docUser ->
                            callbackOK(docUser.get("telf").toString())
                        }
                        .addOnFailureListener {
                            callbackERROR(it.message!!)
                        }
                }
            }
            .addOnFailureListener {
                callbackERROR(it.message!!)
            }
    }

    fun getUserDogOwnerById(userId: String, callbackOK: (DogOwner) -> Unit, callbackERROR: (String) -> Unit){
        dbFirebase.collection("DogOwners").document(userId)
            .get()
            .addOnSuccessListener { docWalker ->
                (docWalker.data?.get("userRef") as DocumentReference)
                    .get()
                    .addOnSuccessListener { docUser ->
                        val dogOwner = DogOwner(
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
                            userRef = docUser.id,
                            dogsRef = null // se les atribuye m??s adelante
                        )
                        Log.d("UserManager","Loaded user from Firebase")
                        callbackOK(dogOwner)

                    }
                    .addOnFailureListener {
                        callbackERROR(it.message!!)
                    }
            }
            .addOnFailureListener {
                callbackERROR(it.message!!)
            }
    }

    fun updateCertificate(userId:String, status:Boolean){
        dbFirebase.collection("DogWalkers").document(userId)
            .update(mapOf(
                "certificateAccepted" to status
            ))
            .addOnSuccessListener {
                Toast.makeText(context, "Certificado actualizado correctamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error al actualizar el certificado", Toast.LENGTH_SHORT).show()
                Log.e("CertificadoFragment", it.message!!)
            }
    }

    fun existUsername(userId:String, myUsername:String, callbackOK: (Boolean) -> Unit){
        dbFirebase.collection("Users").document(userId)
            .get()
            .addOnSuccessListener { doc ->
                if(doc.exists()){
                    val username = doc.data?.get("username").toString()
                    dbFirebase.collection("Users")
                        .whereEqualTo("username", myUsername)
                        .get()
                        .addOnSuccessListener { res ->
                            if(res.size() >= 1) {
                                if(myUsername == username){
                                    callbackOK(false)
                                }else{
                                    callbackOK(true)
                                }
                            }else{
                                callbackOK(false)
                            }
                        }
                        .addOnFailureListener {
                            println(it.message!!)
                        }
                }
            }
            .addOnFailureListener{
                println(it.message!!)
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

    fun updateUserAuth(userId: String, newUsername: String){

        dbFirebase.collection("Users").document(userId)
            .update(mapOf(
                "username" to newUsername,
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

    fun updatePhoto(userId: String, photo: String){
        dbFirebase.collection("Users").document(userId)
            .update(mapOf(
                "photoUrl" to photo,
            ))
            .addOnSuccessListener {
                Toast.makeText(context, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error al actualizar sus datos", Toast.LENGTH_SHORT).show()
                Log.e("PerfilFragment", it.message!!)
            }
    }

    fun getPhotoUrl(userId: String, callbackOK: (String) -> Unit){
        dbFirebase.collection("Users").document(userId)
            .get()
            .addOnSuccessListener { doc ->
                println(userId)
                if(doc.exists()) {
                    callbackOK(doc.data?.get("photoUrl").toString())
                }
            }
            .addOnFailureListener {
                Log.e("PerfilFragment", it.message!!)
            }
    }


    fun updateDogWalkerScore(dogWalkerId: String, callbackOK: (Float) -> Unit, callbackError: (String) -> Unit){
        dbFirebase.collection("Reviews")
            .whereEqualTo("dogWalkerRef", dbFirebase.collection("DogWalkers").document(dogWalkerId))
            .get()
            .addOnSuccessListener { result ->
                var sum = 0f
                var count = 0
                for(document in result){
                    sum += document.data["score"].toString().toFloat()
                    count++
                    if(count == result.size()){
                        val newProm = sum/count
                        dbFirebase.collection("DogWalkers")
                            .document(dogWalkerId)
                            .update("score", newProm)
                            .addOnSuccessListener {
                                callbackOK(newProm)
                            }
                            .addOnFailureListener {
                                callbackError(it.message!!)
                            }
                    }
                }
            }
            .addOnFailureListener {
                callbackError(it.message!!)
            }
    }

    private fun md5(input:String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }
}