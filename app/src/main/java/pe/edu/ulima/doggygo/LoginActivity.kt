package pe.edu.ulima.doggygo

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout

class LoginActivity: AppCompatActivity() {

    private var tinUsername: TextInputLayout? = null
    private var tinPassword: TextInputLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        tinUsername = findViewById(R.id.tinName)
        tinPassword = findViewById(R.id.tinPassword)

        findViewById<TextView>(R.id.tviSignup).setOnClickListener {
            //Cambiar a Signup
            val intent: Intent = Intent(this, SignupActivity::class.java)
            startActivityForResult(intent,10)
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
}