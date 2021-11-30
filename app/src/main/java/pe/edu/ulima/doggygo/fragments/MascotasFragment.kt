package pe.edu.ulima.doggygo.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import pe.edu.ulima.doggygo.R
import pe.edu.ulima.doggygo.adapter.PetListAdapter
import pe.edu.ulima.doggygo.manager.PetManager
import pe.edu.ulima.doggygo.model.DogOwner
import pe.edu.ulima.doggygo.model.DogWalker
import pe.edu.ulima.doggygo.model.Pet

class MascotasFragment: Fragment() {
    interface Actions{
        fun onAddClicked()
        fun onEditPetClicked(pet: Pet)
    }

    private lateinit var listener: Actions

    override fun onAttach(context: Context) {
        super.onAttach(context)

        listener = context as Actions
        if(listener == null){
            Log.e("MascotasFragment", "Activity no implementa la interfaz MascotasFragment.Actions")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mascota, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = arguments?.getSerializable("user") as DogOwner

        val petList = mutableListOf<Pet>()

        val rviPet = view.findViewById<RecyclerView>(R.id.rviPets)

        val petManager = PetManager(requireActivity().application)

        petManager.getPets(user.id!!, {
                res: List<Pet> ->
            petList.addAll(res)
            rviPet.adapter = PetListAdapter(this, petList){ pet: Pet ->
                listener.onEditPetClicked(pet)
            }
        }, {
            Toast.makeText(activity, "Error al obtener mascotas", Toast.LENGTH_SHORT).show()
            println(it)
        })

        view.findViewById<FloatingActionButton>(R.id.fabAddPet).setOnClickListener {
            listener?.onAddClicked()
        }
    }
}