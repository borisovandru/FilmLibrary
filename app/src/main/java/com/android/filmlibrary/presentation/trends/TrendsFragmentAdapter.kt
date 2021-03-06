package com.android.filmlibrary.presentation.trends

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.android.filmlibrary.utils.Constant
import com.android.filmlibrary.utils.Constant.EMPTY_POSTER
import com.android.filmlibrary.utils.Constant.FORMATED_STRING_DATE_TMDB
import com.android.filmlibrary.utils.Constant.FORMATED_STRING_YEAR
import com.android.filmlibrary.R
import com.android.filmlibrary.databinding.ItemTrendBinding
import com.android.filmlibrary.data.model.Genre
import com.android.filmlibrary.data.model.Movie
import com.android.filmlibrary.data.model.MoviesByTrend
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TrendsFragmentAdapter : RecyclerView.Adapter<TrendsFragmentAdapter.MyViewHolder>() {

    private var onMovieClickListener: (Movie) -> Unit = {}

    fun setOnMovieClickListener(onMovieClickListener: (Movie) -> Unit) {
        this.onMovieClickListener = onMovieClickListener
    }

    private var moviesByTrend = listOf<MoviesByTrend>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MyViewHolder {
        val binding = ItemTrendBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return MyViewHolder(binding, parent)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun fillMoviesByTrend(moviesByTrend: List<MoviesByTrend>) {

        this.moviesByTrend = moviesByTrend
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.trendName.text = moviesByTrend[position].trend.name
        if (position != -1) {
            holder.setData(moviesByTrend[position])
        }
    }

    override fun getItemCount(): Int {
        return moviesByTrend.size
    }

    inner class MyViewHolder(private val binding: ItemTrendBinding, parent: ViewGroup) :
        RecyclerView.ViewHolder(binding.root) {

        private val parentLoc: ViewGroup = parent
        val trendName: TextView = itemView.findViewById(R.id.trendName)
        lateinit var genre: Genre

        fun setData(moviesByTrend: MoviesByTrend) {
            val linearLayoutItemCategory: LinearLayout = binding.linearLayoutItemTrend
            val linearLayoutIntoScrollView: LinearLayout = binding.linearLayoutIntoScrollViewTrend

            linearLayoutIntoScrollView.removeAllViews()
            moviesByTrend.moviesList.results.forEach { movie ->
                val viewItemMovie: View = LayoutInflater.from(parentLoc.context)
                    .inflate(R.layout.item_movie, linearLayoutItemCategory, false)

                val titleMovie = viewItemMovie.findViewById<TextView>(R.id.movieTitle)
                val yearMovie = viewItemMovie.findViewById<TextView>(R.id.movieYear)
                val posterMovie = viewItemMovie.findViewById<ImageView>(R.id.moviePoster)
                val ratedMovie = viewItemMovie.findViewById<TextView>(R.id.rated)

                posterMovie.setOnClickListener {
                    onMovieClickListener(movie)
                }

                movie.posterUrl.let {

                    if (movie.posterUrl != "" && movie.posterUrl != "-") {
                        Glide.with(viewItemMovie.context)
                            .load(Constant.BASE_IMAGE_URL + Constant.IMAGE_POSTER_SIZE_1 + movie.posterUrl)
                            .into(posterMovie)
                    } else {
                        posterMovie.setImageResource(EMPTY_POSTER)

                    }
                }

                titleMovie.text = movie.title

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

                ratedMovie.text = movie.voteAverage.toString()

                linearLayoutIntoScrollView.addView(viewItemMovie)
            }
        }
    }
}