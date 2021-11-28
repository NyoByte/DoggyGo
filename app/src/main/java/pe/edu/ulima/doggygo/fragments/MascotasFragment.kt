package pe.edu.ulima.doggygo.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import pe.edu.ulima.doggygo.R

class MascotasFragment: Fragment() {
    interface Actions{
        fun onAddClicked()
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

        view.findViewById<FloatingActionButton>(R.id.fabAddPet).setOnClickListener {
            listener?.onAddClicked()
        }
    }
}