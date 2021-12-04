package pe.edu.ulima.doggygo.manager

import android.content.Context
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pe.edu.ulima.doggygo.model.DogWalker

class AnnouncementsManager(private val context: Context) {

    private val dbFirebase = Firebase.firestore

    fun getAnnouncements(callbackOk: (List<DogWalker>) -> Unit, callbackError: (String) -> Unit){
        dbFirebase.collection("DogWalkers")
            .get()
            .addOnSuccessListener { res ->
                val dogWalkerList = arrayListOf<DogWalker>()
                for(document in res){
                    (document.data["userRef"] as DocumentReference)
                        .get()
                        .addOnSuccessListener { docUser ->
                            val dogWalker = DogWalker(
                                id = document.id,
                                age = docUser.data?.get("age").toString(),
                                score = document.data["score"].toString().toInt(),
                                desc = document.data["desc"].toString(),
                                active = document.data["desc"].toString().toBoolean(),
                                username = docUser.data?.get("username").toString(),
                                district = docUser.data?.get("district").toString(),
                                createdDate = docUser.data?.get("createdDate").toString(),
                                gender = docUser.data?.get("gender").toString(),
                                telf = docUser.data?.get("telf").toString(),
                                email = docUser.data?.get("email").toString(),
                                nroDoc = docUser.data?.get("nroDoc").toString(),
                                address = docUser.data?.get("address").toString(),
                                type = docUser.data?.get("type").toString(),
                                firstName = docUser.data?.get("firstName").toString(),
                                lastName = docUser.data?.get("lastName").toString(),
                                password = null,
                                docType = docUser.data?.get("docType").toString(),
                                price = document.data["price"].toString().toInt(),
                                province = docUser.data?.get("province").toString(),
                                userRef = (document.data["userRef"] as DocumentReference).id,
                                numReviews = document.data["numReviews"].toString().toInt(),
                                numWalks = document.data["numWalks"].toString().toInt(),
                            )
                            dogWalkerList.add(dogWalker)
                            if(dogWalkerList.size >= res.size()){
                                callbackOk(dogWalkerList)
                            }
                        }
                        .addOnFailureListener {
                            callbackError(it.message!!)
                        }
                }
            }
            .addOnFailureListener {
                callbackError(it.message!!)
            }
    }
}