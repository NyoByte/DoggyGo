package pe.edu.ulima.doggygo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import pe.edu.ulima.doggygo.fragments.EditMascotaFragment
import pe.edu.ulima.doggygo.fragments.MascotasFragment
import pe.edu.ulima.doggygo.model.DogOwner
import pe.edu.ulima.doggygo.model.DogWalker

class DogOwnerMainActivity : AppCompatActivity(), MascotasFragment.Actions, EditMascotaFragment.Actions {

    private val fragments = mutableListOf<Fragment>()
    private lateinit var user: DogOwner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dog_owner_main)

        user = intent.getSerializableExtra("user") as DogOwner

        fragments.add(MascotasFragment())
        fragments.add(EditMascotaFragment())

        val ft = supportFragmentManager.beginTransaction()
        ft.add(R.id.flDogOwnerContent, fragments[0])
        ft.commit()
    }

    override fun onAddClicked() {
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

    override fun onPetCreated() {
        val fragment = fragments[0]
        val args = Bundle().apply {
            this.putSerializable("user",user)
        }
        fragment.arguments = args

        supportFragmentManager.beginTransaction().apply {
            this.replace(R.id.flDogOwnerContent,fragment)
            this.commit()
        }
    }
}