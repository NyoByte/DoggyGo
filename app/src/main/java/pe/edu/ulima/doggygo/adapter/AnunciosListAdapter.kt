package pe.edu.ulima.doggygo.adapter

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import pe.edu.ulima.doggygo.model.Contract
import pe.edu.ulima.doggygo.model.DogWalker

class AnunciosListAdapter(private val fragment: Fragment,
                          private val anunciosList: MutableList<DogWalker>,
                          private val listener: (DogWalker, String) -> Unit): RecyclerView.Adapter<AnunciosListAdapter.ViewHolder>() {
    class ViewHolder(view: View, val anunciosList: List<DogWalker>,
                     val listener: (DogWalker, String) -> Unit): RecyclerView.ViewHolder(view), View.OnClickListener {


        init{
            view.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        return anunciosList.size
    }
}