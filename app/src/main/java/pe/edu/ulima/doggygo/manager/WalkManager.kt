package pe.edu.ulima.doggygo.manager

import android.content.Context
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pe.edu.ulima.doggygo.model.Contract
import pe.edu.ulima.doggygo.model.Walk

class WalkManager(private val context: Context) {
    private val dbFirebase = Firebase.firestore
    private val contractManager = ContractManager(context)

    fun getWalks(userId: String, callbackOK: (List<Walk>) -> Unit, callbackError: (String) -> Unit){
        val walkList = mutableListOf<Walk>()
        contractManager.getContracts(userId, true, { contractList ->
            contractList.forEach { contract ->
                if(contract.walkRef != null){
                    contract.walkRef.get()
                        .addOnSuccessListener { walk ->
                            val newWalk = Walk(
                                id = walk.id,
                                note = contract.note,
                                dogOwnerFullName = contract.dogOwnerFullName,
                                dogOwnerDistrict = contract.dogOwnerDistrict,
                                dogWeight = contract.dogWeight,
                                dogName = contract.dogName,
                                dogBreed = contract.dogBreed,
                                dogAge = contract.dogAge,
                                date = contract.date,
                                dogActivityLevel = contract.dogActivityLevel,
                                photoUrl = contract.photoUrl,
                                time = contract.time,
                                pee = walk.data?.get("pee").toString().toBoolean(),
                                poo = walk.data?.get("poo").toString().toBoolean()
                            )
                            walkList.add(newWalk)
                            if(walkList.size == contractList.size){
                                callbackOK(walkList)
                            }
                        }
                        .addOnFailureListener {
                            callbackError(it.message!!)
                        }
                }
            }
        },{
            callbackError(it)
        })
    }
}