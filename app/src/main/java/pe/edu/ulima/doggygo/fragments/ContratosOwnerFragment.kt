package pe.edu.ulima.doggygo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import pe.edu.ulima.doggygo.R
import pe.edu.ulima.doggygo.adapter.ContractListAdapter
import pe.edu.ulima.doggygo.adapter.ContractOwnerListAdapter
import pe.edu.ulima.doggygo.manager.ContractManager
import pe.edu.ulima.doggygo.model.Contract
import pe.edu.ulima.doggygo.model.DogOwner

class ContratosOwnerFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_contratos_owner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = arguments?.getSerializable("user") as DogOwner

        val contractList = mutableListOf<Contract>()

        val rviContract = view.findViewById<RecyclerView>(R.id.rviContractsOwner)
        val contractManager = ContractManager(requireActivity().applicationContext)

        contractManager.getContractsForOwner(user.userRef!!,false, {cList: List<Contract> ->
            contractList.addAll(cList)
            println("Obtenidos contratos para el id ${user.userRef}, resultados: ${cList.size}")
            rviContract.adapter = ContractOwnerListAdapter(this, contractList){ contract: Contract ->
                contractManager.deleteContract(contract.id, {
                    Toast.makeText(activity, "Contrato cancelado correctamente", Toast.LENGTH_SHORT).show()

                    val pos = contractList.indexOf(contract)
                    contractList.removeAt(pos)
                    rviContract.adapter?.notifyItemRemoved(pos)
                }, {
                    Toast.makeText(activity, "Error al cancelar el contrato", Toast.LENGTH_SHORT).show()
                    println(it)
                })
            }
        }, {error ->
            println(error)
            Toast.makeText(activity, "Error obtaining contracts", Toast.LENGTH_SHORT).show()
        })
    }
}