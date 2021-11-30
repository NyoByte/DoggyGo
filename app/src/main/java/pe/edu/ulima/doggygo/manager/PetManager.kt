package pe.edu.ulima.doggygo.manager

import android.content.Context
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pe.edu.ulima.doggygo.model.Contract
import pe.edu.ulima.doggygo.model.Pet

class PetManager(private val context: Context)  {

    private val dbFirebase = Firebase.firestore

    fun getPets(userId: String, callbackOK: (List<Pet>) -> Unit, callbackError: (String) -> Unit){
        val pets = arrayListOf<Pet>()
        dbFirebase.collection("DogOwners")
            .document(userId)
            .get()
            .addOnSuccessListener { userDoc ->
                val petRefList = userDoc.data?.get("dogsRef") as ArrayList<DocumentReference>
                for(petRef in petRefList){
                    petRef.get()
                        .addOnSuccessListener { petDoc ->
                            val newPet = Pet(
                                id = petDoc.id,
                                name = petDoc.data!!["name"].toString(),
                                sex = petDoc.data!!["gender"].toString(),
                                age = petDoc.data!!["age"].toString().toInt(),
                                activityLevel = petDoc.data!!["activityLevel"].toString(),
                                breed = petDoc.data!!["breed"].toString(),
                                weight = petDoc.data!!["weight"].toString().toInt(),
                                note = petDoc.data!!["note"].toString(),
                                photoUrl = petDoc.data!!["photo"].toString(),
                            )
                            pets.add(newPet)
                            if(pets.size == petRefList.size){
                                callbackOK(pets)
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