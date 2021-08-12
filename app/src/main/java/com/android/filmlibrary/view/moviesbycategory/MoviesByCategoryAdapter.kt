package com.android.filmlibrary.view.moviesbycategory

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.android.filmlibrary.Constant
import com.android.filmlibrary.databinding.ItemMovieBinding
import com.android.filmlibrary.model.data.MoviesByCategories

class MoviesByCategoryAdapter : RecyclerView.Adapter<MoviesByCategoryAdapter.MyViewHolder>() {

    private var onMovieClickListener: (Int) -> Unit = {}

    fun setOnMovieClickListener(onMovieClickListener: (Int) -> Unit) {
        this.onMovieClickListener = onMovieClickListener
    }

    private lateinit var moviesByCategories: MoviesByCategories

    fun fillMovies(moviesByCategories: MoviesByCategories) {
        Log.v(
            "Debug1",
            "MoviesByCategoryAdapter fillMovies movies.movies.size=" + moviesByCategories.movies.size
        )
        this.moviesByCategories = moviesByCategories
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
        val movie = moviesByCategories.movies[position]
        holder.movieTitle.text = movie.title
        holder.movieCat.text = movie.category.title
        holder.movieYear.text = movie.year.toString()
        holder.setData(movie.posterUrl)
        //holder.moviePoster = movie.posterUrl

        holder.movieId = movie.id
    }

    override fun getItemCount(): Int {
        return moviesByCategories.movies.size
    }

    inner class MyViewHolder(binding: ItemMovieBinding, parent: ViewGroup) :
        RecyclerView.ViewHolder(binding.root) {

        val movieTitle: TextView = binding.movieTitle
        val movieYear: TextView = binding.movieYear
        val movieCat: TextView = binding.movieCat
        private var moviePoster: ImageView = binding.moviePoster
        var movieId: Int = 0
        private val parentLoc: ViewGroup = parent

        fun setData(posterURL: String){
            posterURL.let{
                Glide.with(parentLoc.context)
                    .load(Constant.BASE_IMAGE_URL + Constant.IMAGE_POSTER_SIZE_1 + posterURL)
                    .into(moviePoster)
            }
        }

        init {
            binding.root.setOnClickListener {
                onMovieClickListener(movieId)
            }
        }

    }


}