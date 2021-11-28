package pe.edu.ulima.doggygo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import pe.edu.ulima.doggygo.R
import pe.edu.ulima.doggygo.model.Contract
import pe.edu.ulima.doggygo.model.Review

class ReviewListAdapter(private val fragment: Fragment,
                        private val reviewList: MutableList<Review>,
                        private val listener: (Review) -> Unit): RecyclerView.Adapter<ReviewListAdapter.ViewHolder>() {

    class ViewHolder(view: View,
                    val reviewList: List<Review>,
                    val listener: (Review) -> Unit): RecyclerView.ViewHolder(view),
                    View.OnClickListener {

        //val iviPhoto: ImageView = view.findViewById(R.id.iviPhoto)
        val tviFullName: TextView = view.findViewById(R.id.tviFullName)
        val tviDisctrict: TextView = view.findViewById(R.id.tviDisctrict)
        val tviComment: TextView = view.findViewById(R.id.tviComment)
        val tviDate: TextView = view.findViewById(R.id.tviDate)
        val rbaScore: RatingBar = view.findViewById(R.id.rbaScore)

        init{
            view.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            listener(reviewList[adapterPosition])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_calificacion, parent, false)
        return ViewHolder(view, reviewList, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tviFullName.text = reviewList[position].fullName
        holder.tviDisctrict.text = reviewList[position].district
        holder.tviComment.text = reviewList[position].comment
        holder.tviDate.text = reviewList[position].date
        holder.rbaScore.rating = reviewList[position].score
        //Glide.with(fragment).load(reviewList[position].photoUrl).into(holder.iviPhoto)
    }

    override fun getItemCount(): Int {
        return reviewList.size
    }
}