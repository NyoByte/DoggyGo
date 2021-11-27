package pe.edu.ulima.doggygo.adapter

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import pe.edu.ulima.doggygo.model.Contract

class ContractListAdapter(private val fragment: Fragment,
                          private val contractList: MutableList<Contract>,
                          private val listener: (Contract) -> Unit): RecyclerView.Adapter<ContractListAdapter.ViewHolder>() {
    class ViewHolder(view: View, val contractList: List<Contract>,
                     val listener: (Contract) -> Unit): RecyclerView.ViewHolder(view), View.OnClickListener {

        init{
            view.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            TODO("Not yet implemented")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
}