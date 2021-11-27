package pe.edu.ulima.doggygo.model

data class Contract (
    val id: String,
    val dogOwnerFullName: String,
    val dogOwnerDistrict: String,
    val date: String,
    val time: Int,
    val dogName: String,
    val dogBreed: String,
    val dogActivityLeve: String,
    val dogAge: Int,
    val dogWeight: Int
)