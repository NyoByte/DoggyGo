package pe.edu.ulima.doggygo.model

class Review (
    val id: String,
    val score: Float,
    val comment: String,
    val date: String,
    val dogWalker_id: String,
    val dogOwner_id: String,
    val photoUrl: String,
    val fullName: String,
    val district: String
)