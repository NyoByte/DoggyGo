package pe.edu.ulima.doggygo.model

import com.google.firebase.firestore.DocumentReference

class DogOwner(
    id: String?,
    firstName: String,
    lastName: String,
    gender: String,
    telf: String,
    age: String,
    email: String,
    docType: String,
    nroDoc: String,
    province: String,
    district: String,
    address: String,
    type: String,
    username: String,
    password: String?,
    createdDate: String,
    val userRef: String,
    val dogsRef: List<DocumentReference>?

): User(
    id, firstName, lastName, gender, telf, age, email, docType, nroDoc,
    province, district, address, type, username, password, createdDate
) {
}