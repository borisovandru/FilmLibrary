package com.android.filmlibrary.presentation.search

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
import com.android.filmlibrary.data.model.Movie
import com.android.filmlibrary.data.model.MoviesList
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SearchFragmentAdapter : RecyclerView.Adapter<SearchFragmentAdapter.MyViewHolder>() {

    private var onMovieClickListener: (Movie) -> Unit = {}

    fun setOnMovieClickListener(onMovieClickListener: (Movie) -> Unit) {
        this.onMovieClickListener = onMovieClickListener
    }

    private var moviesBySearch = MoviesList(
        listOf(),
        0,
        0
    )

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SearchFragmentAdapter.MyViewHolder {

        val binding = ItemMovieBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return MyViewHolder(binding, parent)
    }

    fun fillMoviesBySearch(moviesBySearches: MoviesList) {
        this.moviesBySearch = moviesBySearches
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: SearchFragmentAdapter.MyViewHolder, position: Int) {

        val movie = moviesBySearch.results[position]
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

        holder.setData(movie.posterUrl)
        holder.movieId = movie.id
        holder.movie = movie
    }

    override fun getItemCount(): Int {
        return moviesBySearch.results.size
    }

    inner class MyViewHolder(binding: ItemMovieBinding, parent: ViewGroup) :
        RecyclerView.ViewHolder(binding.root) {

        val movieTitle: TextView = binding.movieTitle
        val movieYear: TextView = binding.movieYear
        private var moviePoster: ImageView = binding.moviePoster
        var movie: Movie? = null
        var movieId: Int = movie?.id ?: 0
        private val parentLoc: ViewGroup = parent

        fun setData(posterURL: String?) {

            posterURL?.let {
                if (posterURL != "" && posterURL != "-") {
                    Glide.with(parentLoc.context)
                        .load(Constant.BASE_IMAGE_URL + Constant.IMAGE_POSTER_SIZE_1 + posterURL)
                        .into(moviePoster)
                } else {
                    moviePoster.setImageResource(EMPTY_POSTER)
                }
            } ?: run {
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