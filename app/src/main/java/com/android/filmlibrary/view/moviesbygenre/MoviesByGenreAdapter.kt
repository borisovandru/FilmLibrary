package com.android.filmlibrary.view.moviesbygenre

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.android.filmlibrary.Constant
import com.android.filmlibrary.R
import com.android.filmlibrary.databinding.ItemMovieBinding
import com.android.filmlibrary.model.data.Genre
import com.android.filmlibrary.model.data.MoviesByGenre
import com.android.filmlibrary.model.data.MoviesList
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MoviesByGenreAdapter : RecyclerView.Adapter<MoviesByGenreAdapter.MyViewHolder>() {

    private var onMovieClickListener: (Int) -> Unit = {}

    fun setOnMovieClickListener(onMovieClickListener: (Int) -> Unit) {
        this.onMovieClickListener = onMovieClickListener
    }

    private var moviesByGenre: MoviesByGenre =
        MoviesByGenre(Genre(), MoviesList(mutableListOf(), 0, 0))
    private lateinit var genre: Genre

    fun fillMovies(moviesByGenre: MoviesByGenre) {
        Log.v(
            "Debug1",
            "MoviesByCategoryAdapter fillMovies movies.movies.size=" + moviesByGenre.movies.results.size
        )
        this.moviesByGenre = moviesByGenre
        this.genre = moviesByGenre.genre
        notifyDataSetChanged()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        Log.v("Debug1", "MoviesByCategoryAdapter onCreateViewHolder")

        val binding = ItemMovieBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MyViewHolder(binding, parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Log.v("Debug1", "MoviesByCategoryAdapter onBindViewHolder")
        val movie = moviesByGenre.movies.results[position]
        holder.movieTitle.text = movie.title

        if (movie.genre_ids.isNotEmpty()) {
            holder.movieCat.text = genre.name
        }

        if (movie.dateRelease != "") {
            val localDate = LocalDate.parse(
                movie.dateRelease,
                DateTimeFormatter.ofPattern("yyyy-MM-dd")
            )
            val formatter = DateTimeFormatter.ofPattern("yyyy")
            val formattedDate = localDate.format(formatter)
            holder.movieYear.text = formattedDate
        } else {
            holder.movieYear.text = ""
        }

        movie.posterUrl?.let { holder.setData(it) }

        holder.movieId = movie.id
    }

    override fun getItemCount(): Int {
        return moviesByGenre.movies.results.size
    }

    inner class MyViewHolder(binding: ItemMovieBinding, parent: ViewGroup) :
        RecyclerView.ViewHolder(binding.root) {

        val movieTitle: TextView = binding.movieTitle
        val movieYear: TextView = binding.movieYear
        private var moviePoster: ImageView = binding.moviePoster
        var movieId: Int = 0
        private val parentLoc: ViewGroup = parent
        var movieCat: TextView = binding.movieCat

        fun setData(posterURL: String) {
            posterURL.let {
                if (posterURL != "" && posterURL != "-") {
                    Glide.with(parentLoc.context)
                        .load(Constant.BASE_IMAGE_URL + Constant.IMAGE_POSTER_SIZE_1 + posterURL)
                        .into(moviePoster)
                } else {
                    moviePoster.setImageResource(R.drawable.empty_poster)
                }
            }
        }

        init {
            binding.root.setOnClickListener {
                onMovieClickListener(movieId)
            }
        }
    }
}