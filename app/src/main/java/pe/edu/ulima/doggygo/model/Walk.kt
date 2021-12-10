package pe.edu.ulima.doggygo.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import java.io.Serializable

class Walk(
    var pee: Boolean,
    var poo: Boolean,
    var walkStarted: Timestamp?,
    var walkEnded: Timestamp?,
    var listLatLng: MutableList<GeoPoint>?,
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
    note: String,
    duration: Int,
    dogWalkerId: String?
) : Contract(
    id, dogOwnerFullName, dogOwnerDistrict, date, time, dogName,
    dogBreed,dogActivityLevel, dogAge,dogWeight, photoUrl, note, null, null, duration, dogWalkerId
), Serializable {
}