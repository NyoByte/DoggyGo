package pe.edu.ulima.doggygo

import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import pe.edu.ulima.doggygo.fragments.*
import pe.edu.ulima.doggygo.manager.UserManager
import pe.edu.ulima.doggygo.model.DogWalker
import pe.edu.ulima.doggygo.model.Walk

class DogWalkerMainActivity : AppCompatActivity(),
    PerfilFragment.Actions,
    UsuarioFragment.Actions,
    PaseoWalkerFragment.Actions{

    private val fragments = mutableListOf<Fragment>()
    private var nameFragment:String? = null
    private lateinit var nviMain: NavigationView
    private lateinit var dlaMain: DrawerLayout
    private lateinit var user: DogWalker
    private lateinit var userManager: UserManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dog_walker_main)
        setTitle("Paseador")
        // Configuracion menu Hamburguesa
        val actionBar = supportActionBar
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_hamburger_24)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        // Configuracion NavigationView
        nviMain = findViewById(R.id.nviMain)
        dlaMain = findViewById(R.id.dlaMain)

        // Fragments
        val intentData:Bundle? = intent.getBundleExtra("data")
        nameFragment = intentData?.getString("namefragment")

        fragments.add(PerfilFragment("dogWalker"))
        fragments.add(CertificadoFragment())
        fragments.add(CalificacionFragment())
        fragments.add(AnuncioWalkerFragment())
        fragments.add(PaseoWalkerFragment())
        fragments.add(ContratosFragment())
        fragments.add(UsuarioFragment("dogWalker"))
        fragments.add(ConfiguracionFragment())
        //Fuera del menú
        fragments.add(PaseoDetalleWalkerFragment()) //8

        nviMain.setNavigationItemSelectedListener {
            changeFragment(it)
            onOptionsItemSelected(it)

            it.setChecked(true)
            dlaMain.closeDrawers()
            true
        }
        userManager = UserManager(this)
        user = intent.getSerializableExtra("user") as DogWalker

        nviMain.getHeaderView(0).findViewById<TextView>(R.id.main_header_tviUsername).setText(user.username)
        nviMain.getHeaderView(0).findViewById<TextView>(R.id.main_header_tviEmail).setText(user.email)
        nviMain.setCheckedItem(R.id.mainAdvertisement)

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
        userManager.getUserDogWalkerById(user.id!!, { dogWalker: DogWalker ->
            user = dogWalker
            val args = Bundle().apply {
                this.putSerializable("user",user)
            }
            fragment.arguments = args
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.flContent,fragment)
            ft.commit()
        }, {error ->
            println(error)
            Toast.makeText(this, "Error obtaining user", Toast.LENGTH_SHORT).show()
        })
    }

    private fun changeCertificateFragment() {
        val fragment = fragments[1]
        userManager.getUserDogWalkerById(user.id!!, { dogWalker: DogWalker ->
            user = dogWalker
            val args = Bundle().apply {
                this.putSerializable("user",user)
            }
            fragment.arguments = args
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.flContent,fragment)
            ft.commit()
        }, {error ->
            println(error)
            Toast.makeText(this, "Error obtaining user", Toast.LENGTH_SHORT).show()
        })
    }

    private fun changeReviewFragment() {
        val fragment = fragments[2]
        userManager.getUserDogWalkerById(user.id!!, { dogWalker: DogWalker ->
            user = dogWalker
            val args = Bundle().apply {
                this.putSerializable("user",user)
            }
            fragment.arguments = args
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.flContent,fragment)
            ft.commit()
        }, {error ->
            println(error)
            Toast.makeText(this, "Error obtaining user", Toast.LENGTH_SHORT).show()
        })
    }

    private fun changeAdvertisementFragment() {
        val fragment = fragments[3]
        userManager.getUserDogWalkerById(user.id!!, { dogWalker: DogWalker ->
            user = dogWalker
            val args = Bundle().apply {
                this.putSerializable("user",user)
            }
            fragment.arguments = args
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.flContent,fragment)
            ft.commit()
        }, {error ->
            println(error)
            Toast.makeText(this, "Error obtaining user", Toast.LENGTH_SHORT).show()
        })
    }

    private fun changeWalkFragment() {
        val fragment = fragments[4]
        val args = Bundle().apply {
            this.putSerializable("user",user)
        }
        fragment.arguments = args
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.flContent,fragment)
        ft.commit()
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
        val fragment = fragments[6]
        val args = Bundle().apply {
            this.putSerializable("user",user)
        }
        fragment.arguments = args
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.flContent,fragment)
        ft.commit()
    }

    private fun changeConfigFragment() {
        // TODO("Not yet implemented")
        Toast.makeText(this, "Not yet implemented", Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            dlaMain.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    // Listeners
    override fun onSaveUser_Perfil(email: String) {
        nviMain.getHeaderView(0).findViewById<TextView>(R.id.main_header_tviEmail).setText(email)
    }

    override fun onSaveUser_Usuario(username:String){
        nviMain.getHeaderView(0).findViewById<TextView>(R.id.main_header_tviUsername).setText(username)
    }

    override fun onDetailsWalkerClicked(walk: Walk) {
        val fragment = fragments[8]
        val args = Bundle().apply {
            this.putSerializable("user",user)
            this.putSerializable("walk", walk)
        }
        fragment.arguments = args
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.flContent,fragment)
        ft.commit()
    }

}