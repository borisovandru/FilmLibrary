package com.android.filmlibrary.view.search

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.android.filmlibrary.Constant
import com.android.filmlibrary.R
import com.android.filmlibrary.databinding.ItemMovieBinding
import com.android.filmlibrary.model.data.Movie
import com.bumptech.glide.Glide
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SearchFragmentAdapter : RecyclerView.Adapter<SearchFragmentAdapter.MyViewHolder>() {

    private var onMovieClickListener: (Int) -> Unit = {}

    fun setOnMovieClickListener(onMovieClickListener: (Int) -> Unit) {
        this.onMovieClickListener = onMovieClickListener
    }

    private var moviesBySearch = listOf<Movie>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SearchFragmentAdapter.MyViewHolder {
        Log.v("Debug1", "SearchFragmentAdapter onCreateViewHolder")

        val binding = ItemMovieBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return MyViewHolder(binding, parent)
    }

    fun fillMoviesBySearch(moviesBySearch: List<Movie>) {
        Log.v(
            "Debug1",
            "SearchFragmentAdapter fillMoviesByTrend moviesBySearch.size=" + moviesBySearch.size
        )
        this.moviesBySearch = moviesBySearch
        notifyDataSetChanged()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: SearchFragmentAdapter.MyViewHolder, position: Int) {
        Log.v("Debug1", "SearchFragmentAdapter onBindViewHolder")

        val movie = moviesBySearch[position]
        holder.movieTitle.text = movie.title

        if (movie.genres.isNotEmpty()) {
            holder.movieCat.text = movie.genres.first().title
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

        holder.setData(movie.posterUrl)
        holder.movieId = movie.id
    }

    override fun getItemCount(): Int {
        return moviesBySearch.size
    }

    inner class MyViewHolder(binding: ItemMovieBinding, parent: ViewGroup) :
        RecyclerView.ViewHolder(binding.root) {

        val movieTitle: TextView = binding.movieTitle
        val movieYear: TextView = binding.movieYear
        val movieCat: TextView = binding.movieCat
        private var moviePoster: ImageView = binding.moviePoster
        var movieId: Int = 0
        private val parentLoc: ViewGroup = parent

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