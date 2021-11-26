package pe.edu.ulima.doggygo

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputLayout

class SignupActivity: AppCompatActivity() {
    private var tinGenders: TextInputLayout? = null
    private var cgrUserType: ChipGroup? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        tinGenders = findViewById(R.id.tinGenders)
        //Llenar el select de géneros
        val genders = listOf("Masculino", "Femenino", "No especificar")
        val gendersAdapter = ArrayAdapter(this,R.layout.list_genders, genders)
        (tinGenders?.editText as? AutoCompleteTextView)?.setAdapter(gendersAdapter)

        cgrUserType = findViewById(R.id.cgrUserType)

        findViewById<Button>(R.id.btnRegistrar).setOnClickListener {
            //Como obtener el valor del ChipGroup
            /*
            *   val tempSelect = cgrUserType?.getChildAt(0) as Chip
            *   val userType:String
            *   if(tempSelect.isChecked){
            *       userType="Paseador"
            *   } else{
            *        userType="Dueño"
            *    }
            *    println("--->>>>>"+userType)
            */
            //Guardar en la DB

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

        findViewById<TextView>(R.id.tviSignin).setOnClickListener {
            //Volver al login
            setResult(RESULT_CANCELED)
            finish()
        }
    }
}