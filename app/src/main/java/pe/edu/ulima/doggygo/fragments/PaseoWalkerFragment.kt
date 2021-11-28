package pe.edu.ulima.doggygo.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import pe.edu.ulima.doggygo.R
import pe.edu.ulima.doggygo.adapter.ContractListAdapter
import pe.edu.ulima.doggygo.adapter.WalkListAdapter
import pe.edu.ulima.doggygo.manager.ContractManager
import pe.edu.ulima.doggygo.manager.WalkManager
import pe.edu.ulima.doggygo.model.Contract
import pe.edu.ulima.doggygo.model.DogWalker
import pe.edu.ulima.doggygo.model.Walk

class PaseoWalkerFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_paseos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = arguments?.getSerializable("user") as DogWalker

        var walkList = mutableListOf<Walk>()

        val rviWalks = view.findViewById<RecyclerView>(R.id.rviWalks)
        val walkManager = WalkManager(requireActivity().applicationContext)

        walkManager.getWalks(user.id!!,{wList: List<Walk> ->
            wList.forEach {
                Log.d("WalksManager", it.pee.toString())
            }
            walkList.addAll(wList)
            rviWalks.adapter = WalkListAdapter(this, walkList){ walk: Walk ->
                Log.d("WalksManager", walk.id)
            }
        }, {error ->
            println(error)
            Toast.makeText(activity, "Error obteniendo la lista de paseos", Toast.LENGTH_SHORT).show()
        })
    }

}