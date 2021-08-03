package com.android.filmlibrary.view

import android.content.res.TypedArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.android.filmlibrary.R
import com.android.filmlibrary.model.data.Movie

class MoviesAdapter(private var onClickMovie: HomeFragment.OnClickMovie?) :
    RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder>() {

    private lateinit var pictures: TypedArray // = resources.obtainTypedArray(R.array.images);
    private var movies: List<Movie> = listOf()
    fun setMoviesData(data: List<Movie>) {
        movies = data
        notifyDataSetChanged()
    }

    inner class MoviesViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        private val images: AppCompatImageView
        private val name: TextView
        private val date: TextView
        private val rating: TextView

        init {
            images = item.findViewById(R.id.image)
            name = item.findViewById(R.id.name)
            date = item.findViewById(R.id.date)
            rating = item.findViewById(R.id.rating)
        }

        fun bind(movie: Movie) {

            images.setImageResource(movie.image)
            name.setText(movie.name)
            date.setText(movie.date)
            rating.setText("" + movie.reting)
            images.setOnClickListener {
                onClickMovie?.onClick(movie)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
        pictures = parent.context.resources.obtainTypedArray(R.array.images)
        return MoviesViewHolder(view)
    }

    override fun onBindViewHolder(holder: MoviesViewHolder, position: Int) {
        movies[position].image = pictures.getResourceId(position, 0)
        holder.bind(movies[position])
    }

    override fun getItemCount() = movies.size

}