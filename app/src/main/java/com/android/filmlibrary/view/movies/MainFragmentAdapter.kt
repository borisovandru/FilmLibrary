package com.android.filmlibrary.view.movies

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
import com.android.filmlibrary.R
import com.android.filmlibrary.databinding.ItemCategoryBinding
import com.android.filmlibrary.model.data.Category
import com.android.filmlibrary.model.data.Movie
import com.android.filmlibrary.model.data.MoviesByCategories
import kotlin.collections.ArrayList

class MainFragmentAdapter : RecyclerView.Adapter<MainFragmentAdapter.MyViewHolder>() {

    //-------------------------------------------------------------------
    private var onMovieClickListener: (Int) -> Unit = {}

    fun setOnMovieClickListener(onMovieClickListener: (Int) -> Unit) {
        this.onMovieClickListener = onMovieClickListener
    }

    //-------------------------------------------------------------------
    private var onCategoryClickListener: (Int) -> Unit = {}

    fun setOnCategoryClickListener(onCategoryClickListener: (Int) -> Unit) {
        this.onCategoryClickListener = onCategoryClickListener
    }

    //-------------------------------------------------------------------

    private var moviesByCategory: List<MoviesByCategories> = ArrayList()

    fun fillMoviesByCategory(moviesByCategory: List<MoviesByCategories>) {
        Log.v(
            "Debug1",
            "CategoriesAdapter fillMoviesByCategory moviesByCategory.size=" + moviesByCategory.size
        )
        this.moviesByCategory = moviesByCategory
        notifyDataSetChanged()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        Log.v("Debug1", "CategoriesAdapter onCreateViewHolder")

        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return MyViewHolder(binding, parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Log.v("Debug1", "CategoriesAdapter onBindViewHolder")
        holder.categoryName.text = moviesByCategory[position].category.title
        moviesByCategory[position].category.id.let{
            holder.categoryId = moviesByCategory[position].category.id
        }
        if (position != -1) {
            holder.setData(moviesByCategory[position])
            holder.categoryId = moviesByCategory[position].category.id
        }
    }

    override fun getItemCount(): Int {
        return moviesByCategory.size
    }

    inner class MyViewHolder(private val binding: ItemCategoryBinding, parent: ViewGroup) :
        RecyclerView.ViewHolder(binding.root) {

        private val parentLoc: ViewGroup = parent
        val categoryName: TextView = itemView.findViewById(R.id.categoryName)
        var categoryId: Int = 0
        lateinit var category: Category
        var movies: List<Movie> = ArrayList()


        init {
            Log.v("Debug1", "CategoriesAdapter MyViewHolder init categoryId=$categoryId")
            binding.root.setOnClickListener {
                onCategoryClickListener(categoryId)
            }
        }

        fun setData(moviesByCategory: MoviesByCategories) {
            Log.v("Debug1", "CategoriesAdapter MyViewHolder setData")
            val linearLayoutItemCategory: LinearLayout = binding.linearLayoutItemCategory
            val linearLayoutIntoScrollView: LinearLayout = binding.linearLayoutIntoScrollView

            linearLayoutIntoScrollView.removeAllViews()
            for (movie in moviesByCategory.movies) {
                val viewItemMovie: View = LayoutInflater.from(parentLoc.context)
                    .inflate(R.layout.item_movie, linearLayoutItemCategory, false)

                val titleMovie = viewItemMovie.findViewById<TextView>(R.id.movieTitle)
                val yearMovie = viewItemMovie.findViewById<TextView>(R.id.movieYear)
                val catMovie = viewItemMovie.findViewById<TextView>(R.id.movieCat)
                val posterMovie = viewItemMovie.findViewById<ImageView>(R.id.moviePoster)

                Log.v("Debug1", "CategoriesAdapter MyViewHolder setData for movie.id" + movie.id)

                posterMovie.setOnClickListener {
                    onMovieClickListener(movie.id)
                }

                movie.posterUrl.let{
                    Glide.with(viewItemMovie.context)
                        .load(Constant.BASE_IMAGE_URL + Constant.IMAGE_POSTER_SIZE_1 + movie.posterUrl)
                        .into(posterMovie)
                }

                titleMovie.text = movie.title
                yearMovie.text = movie.year.toString()
                catMovie.text = movie.category.title

                linearLayoutIntoScrollView.addView(viewItemMovie)
            }
        }
    }
}