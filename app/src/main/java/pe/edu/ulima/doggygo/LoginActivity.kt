package pe.edu.ulima.doggygo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pe.edu.ulima.doggygo.model.DogOwner
import pe.edu.ulima.doggygo.model.DogWalker
import pe.edu.ulima.doggygo.model.User
import java.math.BigInteger
import java.security.MessageDigest

class LoginActivity: AppCompatActivity() {

    private var tinUsername: TextInputLayout? = null
    private var tinPassword: TextInputLayout? = null

    private val dbFirebase = Firebase.firestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        tinUsername = findViewById(R.id.tinUsername)
        tinPassword = findViewById(R.id.tinPassword)

        findViewById<TextView>(R.id.tviSignup).setOnClickListener {
            //Cambiar a Signup
            val intent: Intent = Intent(this, SignupActivity::class.java)
            startActivityForResult(intent,10)
        }

        findViewById<Button>(R.id.btnIngresar).setOnClickListener {
            val username = tinUsername?.editText?.text.toString()
            val password = md5(tinPassword?.editText?.text.toString())

            println("$username, $password")

            dbFirebase.collection("Users")
                .whereEqualTo("username", username)
                .whereEqualTo("password", password)
                .get()
                .addOnSuccessListener { documents ->
                    if(documents.isEmpty){
                        Toast.makeText(this, "Wrong username or password", Toast.LENGTH_SHORT).show()
                    }else{
                        val intent = Intent()
                        val bundle = Bundle()
                        var user: User? = null
                        val document = documents.documents[0]
                        if(document != null){
                            if(document.data!!["type"].toString() == "Paseador"){
                                println("Into->Paseador")
                                dbFirebase.collection("DogWalkers")
                                    .whereEqualTo("userRef", document.reference)
                                    .get()
                                    .addOnSuccessListener { dwDoc ->
                                        val dogWalkerDocument = dwDoc.documents[0]
                                        user = DogWalker(
                                            id = dogWalkerDocument.id,
                                            firstName = document.data!!["firstName"].toString(),
                                            lastName = document.data!!["lastName"].toString(),
                                            type = document.data!!["type"].toString(),
                                            address = document.data!!["address"].toString(),
                                            age = document.data!!["age"].toString(),
                                            nroDoc = document.data!!["nroDoc"].toString(),
                                            docType = document.data!!["docType"].toString(),
                                            email = document.data!!["email"].toString(),
                                            telf = document.data!!["telf"].toString(),
                                            gender = document.data!!["gender"].toString(),
                                            createdDate = document.data!!["createdDate"].toString(),
                                            province = document.data!!["province"].toString(),
                                            district = document.data!!["district"].toString(),
                                            username = document.data!!["username"].toString(),
                                            password = null,
                                            active = dogWalkerDocument.data!!["active"].toString().toBoolean(),
                                            desc = dogWalkerDocument.data!!["desc"].toString(),
                                            price = dogWalkerDocument.data!!["price"].toString().toInt(),
                                            score = dogWalkerDocument.data!!["score"].toString().toInt(),
                                            userRef = (dogWalkerDocument.data!!["userRef"] as DocumentReference).id
                                        )

                                        intent.putExtra("user", user)
                                        intent.setClass(this, DogWalkerMainActivity::class.java)
                                        startActivity(intent)
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(this, "Error obteniendo el usuario", Toast.LENGTH_SHORT).show()
                                        Log.e("LoginActivity", it.message!!)
                                    }
                            }else if(document.data!!["type"].toString() == "Dueño"){
                                println("Into->Dueño")
                                dbFirebase.collection("DogOwners")
                                    .whereEqualTo("userRef", document.reference)
                                    .get()
                                    .addOnSuccessListener { doDoc ->
                                        val dogOwnerDocument = doDoc.documents[0]
                                        user = DogOwner(
                                            id = dogOwnerDocument.id,
                                            firstName = document.data!!["firstName"].toString(),
                                            lastName = document.data!!["lastName"].toString(),
                                            type = document.data!!["type"].toString(),
                                            address = document.data!!["address"].toString(),
                                            age = document.data!!["age"].toString(),
                                            nroDoc = document.data!!["nroDoc"].toString(),
                                            docType = document.data!!["docType"].toString(),
                                            email = document.data!!["email"].toString(),
                                            telf = document.data!!["telf"].toString(),
                                            gender = document.data!!["gender"].toString(),
                                            createdDate = document.data!!["createdDate"].toString(),
                                            province = document.data!!["province"].toString(),
                                            district = document.data!!["district"].toString(),
                                            username = document.data!!["username"].toString(),
                                            password = null,
                                            userRef = (dogOwnerDocument.data!!["userRef"] as DocumentReference).id,
                                            dogsRef = null //(dogOwnerDocument.data?.get("dogsRef") as ArrayList<DocumentReference>).toArray().toList() as List<DocumentReference>
                                        )

                                        intent.putExtra("user", user)
                                        intent.setClass(this, DogOwnerMainActivity::class.java)
                                        startActivity(intent)
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(this, "Error obteniendo el usuario", Toast.LENGTH_SHORT).show()
                                        Log.e("LoginActivity", it.message!!)
                                    }
                            }
                        }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error obteniendo el usuario", Toast.LENGTH_SHORT).show()
                    Log.e("LoginActivity", it.message!!)
                }
        }
    }

    override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 10){
            // Caso signup
            if(resultCode == RESULT_OK){
                // Registro exitoso
                val username = data?.getBundleExtra("data")?.getString("username")
                val password = data?.getBundleExtra("data")?.getString("password")
                tinUsername?.editText?.setText(username)
                tinPassword?.editText?.setText(password)
            }else{
                tinUsername?.editText?.setText("")
                tinPassword?.editText?.setText("")
            }
        }
    }

    fun md5(input:String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }
}