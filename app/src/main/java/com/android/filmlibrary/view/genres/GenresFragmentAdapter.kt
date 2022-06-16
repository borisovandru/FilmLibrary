package com.android.filmlibrary.view.genres

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.android.filmlibrary.Constant
import com.android.filmlibrary.Constant.EMPTY_POSTER
import com.android.filmlibrary.Constant.FORMATED_STRING_DATE_TMDB
import com.android.filmlibrary.Constant.FORMATED_STRING_YEAR
import com.android.filmlibrary.R
import com.android.filmlibrary.databinding.ItemGenreBinding
import com.android.filmlibrary.model.data.Genre
import com.android.filmlibrary.model.data.Movie
import com.android.filmlibrary.model.data.MoviesByGenre
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList

class GenresFragmentAdapter : RecyclerView.Adapter<GenresFragmentAdapter.MyViewHolder>() {

    private var onMovieClickListener: (Movie) -> Unit = {}

    fun setOnMovieClickListener(onMovieClickListener: (Movie) -> Unit) {
        this.onMovieClickListener = onMovieClickListener
    }

    private var onCategoryClickListener: (Int) -> Unit = {}

    fun setOnGenresClickListener(onCategoryClickListener: (Int) -> Unit) {
        this.onCategoryClickListener = onCategoryClickListener
    }

    private var moviesByCategory: List<MoviesByGenre> = ArrayList()
    private var genres: List<Genre> = ArrayList()

    @SuppressLint("NotifyDataSetChanged")
    fun fillMoviesByGenres(moviesByCategory: List<MoviesByGenre>, genres: List<Genre>) {
        Log.v(
            "Debug1",
            "CategoriesAdapter fillMoviesByCategory moviesByCategory.size=" + moviesByCategory.size
        )
        this.moviesByCategory = moviesByCategory
        this.genres = genres
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        Log.v("Debug1", "CategoriesAdapter onCreateViewHolder")

        val binding = ItemGenreBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return MyViewHolder(binding, parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Log.v("Debug1", "CategoriesAdapter onBindViewHolder")
        holder.categoryName.text = moviesByCategory[position].genre.name
        moviesByCategory[position].genre.id.let {
            holder.categoryId = moviesByCategory[position].genre.id
        }
        if (position != -1) {
            holder.setData(moviesByCategory[position])
            holder.categoryId = moviesByCategory[position].genre.id
        }
    }

    override fun getItemCount(): Int {
        return moviesByCategory.size
    }

    inner class MyViewHolder(private val binding: ItemGenreBinding, parent: ViewGroup) :
        RecyclerView.ViewHolder(binding.root) {

        private val parentLoc: ViewGroup = parent
        val categoryName: TextView = itemView.findViewById(R.id.categoryName)
        var categoryId: Int = 0
        lateinit var genre: Genre

        init {
            Log.v("Debug1", "CategoriesAdapter MyViewHolder init categoryId=$categoryId")
            binding.root.setOnClickListener {
                onCategoryClickListener(categoryId)
            }
        }

        fun setData(moviesByCategory: MoviesByGenre) {
            Log.v("Debug1", "CategoriesAdapter MyViewHolder setData")
            val linearLayoutItemCategory: LinearLayout = binding.linearLayoutItemCategory
            val linearLayoutIntoScrollView: LinearLayout = binding.linearLayoutIntoScrollView

            linearLayoutIntoScrollView.removeAllViews()
            moviesByCategory.movies.results.forEach { movie ->
                val viewItemMovie: View = LayoutInflater.from(parentLoc.context)
                    .inflate(R.layout.item_movie, linearLayoutItemCategory, false)

                val titleMovie = viewItemMovie.findViewById<TextView>(R.id.movieTitle)
                val yearMovie = viewItemMovie.findViewById<TextView>(R.id.movieYear)
                val ratedMovie = viewItemMovie.findViewById<TextView>(R.id.rated)
                val posterMovie = viewItemMovie.findViewById<ImageView>(R.id.moviePoster)

                Log.v("Debug1", "CategoriesAdapter MyViewHolder setData for movie.id" + movie.id)

                posterMovie.setOnClickListener {
                    onMovieClickListener(movie)
                }

                if (movie.posterUrl != "" && movie.posterUrl != "-" && movie.posterUrl != null) {
                    Glide.with(viewItemMovie.context)
                        .load(Constant.BASE_IMAGE_URL + Constant.IMAGE_POSTER_SIZE_1 + movie.posterUrl)
                        .into(posterMovie)
                } else {
                    posterMovie.setImageResource(EMPTY_POSTER)
                }

                ratedMovie.text = movie.voteAverage.toString()
                titleMovie.text = movie.title
                movie.dateRelease?.let {
                    if (movie.dateRelease != "") {
                        val localDate = LocalDate.parse(
                            movie.dateRelease,
                            DateTimeFormatter.ofPattern(FORMATED_STRING_DATE_TMDB)
                        )
                        val formatter = DateTimeFormatter.ofPattern(FORMATED_STRING_YEAR)
                        val formattedDate = localDate.format(formatter)
                        yearMovie.text = formattedDate
                    } else {
                        yearMovie.text = ""
                    }
                }

                linearLayoutIntoScrollView.addView(viewItemMovie)
            }
        }
    }
}