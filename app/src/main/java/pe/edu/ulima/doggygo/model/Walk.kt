package pe.edu.ulima.doggygo.model

class Walk(
    val pee: Boolean,
    val poo: Boolean,
    id: String,
    dogOwnerFullName: String,
    dogOwnerDistrict: String,
    date: String,
    time: Int,
    dogName: String,
    dogBreed: String,
    dogActivityLevel: String,
    dogAge: Int,
    dogWeight: Int,
    photoUrl: String,
    note: String
) : Contract(
    id, dogOwnerFullName, dogOwnerDistrict, date, time, dogName,
    dogBreed,dogActivityLevel, dogAge,dogWeight, photoUrl, note, null
) {
}