package pe.edu.ulima.doggygo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.math.BigInteger
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class SignupActivity: AppCompatActivity() {
    private var tinGenders: TextInputLayout? = null
    private var cgrUserType: ChipGroup? = null

    private val dbFirebase = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        tinGenders = findViewById(R.id.tinGenders)
        //Llenar el select de géneros
        val genders = listOf("Masculino", "Femenino", "No especificar")
        val gendersAdapter = ArrayAdapter(this,R.layout.list_items, genders)
        (tinGenders?.editText as? AutoCompleteTextView)?.setAdapter(gendersAdapter)

        val tinDocType = findViewById<TextInputLayout>(R.id.tinDocumentType)
        //Llenar el select de doc types
        val docTypeList = listOf("DNI", "Carné de extranjería")
        val docTypeAdapter = ArrayAdapter(this,R.layout.list_items, docTypeList)
        (tinDocType?.editText as? AutoCompleteTextView)?.setAdapter(docTypeAdapter)

        val tinProvince = findViewById<TextInputLayout>(R.id.tinProvince)
        //Llenar el select de provincias
        val provinceList = listOf("Lima")
        val provinceAdapter = ArrayAdapter(this,R.layout.list_items, provinceList)
        (tinProvince?.editText as? AutoCompleteTextView)?.setAdapter(provinceAdapter)

        val tinDistrict = findViewById<TextInputLayout>(R.id.tinDistrict)
        //Llenar el select de provincias
        val districtList = listOf("La Molina", "Miraflores", "San Borja", "San Isidro","Surco")
        val districtAdapter = ArrayAdapter(this,R.layout.list_items, districtList)
        (tinDistrict?.editText as? AutoCompleteTextView)?.setAdapter(districtAdapter)

        cgrUserType = findViewById(R.id.cgrUserType)

        findViewById<Button>(R.id.btnIngresar).setOnClickListener {

            val firstName = findViewById<TextInputLayout>(R.id.tinFirstName).editText?.text.toString()
            val lastName = findViewById<TextInputLayout>(R.id.tinLastName).editText?.text.toString()
            val phone = findViewById<TextInputLayout>(R.id.tinTelf).editText?.text.toString()
            val age = findViewById<TextInputLayout>(R.id.tinAge).editText?.text.toString().toInt()
            val email = findViewById<TextInputLayout>(R.id.tinEmail).editText?.text.toString()
            val doc = findViewById<TextInputLayout>(R.id.tinDocument).editText?.text.toString()
            val address = findViewById<TextInputLayout>(R.id.tinAddress).editText?.text.toString()
            val username = findViewById<TextInputLayout>(R.id.tinUsername).editText?.text.toString()
            val password = md5(findViewById<TextInputLayout>(R.id.tinPassword).editText?.text.toString())
            val gender = findViewById<TextInputLayout>(R.id.tinGenders).editText?.text.toString()
            val docType = findViewById<TextInputLayout>(R.id.tinDocumentType).editText?.text.toString()
            val province = findViewById<TextInputLayout>(R.id.tinProvince).editText?.text.toString()
            val district = findViewById<TextInputLayout>(R.id.tinDistrict).editText?.text.toString()
            val userType = findViewById<Chip>(findViewById<ChipGroup>(R.id.cgrUserType).checkedChipId).text.toString()

            val sdf = SimpleDateFormat("dd/MM/yyyy")
            val currentDate = sdf.format(Date())

            val newUser = hashMapOf<String, Any>(
                "firstName" to firstName,
                "lastName" to lastName,
                "gender" to gender,
                "telf" to phone,
                "age" to age,
                "email" to email,
                "docType" to docType,
                "nroDoc" to doc,
                "province" to province,
                "district" to district,
                "address" to address,
                "type" to userType,
                "username" to username,
                "password" to password,
                "createdDate" to currentDate
            )

            dbFirebase.collection("Users")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener { documents ->
                    if(documents.isEmpty){
                        dbFirebase.collection("Users")
                            .add(newUser)
                            .addOnSuccessListener {
                                if(userType == "Paseador"){
                                    val newDogWalker = hashMapOf<String, Any>(
                                        "active" to false,
                                        "desc" to "",
                                        "price" to 0,
                                        "score" to 0,
                                        "userRef" to it
                                    )
                                    dbFirebase.collection("DogWalkers")
                                        .add(newDogWalker)
                                        .addOnSuccessListener {
                                            Toast.makeText(this,"Usuario creado satisfactoriamente", Toast.LENGTH_LONG).show()
                                            //Volver al login con data
                                            val bundle = Bundle()
                                            bundle.putString("username",findViewById<TextInputLayout>(R.id.tinUsername).editText?.text.toString())
                                            bundle.putString("password",findViewById<TextInputLayout>(R.id.tinPassword).editText?.text.toString())
                                            val intent = Intent(this, LoginActivity::class.java).apply{
                                                this.putExtra("data", bundle)
                                            }
                                            setResult(RESULT_OK, intent)
                                            finish()
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(this,"Ocurrió un erro creando al usuario", Toast.LENGTH_LONG).show()
                                            println("Error")
                                            println(it.message!!)
                                        }
                                }else if(userType == "Dueño"){
                                    val newDogOwner = hashMapOf<String, Any>(
                                        //TODO: Definir atributos para DogOwner
                                        "userRef" to it
                                    )
                                    dbFirebase.collection("DogOwners")
                                        .add(newDogOwner)
                                        .addOnSuccessListener {
                                            Toast.makeText(this,"Usuario creado satisfactoriamente", Toast.LENGTH_LONG).show()
                                            //Volver al login con data
                                            val bundle = Bundle()
                                            bundle.putString("username",findViewById<TextInputLayout>(R.id.tinUsername).editText?.text.toString())
                                            bundle.putString("password",findViewById<TextInputLayout>(R.id.tinPassword).editText?.text.toString())
                                            val intent = Intent(this, LoginActivity::class.java).apply{
                                                this.putExtra("data", bundle)
                                            }
                                            setResult(RESULT_OK, intent)
                                            finish()
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(this,"Ocurrió un erro creando al usuario", Toast.LENGTH_LONG).show()
                                            println("Error")
                                            println(it.message!!)
                                        }
                                }
                            }
                            .addOnFailureListener {
                                Toast.makeText(this,"Error creando el usuario", Toast.LENGTH_SHORT).show()
                                println(it)
                            }
                    }else{
                        Toast.makeText(this, "El nombre de usuario ya existe", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Log.e("SignUpActivity", it.message!!)
                    Toast.makeText(this, "Ocurrió un error creando el usuario", Toast.LENGTH_LONG).show()
                }


        }

        findViewById<TextView>(R.id.tviSignin).setOnClickListener {
            //Volver al login
            setResult(RESULT_CANCELED)
            finish()
        }
    }
    fun md5(input:String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }
}