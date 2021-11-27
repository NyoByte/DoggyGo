package pe.edu.ulima.doggygo

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import pe.edu.ulima.doggygo.fragments.*
import pe.edu.ulima.doggygo.model.DogWalker
import pe.edu.ulima.doggygo.model.User

class DogWalkerMainActivity : AppCompatActivity() {

    private val fragments = mutableListOf<Fragment>()
    private var nameFragment:String? = null
    private lateinit var dlaMain: DrawerLayout
    private lateinit var user: User

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

        // Fragments
        val intentData:Bundle? = intent.getBundleExtra("data")
        nameFragment = intentData?.getString("namefragment")

        fragments.add(PerfilFragment())
        fragments.add(CertificadoFragment())
        fragments.add(CalificacionFragment())
        fragments.add(AnuncioWalkerFragment())
        fragments.add(PaseoWalkerFragment())
        fragments.add(ContratosFragment())
        fragments.add(ConfiguracionFragment())

        nviMain.setNavigationItemSelectedListener {
            changeFragment(it)
            onOptionsItemSelected(it)

            it.setChecked(true)
            dlaMain.closeDrawers()
            true
        }

        user = intent.getSerializableExtra("user") as DogWalker

        val mainFragment = fragments[3]
        val args = Bundle().apply {
            this.putSerializable("user",user)
        }
        mainFragment.arguments = args

        val ft = supportFragmentManager.beginTransaction()
        ft.add(R.id.flContent, mainFragment)
        ft.commit()

    }

    private fun changeFragment(menuItem:MenuItem){
        when (menuItem.itemId) {
            R.id.mainProfile -> {
                changeProfileFragment()
            }
            R.id.mainCertificate -> {
                changeCertificateFragment()
            }
            R.id.mainReview -> {
                changeReviewFragment()
            }
            R.id.mainAdvertisement -> {
                changeAdvertisementFragment()
            }
            R.id.mainWalk -> {
                changeWalkFragment()
            }
            R.id.mainContract -> {
                changeContractFragment()
            }
            R.id.mainUser -> {
                changeUserFragment()
            }
            R.id.mainConfig -> {
                changeConfigFragment()
            }
        }
    }

    private fun changeProfileFragment() {
        val fragment = fragments[0]
        val args = Bundle().apply {
            this.putSerializable("user",user)
        }
        fragment.arguments = args
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.flContent,fragment)
        ft.commit()
    }

    private fun changeCertificateFragment() {
        val fragment = fragments[1]
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.flContent,fragment)
        ft.commit()
    }

    private fun changeReviewFragment() {
        TODO("Not yet implemented")
    }

    private fun changeAdvertisementFragment() {
        val fragment = fragments[2]
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.flContent,fragment)
        ft.commit()
    }

    private fun changeWalkFragment() {
        TODO("Not yet implemented")
    }

    private fun changeContractFragment() {
        val fragment = fragments[5]
        val args = Bundle().apply {
            this.putSerializable("user",user)
        }
        fragment.arguments = args
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.flContent,fragment)
        ft.commit()
    }

    private fun changeUserFragment() {
        TODO("Not yet implemented")
    }

    private fun changeConfigFragment() {
        TODO("Not yet implemented")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            dlaMain.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

}