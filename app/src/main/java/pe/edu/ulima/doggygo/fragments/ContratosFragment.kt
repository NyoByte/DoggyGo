package pe.edu.ulima.doggygo.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pe.edu.ulima.doggygo.R
import pe.edu.ulima.doggygo.adapter.ContractListAdapter
import pe.edu.ulima.doggygo.manager.ContractManager
import pe.edu.ulima.doggygo.model.Contract
import pe.edu.ulima.doggygo.model.DogWalker
import pe.edu.ulima.doggygo.model.User

class ContratosFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_contratos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = arguments?.getSerializable("user") as DogWalker

        var contractList = mutableListOf<Contract>()
        var currentPage = 0

        val rviContract = view.findViewById<RecyclerView>(R.id.rviContracts)
        val contractManager = ContractManager(requireActivity().applicationContext)

        contractManager.getContracts(user.id!!,false,{cList: List<Contract> ->
            contractList.addAll(cList)
            rviContract.adapter = ContractListAdapter(this, contractList){ contract: Contract, state: String ->
                contractManager.updateContractState(contract.id, state, {
                    Toast.makeText(activity, "Contrato actualizado correctamente", Toast.LENGTH_SHORT).show()

                    val pos = contractList.indexOf(contract)
                    contractList.removeAt(pos)
                    rviContract.adapter?.notifyItemRemoved(pos)
                }, {
                    Toast.makeText(activity, "Error al actualizar el contrato", Toast.LENGTH_SHORT).show()
                    println(it)
                })
            }
        }, {error ->
            println(error)
            Toast.makeText(activity, "Error obtaining contracts", Toast.LENGTH_SHORT).show()
        })
    }
}