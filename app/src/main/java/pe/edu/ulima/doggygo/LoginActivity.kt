package pe.edu.ulima.doggygo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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

        tinUsername = findViewById(R.id.tinAnuncioDesc)
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
                        for(document in documents){
                            user = User(
                                id = document.id,
                                firstName = document.data["firstName"].toString(),
                                lastName = document.data["lastName"].toString(),
                                type = document.data["type"].toString(),
                                address = document.data["address"].toString(),
                                age = document.data["age"].toString(),
                                nroDoc = document.data["nroDoc"].toString(),
                                doctype = document.data["doctype"].toString(),
                                email = document.data["email"].toString(),
                                telf = document.data["telf"].toString(),
                                gender = document.data["gender"].toString(),
                                createdDate = document.data["createdDate"].toString(),
                                province = document.data["province"].toString(),
                                district = document.data["district"].toString(),
                                username = document.data["username"].toString(),
                                active = document.data["active"].toString().toBoolean(),
                                desc = document.data["desc"].toString(),
                                price = document.data["price"].toString().toInt(),
                                score = document.data["score"].toString().toInt()
                            )
                        }
                        if(user != null){
                            intent.putExtra("user", user)
                            if(user.type=="Paseador"){
                                intent.setClass(this, DogWalkerMainActivity::class.java)
                            }
                            startActivity(intent)
                        }
                    }
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