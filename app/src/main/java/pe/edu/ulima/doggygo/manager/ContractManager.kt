package pe.edu.ulima.doggygo.manager

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pe.edu.ulima.doggygo.model.Contract

class ContractManager(private val context: Context) {

    private val dbFirebase = Firebase.firestore

    fun getContracts(callbackOK: (List<Contract>) -> Unit, callbackError: (String) -> Unit){
        dbFirebase.collection("Contracts")
            .get()
            .addOnSuccessListener { res ->
                val contracts = arrayListOf<Contract>()
                for(document in res){
                    (document.data["dogOwnerReference"] as DocumentReference)
                        .get()
                        .addOnSuccessListener { docOwner ->
                            (document.data["dogReference"] as DocumentReference)
                                .get()
                                .addOnSuccessListener { docDog ->
                                    val contract = Contract(
                                        id = document.id,
                                        date = document.data["date"].toString(),
                                        time = document.data["time"].toString().toInt(),
                                        dogOwnerDistrict = docOwner.data?.get("district").toString()!!,
                                        dogOwnerFullName = docOwner.data?.get("firstName").toString()!! + " " + docOwner.data?.get("lastName").toString()!!,
                                        dogActivityLevel = docDog.data?.get("activityLevel")?.toString()!!,
                                        dogAge = docDog.data?.get("age").toString()!!.toInt()!!,
                                        dogBreed = docDog.data?.get("breed").toString()!!,
                                        dogName = docDog.data?.get("name").toString()!!,
                                        dogWeight = docDog.data?.get("weight").toString()!!.toInt()!!
                                    )
                                    contracts.add(contract)
                                    if(contracts.size >= res.size()){
                                        Log.d("ContractManager", "Loaded contracts from Firebase")
                                        println()
                                        callbackOK(contracts)
                                    }
                                }
                                .addOnFailureListener {
                                    callbackError(it.message!!)
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