package com.android.filmlibrary.view.moviesbygenre

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.android.filmlibrary.Constant
import com.android.filmlibrary.Constant.EMPTY_POSTER
import com.android.filmlibrary.Constant.FORMATED_STRING_DATE_TMDB
import com.android.filmlibrary.Constant.FORMATED_STRING_YEAR
import com.android.filmlibrary.databinding.ItemMovieBinding
import com.android.filmlibrary.model.data.Genre
import com.android.filmlibrary.model.data.Movie
import com.android.filmlibrary.model.data.MoviesByGenre
import com.android.filmlibrary.model.data.MoviesList
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MoviesByGenreAdapter : RecyclerView.Adapter<MoviesByGenreAdapter.MyViewHolder>() {

    private var onMovieClickListener: (Movie) -> Unit = {}

    fun setOnMovieClickListener(onMovieClickListener: (Movie) -> Unit) {
        this.onMovieClickListener = onMovieClickListener
    }

    private var moviesByGenre: MoviesByGenre =
        MoviesByGenre(Genre(), MoviesList(mutableListOf(), 0, 0))
    private lateinit var genre: Genre

    @SuppressLint("NotifyDataSetChanged")
    fun fillMovies(moviesByGenre: MoviesByGenre) {
        this.moviesByGenre = moviesByGenre
        this.genre = moviesByGenre.genre
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val binding = ItemMovieBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MyViewHolder(binding, parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val movie = moviesByGenre.movies.results[position]
        holder.movieTitle.text = movie.title

        if (movie.dateRelease != "") {
            val localDate = LocalDate.parse(
                movie.dateRelease,
                DateTimeFormatter.ofPattern(FORMATED_STRING_DATE_TMDB)
            )
            val formatter = DateTimeFormatter.ofPattern(FORMATED_STRING_YEAR)
            val formattedDate = localDate.format(formatter)
            holder.movieYear.text = formattedDate
        } else {
            holder.movieYear.text = ""
        }

        movie.posterUrl?.let { holder.setData(it) }
        holder.movieId = movie.id
        holder.movie = movie
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
        var movie: Movie? = null

        fun setData(posterURL: String) {

            if (posterURL != "" && posterURL != "-") {
                Glide.with(parentLoc.context)
                    .load(Constant.BASE_IMAGE_URL + Constant.IMAGE_POSTER_SIZE_1 + posterURL)
                    .into(moviePoster)
            } else {
                moviePoster.setImageResource(EMPTY_POSTER)
            }

        }

        init {
            binding.root.setOnClickListener {
                movie?.let { it1 -> onMovieClickListener(it1) }
            }
        }
    }
}