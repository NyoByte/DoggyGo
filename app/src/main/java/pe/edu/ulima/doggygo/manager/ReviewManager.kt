package pe.edu.ulima.doggygo.manager

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pe.edu.ulima.doggygo.model.Review
import java.text.SimpleDateFormat
import java.util.*

class ReviewManager(private val context: Context) {
    private val dbFirebase = Firebase.firestore

    fun getReviewsById(userId: String, callbackOK: (List<Review>) -> Unit, callbackERROR: (String) -> Unit){
        dbFirebase.collection("Reviews")
            .whereEqualTo("dogWalkerRef", dbFirebase.collection("DogWalkers").document(userId))
            .get()
            .addOnSuccessListener { res ->
                val reviews = arrayListOf<Review>()
                for (document in res){
                    (document.data["dogOwnerRef"] as DocumentReference)
                        .get()
                        .addOnSuccessListener { docOwner ->
                            (docOwner.data?.get("userRef") as DocumentReference)
                                .get()
                                .addOnSuccessListener { docUser ->
                                    val review = Review(
                                        id = document.id,
                                        score = document.data["score"].toString().toFloat(),
                                        comment = document.data["comment"].toString(),
                                        date = document.data["date"].toString(),
                                        dogWalker_id = (document.data["dogWalkerRef"] as DocumentReference).id,
                                        dogOwner_id = (document.data["dogOwnerRef"] as DocumentReference).id,
                                        photoUrl = docUser.data?.get("photoUrl").toString(),
                                        fullName = docUser.data?.get("firstName").toString() + " " + docUser.data?.get("lastName").toString(),
                                        district = docUser.data?.get("district").toString()
                                    )
                                    reviews.add(review)
                                    if(reviews.size >= res.size()){
                                        Log.d("ReviewManager", "Loaded reviews from Firebase")
                                        callbackOK(reviews)
                                    }
                                }
                                .addOnFailureListener {
                                    callbackERROR(it.message!!)
                                }
                        }
                        .addOnFailureListener{
                            callbackERROR(it.message!!)
                        }
                }
            }
            .addOnFailureListener {
                callbackERROR(it.message!!)
            }
    }

    fun createReview(dogOwnerId: String, dogWalkerId: String, score: Float, comment: String, callbackOK: () -> Unit, callbackError: (String) -> Unit){
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val currentDate = sdf.format(Date())

        val newReview = hashMapOf<String, Any>(
            "comment" to comment,
            "date" to currentDate,
            "dogOwnerRef" to dbFirebase.collection("DogOwners").document(dogOwnerId),
            "dogWalkerRef" to dbFirebase.collection("DogWalkers").document(dogWalkerId),
            "score" to score
        )
        dbFirebase.collection("Reviews")
            .add(newReview)
            .addOnSuccessListener {
                callbackOK()
            }
            .addOnFailureListener {
                callbackError(it.message!!)
            }
    }
}