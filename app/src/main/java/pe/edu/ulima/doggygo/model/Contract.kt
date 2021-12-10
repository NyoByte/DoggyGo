package pe.edu.ulima.doggygo.model

import com.google.firebase.firestore.DocumentReference

open class Contract (
    val id: String,
    val dogOwnerFullName: String,
    val dogOwnerDistrict: String,
    val date: String,
    val time: Int,
    val dogName: String,
    val dogBreed: String,
    val dogActivityLevel: String,
    val dogAge: Int,
    val dogWeight: Int,
    val photoUrl: String,
    val note: String,
    val walkRef: DocumentReference?,
    val price: Int?,
    val duration: Int,
    val dogWalkerId: String?
){}