package com.android.filmlibrary.view

import android.content.res.TypedArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.filmlibrary.R
import com.android.filmlibrary.databinding.ItemMovieBinding
import com.android.filmlibrary.model.data.Movie

class MoviesAdapter : RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder>() {

    private lateinit var pictures: TypedArray
    private var movies: List<Movie> = listOf()

    private var onClickMovie: (Movie) -> Unit = {}
    fun setOnClickView(onClickMovie: (Movie) -> Unit) {
        this.onClickMovie = onClickMovie
    }

    fun setMoviesData(data: List<Movie>) {
        movies = data
        notifyDataSetChanged()
    }

    inner class MoviesViewHolder(private val binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie) {
            with(binding) {
                image.setImageResource(movie.image)
                name.text = movie.name
                date.text = movie.date
                rating.text = (movie.rating.toString())
                image.setOnClickListener {
                    onClickMovie(movie)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviesViewHolder {
        pictures = parent.context.resources.obtainTypedArray(R.array.images)
        return MoviesViewHolder(
            ItemMovieBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MoviesViewHolder, position: Int) {
        movies[position].image = pictures.getResourceId(position, 0)
        holder.bind(movies[position])
    }

    override fun getItemCount() = movies.size

}