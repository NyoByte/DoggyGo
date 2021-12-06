package pe.edu.ulima.doggygo.model

class DogWalker(
    val desc: String,
    val active: Boolean,
    val price: Int,
    val score: Int,
    val userRef: String,
    val numWalks: Int,
    val numReviews: Int,
    val certificateAccepted: Boolean,
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
    createdDate: String
): User(id, firstName, lastName, gender, telf, age,
    email, docType, nroDoc, province, district, address,
    type, username, password, createdDate) {
}