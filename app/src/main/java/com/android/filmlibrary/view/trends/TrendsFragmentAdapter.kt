package com.android.filmlibrary.view.trends

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.filmlibrary.Constant
import com.android.filmlibrary.R
import com.android.filmlibrary.databinding.ItemTrendBinding
import com.android.filmlibrary.model.data.Genre
import com.android.filmlibrary.model.data.Movie
import com.android.filmlibrary.model.data.MoviesByTrend
import com.bumptech.glide.Glide
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TrendsFragmentAdapter : RecyclerView.Adapter<TrendsFragmentAdapter.MyViewHolder>() {


    private var onMovieClickListener: (Int) -> Unit = {}

    fun setOnMovieClickListener(onMovieClickListener: (Int) -> Unit) {
        this.onMovieClickListener = onMovieClickListener
    }


    private var moviesByTrend = listOf<MoviesByTrend>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): TrendsFragmentAdapter.MyViewHolder {
        Log.v("Debug1", "TrendsFragmentAdapter onCreateViewHolder")

        val binding = ItemTrendBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return MyViewHolder(binding, parent)
    }

    fun fillMoviesByTrend(moviesByTrend: List<MoviesByTrend>) {
        Log.v(
            "Debug1",
            "TrendsFragmentAdapter fillMoviesByTrend moviesByTrend.size=" + moviesByTrend.size
        )
        this.moviesByTrend = moviesByTrend
        notifyDataSetChanged()

    }

    override fun onBindViewHolder(holder: TrendsFragmentAdapter.MyViewHolder, position: Int) {
        Log.v("Debug1", "TrendsFragmentAdapter onBindViewHolder")
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
        var movies: List<Movie> = ArrayList()


        fun setData(moviesByTrend: MoviesByTrend) {
            Log.v("Debug1", "TrendsFragmentAdapter MyViewHolder setData")
            val linearLayoutItemCategory: LinearLayout = binding.linearLayoutItemTrend
            val linearLayoutIntoScrollView: LinearLayout = binding.linearLayoutIntoScrollViewTrend

            linearLayoutIntoScrollView.removeAllViews()
            for (movie in moviesByTrend.movies) {
                val viewItemMovie: View = LayoutInflater.from(parentLoc.context)
                    .inflate(R.layout.item_movie, linearLayoutItemCategory, false)

                val titleMovie = viewItemMovie.findViewById<TextView>(R.id.movieTitle)
                val yearMovie = viewItemMovie.findViewById<TextView>(R.id.movieYear)
                val catMovie = viewItemMovie.findViewById<TextView>(R.id.movieCat)
                val posterMovie = viewItemMovie.findViewById<ImageView>(R.id.moviePoster)
                val ratedMovie = viewItemMovie.findViewById<TextView>(R.id.rated)
                Log.v("Debug1", "TrendsFragmentAdapter MyViewHolder setData for movie.id" + movie.id)

                posterMovie.setOnClickListener {
                    onMovieClickListener(movie.id)
                }

                movie.posterUrl.let{

                    if (movie.posterUrl != "" && movie.posterUrl != "-"){
                        Glide.with(viewItemMovie.context)
                            .load(Constant.BASE_IMAGE_URL + Constant.IMAGE_POSTER_SIZE_1 + movie.posterUrl)
                            .into(posterMovie)
                    }else{
                        posterMovie.setImageResource(R.drawable.empty_poster)

                    }
                }

                titleMovie.text = movie.title

                if (movie.dateRelease != "") {
                    val localDate = LocalDate.parse(movie.dateRelease,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    val formatter = DateTimeFormatter.ofPattern("yyyy")
                    val formattedDate = localDate.format(formatter)
                    yearMovie.text = formattedDate
                } else {
                    yearMovie.text = ""
                }

                ratedMovie.text = movie.voteAverage.toString()
                if (movie.genres.isNotEmpty()){
                    catMovie.text = movie.genres.first().title
                }

                linearLayoutIntoScrollView.addView(viewItemMovie)
            }
        }
    }
}