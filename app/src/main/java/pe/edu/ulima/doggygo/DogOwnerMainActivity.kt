package pe.edu.ulima.doggygo

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import pe.edu.ulima.doggygo.fragments.*
import pe.edu.ulima.doggygo.manager.UserManager
import pe.edu.ulima.doggygo.model.DogOwner
import pe.edu.ulima.doggygo.model.DogWalker
import pe.edu.ulima.doggygo.model.Pet

class DogOwnerMainActivity : AppCompatActivity(),
    MascotasFragment.Actions,
    EditMascotaFragment.Actions {

    private val fragments = mutableListOf<Fragment>()
    private var nameFragment:String? = null
    private lateinit var dlaMain: DrawerLayout
    private lateinit var user: DogOwner
    private lateinit var userManager: UserManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dog_owner_main)
        setTitle("Dueño")
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

        fragments.add(PerfilFragment("dogOwner"))
        fragments.add(MascotasFragment())
        fragments.add(AnunciosOwnerFragment())
        fragments.add(PaseoOwnerFragment())
        fragments.add(ContratosFragment()) //dos tipos
        fragments.add(UsuarioFragment("dogOwner"))
        fragments.add(ConfiguracionFragment())
        //Fuera del menú
        fragments.add(EditMascotaFragment()) // 7


        nviMain.setNavigationItemSelectedListener {
            changeFragment(it)
            onOptionsItemSelected(it)

            it.setChecked(true)
            dlaMain.closeDrawers()
            true
        }

        userManager = UserManager(this)
        user = intent.getSerializableExtra("user") as DogOwner
        val mainFragment = fragments[1]
        val args = Bundle().apply {
            this.putSerializable("user",user)
        }
        mainFragment.arguments = args
        val ft = supportFragmentManager.beginTransaction()
        ft.add(R.id.flDogOwnerContent, mainFragment)
        ft.commit()
    }
    private fun changeFragment(menuItem: MenuItem){
        when(menuItem.itemId){
            R.id.mainProfile -> {
                changeProfileFragment()
            }
            R.id.mainPet -> {
                changePetFragment()
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

    override fun onAddClicked() {
        val fragment = fragments[7]
        val args = Bundle().apply {
            this.putSerializable("user",user)
        }
        fragment.arguments = args

        supportFragmentManager.beginTransaction().apply {
            this.replace(R.id.flDogOwnerContent,fragment)
            this.commit()
        }
    }

    override fun onEditPetClicked(pet: Pet) {
        val fragment = fragments[7]
        val args = Bundle().apply {
            this.putSerializable("user", user)
            this.putSerializable("pet",pet)
        }
        fragment.arguments = args

        supportFragmentManager.beginTransaction().apply {
            this.replace(R.id.flDogOwnerContent,fragment)
            this.commit()
        }
    }

    override fun onPetCreated() {
        val fragment = fragments[1]
        val args = Bundle().apply {
            this.putSerializable("user",user)
        }
        fragment.arguments = args

        supportFragmentManager.beginTransaction().apply {
            this.replace(R.id.flDogOwnerContent,fragment)
            this.commit()
        }
    }
    private fun changeProfileFragment() {
        val fragment = fragments[0]
        userManager.getUserDogOwnerById(user.id!!, { dogOwner: DogOwner ->
            user = dogOwner
            val args = Bundle().apply {
                this.putSerializable("user",user)
            }
            fragment.arguments = args
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.flDogOwnerContent,fragment)
            ft.commit()
        }, {error ->
            println(error)
            Toast.makeText(this, "Error obtaining user", Toast.LENGTH_SHORT).show()
        })
    }

    private fun changePetFragment(){
        val fragment = fragments[1]
        val args = Bundle().apply {
            this.putSerializable("user", user)
        }
        fragment.arguments = args
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.flDogOwnerContent,fragment)
        ft.commit()
    }

    private fun changeAdvertisementFragment() {
        val fragment = fragments[2]
        val args = Bundle().apply {
            this.putSerializable("user",user)
        }
        fragment.arguments = args
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.flDogOwnerContent,fragment)
        ft.commit()
    }

    private fun changeWalkFragment() {
        val fragment = fragments[3]
        val args = Bundle().apply {
            this.putSerializable("user",user)
        }
        fragment.arguments = args
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.flDogOwnerContent,fragment)
        ft.commit()
    }

    private fun changeContractFragment() {
        // TODO("Not yet implemented")
        Toast.makeText(this, "Not yet implemented", Toast.LENGTH_SHORT).show()
    }

    private fun changeUserFragment() {
        val fragment = fragments[5]
        val args = Bundle().apply {
            this.putSerializable("user",user)
        }
        fragment.arguments = args
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.flDogOwnerContent,fragment)
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
}