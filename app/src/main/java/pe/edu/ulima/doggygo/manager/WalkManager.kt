package pe.edu.ulima.doggygo.manager

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
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
            val contractWalkList = contractList.filter { contract -> contract.walkRef != null }
            contractWalkList.forEach { contract ->
                contract.walkRef!!.get()
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
                            duration = contract.duration.toString().toInt(),
                            pee = walk.data?.get("pee").toString().toBoolean(),
                            poo = walk.data?.get("poo").toString().toBoolean(),
                            walkStarted = walk.data?.get("walkStarted") as? Timestamp,
                            walkEnded = walk.data?.get("walkEnded") as? Timestamp,
                            listLatLng = walk.data?.get("listLatLng") as? MutableList<GeoPoint>,
                            dogWalkerId = null
                        )
                        walkList.add(newWalk)
                        if(walkList.size == contractWalkList.size){
                            callbackOK(walkList)
                        }
                    }
                    .addOnFailureListener {
                        callbackError(it.message!!)
                    }
            }
        },{
            callbackError(it)
        })
    }

    fun getWalksForOwner(userId: String, callbackOK: (List<Walk>) -> Unit, callbackError: (String) -> Unit){
        val walkList = mutableListOf<Walk>()
        contractManager.getContractsForOwner(userId, true,{ contractList ->
            val contractWalkList = contractList.filter { contract -> contract.walkRef != null }
            contractWalkList.forEach { contract ->
                contract.walkRef!!.get()
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
                            duration = contract.duration.toString().toInt(),
                            pee = walk.data?.get("pee").toString().toBoolean(),
                            poo = walk.data?.get("poo").toString().toBoolean(),
                            walkStarted = walk.data?.get("walkStarted") as? Timestamp,
                            walkEnded = walk.data?.get("walkEnded") as? Timestamp,
                            listLatLng = walk.data?.get("listLatLng") as? MutableList<GeoPoint>,
                            dogWalkerId = contract.dogWalkerId
                        )
                        walkList.add(newWalk)
                        if(walkList.size == contractWalkList.size){
                            callbackOK(walkList)
                        }
                    }
                    .addOnFailureListener {
                        callbackError(it.message!!)
                    }
            }
        },{
            callbackError(it)
        })
    }

    fun updateWalk(walkId: String, walk: Walk){
        dbFirebase.collection("Walks").document(walkId)
            .update(mapOf(
                "pee" to walk.pee,
                "poo" to walk.poo,
                "walkStarted" to walk.walkStarted,
                "walkEnded" to walk.walkEnded,
                "listLatLng" to walk.listLatLng
            ))
            .addOnSuccessListener {
                Toast.makeText(context, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error al actualizar", Toast.LENGTH_SHORT).show()
                Log.e("PaseoDetalleWaFragment", it.message!!)
            }
    }

    fun deleteWalk(walkId: String, callbackOK: () -> Unit, callbackError: (String) -> Unit){
        dbFirebase.collection("Contracts")
            .whereEqualTo("walkRef", dbFirebase.collection("Walks").document(walkId))
            .get()
            .addOnSuccessListener { res ->
                var count = 0
                for(document in res){
                    document.reference
                        .delete()
                        .addOnSuccessListener {
                            count++
                            if(count >= res.size()){
                                dbFirebase.collection("Walks")
                                    .document(walkId)
                                    .delete()
                                    .addOnSuccessListener {
                                        callbackOK()
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
    }
}