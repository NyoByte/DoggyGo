package pe.edu.ulima.doggygo.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pe.edu.ulima.doggygo.R
import pe.edu.ulima.doggygo.adapter.AnunciosListAdapter
import pe.edu.ulima.doggygo.manager.AnnouncementsManager
import pe.edu.ulima.doggygo.manager.PetManager
import pe.edu.ulima.doggygo.model.DogOwner
import pe.edu.ulima.doggygo.model.DogWalker
import pe.edu.ulima.doggygo.model.Pet
import pe.edu.ulima.doggygo.model.User

class AnunciosOwnerFragment: Fragment()  {

    private val dbFirebase = Firebase.firestore
    private lateinit var user: DogOwner

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_anuncios, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user = arguments?.getSerializable("user") as DogOwner

        val anunciosList = mutableListOf<DogWalker>()

        val rviAnuncios = view.findViewById<RecyclerView>(R.id.rviAnuncios)
        val announcementsManager = AnnouncementsManager(requireActivity().applicationContext)

        val petManager = PetManager(requireActivity().applicationContext)

        announcementsManager.getAnnouncements({announcementsList ->
            anunciosList.addAll(announcementsList)
            rviAnuncios.adapter = AnunciosListAdapter(this, anunciosList){dogWalker ->
                val materialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext())

                val customDialog = LayoutInflater.from(requireContext())
                    .inflate(R.layout.dialog_contract_announcement, null, false)

                petManager.getPets(user.id!!, { petList ->
                    launchCustomDialog(materialAlertDialogBuilder, customDialog, petList, dogWalker)
                }, {
                    Toast.makeText(activity, "Ocurrió un error al obtener la lista de mascotas", Toast.LENGTH_LONG).show()
                })
            }
        }, { err ->
            Toast.makeText(activity, "Ocurrió un error al obtener la lista de anuncios", Toast.LENGTH_LONG).show()
            Log.e("AnunciosOwnerFragment", err)
        })

    }

    fun launchCustomDialog(materialAlertDialogBuilder: MaterialAlertDialogBuilder, customAlertDialogView: View, petList: List<Pet>, dogWalker: DogWalker){
        val tinContractDialogPet = customAlertDialogView.findViewById<TextInputLayout>(R.id.tinContractDialogPet)
        val eteContractDialogDate = customAlertDialogView.findViewById<EditText>(R.id.eteContractDialogDate)
        val eteContractDialogTime = customAlertDialogView.findViewById<EditText>(R.id.eteContractDialogTime)
        val eteContractDialogDuration = customAlertDialogView.findViewById<EditText>(R.id.eteContractDialogDuration)
        val eteContractDialogNotes = customAlertDialogView.findViewById<EditText>(R.id.eteContractDialogNotes)

        val petListName = arrayListOf<String>()
        petList.forEach { pet ->
            petListName.add(pet.name)
        }

        val petNameAdapter = ArrayAdapter(requireContext(),R.layout.list_items, petListName)
        (tinContractDialogPet?.editText as? AutoCompleteTextView)?.setAdapter(petNameAdapter)

        materialAlertDialogBuilder.setView(customAlertDialogView)
            .setTitle("Contratar")
            .setMessage("Ingrese los datos del contrato")
            .setPositiveButton("Contratar"){ dialog, _ ->
                val date = eteContractDialogDate.text.toString()
                val time = eteContractDialogTime.text.toString().toInt()
                val duration = eteContractDialogDuration.text.toString().toInt()
                val notes = eteContractDialogNotes.text.toString()
                val selectedPet = petList.find { pet ->
                    pet.name == tinContractDialogPet.editText?.text.toString()
                }
                val dogRef = dbFirebase.collection("Dogs").document(selectedPet?.id!!)
                val dogOwnerRef = dbFirebase.collection("Users").document(user.userRef)
                val dogWalkerRef = dbFirebase.collection("Users").document(dogWalker.userRef)
                val newContract = hashMapOf<String, Any>(
                    "date" to date,
                    "time" to time,
                    "duration" to duration,
                    "dogOwnerReference" to dogOwnerRef,
                    "dogWalkerRef" to dogWalkerRef,
                    "dogReference" to dogRef,
                    "state" to "pendiente",
                    "notes" to notes
                )
                dbFirebase.collection("Contracts")
                    .add(newContract)
                    .addOnSuccessListener {
                        Toast.makeText(requireActivity(), "Contrato creado exitosamente", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireActivity(), "Ocurrió un error al crear el contrato", Toast.LENGTH_SHORT).show()
                        Log.e("AnunciosOwnerFragment", it.message!!)
                    }

                dialog.dismiss()
            }
            .setNegativeButton("Cancelar"){dialog, _ ->
                Toast.makeText(requireActivity(), "Operación cancelada", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .show()
    }
}