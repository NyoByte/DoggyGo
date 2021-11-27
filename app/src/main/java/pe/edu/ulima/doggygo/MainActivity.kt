package pe.edu.ulima.doggygo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import pe.edu.ulima.doggygo.fragments.AnuncioFragment
import pe.edu.ulima.doggygo.model.User

class MainActivity : AppCompatActivity() {

    private val fragments = mutableListOf<Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fragments.add(AnuncioFragment())

        val user = intent.getSerializableExtra("user") as User

        val ft = supportFragmentManager.beginTransaction()

        if(user.type == "Paseador"){
            ft.add(R.id.flaMainContent, fragments[0])
        }
        ft.commit()
    }
}