package pe.edu.ulima.doggygo.model

import java.io.Serializable

 class User (
    val id: String?,
    val firstName: String,
    val lastName: String,
    val gender: String,
    val telf: String,
    val age: String,
    val email: String,
    val doctype: String,
    val nroDoc: String,
    val province: String,
    val district: String,
    val address: String,
    val type: String,
    val username: String,
    val createdDate: String
): Serializable{}