package pe.edu.ulima.doggygo

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView

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
            it.setChecked(true)
            dlaMain.closeDrawers()
            true
        }

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            dlaMain.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

}