package pe.edu.ulima.doggygo.manager

import android.content.Context
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pe.edu.ulima.doggygo.model.Contract

class ContractManager(private val context: Context) {

    private val dbFirebase = Firebase.firestore

    fun getContracts(userId: String,all: Boolean,callbackOK: (List<Contract>) -> Unit, callbackError: (String) -> Unit){
        var query = dbFirebase.collection("Contracts")
            .whereEqualTo("dogWalkerRef", dbFirebase.collection("Users").document(userId))
        if(!all){
            query = query.whereEqualTo("state", "pendiente")
        }
            query.get()
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
                                        dogWeight = docDog.data?.get("weight").toString()!!.toInt()!!,
                                        photoUrl = docDog.data?.get("photo").toString()!!,
                                        note = document.data["notes"].toString(),
                                        walkRef = document.data["walkRef"] as DocumentReference?,
                                        price = null,
                                        duration = document.data["duration"].toString().toInt(),
                                        dogWalkerId = null
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

    fun getContractsForOwner(userId: String, all: Boolean, callbackOK: (List<Contract>) -> Unit, callbackError: (String) -> Unit){
        val dogOwnerReference = dbFirebase.collection("Users").document(userId)
        var query = dbFirebase.collection("Contracts")
           .whereEqualTo("dogOwnerReference", dogOwnerReference)
        if(!all){
            query = query.whereEqualTo("state", "pendiente")
        }
        query.get()
            .addOnSuccessListener { res ->
                val contracts = arrayListOf<Contract>()
                for(document in res){
                    (document.data["dogWalkerRef"] as DocumentReference)
                        .get()
                        .addOnSuccessListener { docWalker ->
                            (document.data["dogReference"] as DocumentReference)
                                .get()
                                .addOnSuccessListener { docDog ->
                                    dbFirebase.collection("DogWalkers")
                                        .whereEqualTo("userRef", docWalker.reference)
                                        .get()
                                        .addOnSuccessListener { result->
                                            val walkerDoc = result.documents[0]
                                            val contract = Contract(
                                                id = document.id,
                                                date = document.data["date"].toString(),
                                                time = document.data["time"].toString().toInt(),
                                                dogOwnerDistrict = docWalker.data?.get("district").toString()!!,
                                                dogOwnerFullName = docWalker.data?.get("firstName").toString()!! + " " + docWalker.data?.get("lastName").toString()!!,
                                                dogActivityLevel = docDog.data?.get("activityLevel")?.toString()!!,
                                                dogAge = docDog.data?.get("age").toString()!!.toInt()!!,
                                                dogBreed = docDog.data?.get("breed").toString()!!,
                                                dogName = docDog.data?.get("name").toString()!!,
                                                dogWeight = docDog.data?.get("weight").toString()!!.toInt()!!,
                                                photoUrl = docDog.data?.get("photo").toString()!!,
                                                note = document.data["notes"].toString(),
                                                walkRef = document.data["walkRef"] as DocumentReference?,
                                                price = walkerDoc.data?.get("price").toString().toInt(),
                                                duration = document.data["duration"].toString().toInt(),
                                                dogWalkerId = walkerDoc.id
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
                        .addOnFailureListener {
                            callbackError(it.message!!)
                        }
                }
            }
            .addOnFailureListener {
                callbackError(it.message!!)
            }
    }

    fun updateContractState(contractId: String, state: String, callbackOK: () -> Unit, callbackError: (String) -> Unit){
        if(state == "aceptado"){
            val listNull:MutableList<GeoPoint>? = null
            val newPaseo = hashMapOf<String, Any?>(
                "poo" to false,
                "pee" to false,
                "walkStarted" to null,
                "walkEnded" to null,
                "listLatLng" to listNull
            )
            dbFirebase.collection("Walks")
                .add(newPaseo)
                .addOnSuccessListener {
                    dbFirebase.collection("Contracts")
                        .document(contractId)
                        .update(hashMapOf(
                            "state" to state,
                            "walkRef" to it
                        ))
                        .addOnSuccessListener {

                        }
                        .addOnFailureListener {
                            callbackError(it.message!!)
                        }
                    callbackOK()
                }
                .addOnFailureListener {
                    callbackError(it.message!!)
                }
        }else{
            dbFirebase.collection("Contracts")
                .document(contractId)
                .update("state", state)
                .addOnSuccessListener {
                    callbackOK
                }
                .addOnFailureListener {
                    callbackError(it.message!!)
                }
        }
    }

    fun deleteContract(contractId: String, callbackOK: () -> Unit, callbackError: (String) -> Unit){
        dbFirebase.collection("Contracts").document(contractId)
            .delete()
            .addOnSuccessListener {
                callbackOK()
            }
            .addOnFailureListener {
                callbackError(it.message!!)
            }
    }
}