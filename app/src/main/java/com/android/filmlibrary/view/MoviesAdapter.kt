package com.android.filmlibrary.view

import android.content.res.Resources
import android.content.res.TypedArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.android.filmlibrary.R
import com.android.filmlibrary.model.data.Movie

class MoviesAdapter(private val movies: List<Movie>, resources: Resources) :
    RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder>() {

    private val pictures: TypedArray = resources.obtainTypedArray(R.array.images)

    class MoviesViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        private val images: AppCompatImageView = item.findViewById(R.id.image)
        private val name: TextView = item.findViewById(R.id.name)
        private val date: TextView = item.findViewById(R.id.date)
        private val rating: TextView = item.findViewById(R.id.rating)

        fun bind(movie: Movie, imageId: Int) {

            images.setImageResource(imageId)
            name.text = movie.name
            date.text = movie.date
            ("" + movie.rating).also { rating.text = it }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
        return MoviesViewHolder(view)
    }

    override fun onBindViewHolder(holder: MoviesViewHolder, position: Int) {
        holder.bind(movies[position], pictures.getResourceId(position, 0))
    }

    override fun getItemCount() = movies.size

}