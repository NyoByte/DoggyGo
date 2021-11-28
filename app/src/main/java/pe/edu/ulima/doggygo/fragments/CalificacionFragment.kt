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
import pe.edu.ulima.doggygo.adapter.ReviewListAdapter
import pe.edu.ulima.doggygo.manager.ContractManager
import pe.edu.ulima.doggygo.manager.ReviewManager
import pe.edu.ulima.doggygo.model.Contract
import pe.edu.ulima.doggygo.model.DogWalker
import pe.edu.ulima.doggygo.model.Review

class CalificacionFragment: Fragment() {
    private var user: DogWalker? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_calificacion, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user = arguments?.getSerializable("user") as DogWalker

        var reviewList = mutableListOf<Review>()
        val rviReviews = view.findViewById<RecyclerView>(R.id.rviReviews)
        val reviewManager = ReviewManager(requireActivity().applicationContext)

        reviewManager.getReviewsById(user!!.id!!,{rList: List<Review> ->
            reviewList.addAll(rList)
            rviReviews.adapter = ReviewListAdapter(this, reviewList){ review: Review ->
                Log.d("CalificacionFragment", review.id)
            }
        }, {error ->
            println(error)
            Toast.makeText(activity, "Error obtaining reviews", Toast.LENGTH_SHORT).show()
        })
    }
}