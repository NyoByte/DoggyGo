package pe.edu.ulima.doggygo.model

import java.io.Serializable

data class Pet (
    val id: String?,
    val name: String,
    val sex: String,
    val age: Int,
    val activityLevel: String,
    val breed: String,
    val weight: Int,
    val note: String,
    val photoUrl: String
): Serializable{}