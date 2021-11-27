package pe.edu.ulima.doggygo

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import pe.edu.ulima.doggygo.fragments.AnuncioFragment
import pe.edu.ulima.doggygo.fragments.ContratosFragment
import pe.edu.ulima.doggygo.model.User

class DogWalkerMainActivity : AppCompatActivity() {

    private val fragments = mutableListOf<Fragment>()
    private lateinit var dlaMain: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dog_walker_main)

        // Configuracion menu Hamburguesa
        val actionBar = supportActionBar
        actionBar?.setHomeAsUpIndicator(android.R.drawable.ic_menu_more)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        // Configuracion NavigationView
        val nviMain = findViewById<NavigationView>(R.id.nviMain)
        dlaMain = findViewById(R.id.dlaMain)

        nviMain.setNavigationItemSelectedListener {
            Log.e("DogWalkerMainActivity","${it.itemId}")
            onOptionsItemSelected(it)
            it.setChecked(true)
            dlaMain.closeDrawers()
            true
        }

        val user = intent.getSerializableExtra("user") as User
        var bundle = Bundle()
        bundle.putSerializable("user", user)

        val anuncioFragment = AnuncioFragment()
        anuncioFragment.arguments = bundle
        fragments.add(anuncioFragment)

        fragments.add(ContratosFragment())

        val ft = supportFragmentManager.beginTransaction()

        ft.add(R.id.flContent, fragments[0])

        ft.commit()

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d("DogWalkerMainActivity","${item.itemId}")
        if (item.itemId == android.R.id.home){
            dlaMain.openDrawer(GravityCompat.START)
        }else if(item.itemId == R.id.mainContract){
            supportFragmentManager.beginTransaction().apply {
                this.replace(R.id.flContent, fragments[1])
                this.commit()
            }
        }

        return super.onOptionsItemSelected(item)
    }

}