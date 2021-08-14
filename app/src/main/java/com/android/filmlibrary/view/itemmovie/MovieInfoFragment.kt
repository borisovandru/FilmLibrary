package com.android.filmlibrary.view.itemmovie

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.filmlibrary.databinding.FragmentMovieInfoBinding
import com.android.filmlibrary.viewmodel.itemmovie.MovieInfoViewModel
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.android.filmlibrary.Constant.BASE_IMAGE_URL
import com.android.filmlibrary.Constant.IMAGE_POSTER_SIZE_1
import com.bumptech.glide.Glide
import com.android.filmlibrary.R
import com.android.filmlibrary.model.AppState
import com.android.filmlibrary.view.showSnackBar


class MovieInfoFragment : Fragment() {

    private val movieInfoViewModel by viewModels<MovieInfoViewModel>()

    private val viewModel: MovieInfoViewModel by lazy {
        ViewModelProvider(this).get(movieInfoViewModel::class.java)
    }

    private var _binding: FragmentMovieInfoBinding? = null
    private val binding
        get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        Log.v("Debug1", "MovieInfoFragment onCreateView")
        _binding = FragmentMovieInfoBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        Log.v("Debug1", "MovieInfoFragment onDestroyView")
        _binding = null

        super.onDestroyView()
    }

    @SuppressLint("SetTextI18n")
    private fun renderData(data: AppState) {
        Log.v("Debug1", "MovieInfoFragment renderData")
        when (data) {
            is AppState.SuccessMovie -> {
                val movieData = data.movieData
                binding.loadingLayout.visibility = View.GONE
                if (movieData.posterUrl != "" && movieData.posterUrl != "-") {
                    Glide.with(this)
                        .load(BASE_IMAGE_URL + IMAGE_POSTER_SIZE_1 + movieData.posterUrl)
                        .into(binding.imageMovie)
                } else {
                    binding.imageMovie.setImageResource(R.drawable.empty_poster)
                }
                binding.rated.text = movieData.voteAverage.toString()

                if (movieData.title == movieData.originalTitle) {
                    binding.titleMovie.text = movieData.title
                } else {
                    binding.titleMovie.text =
                        getString(R.string.titleMovie, movieData.title, movieData.originalTitle)
                }

                binding.yearMovie.text = movieData.dateRelease

                for (country in movieData.countries) {
                    if (binding.countryMovie.text.toString() == "") {
                        binding.countryMovie.text = country.name
                    } else {
                        binding.countryMovie.text = getString(
                            R.string.comma,
                            binding.countryMovie.text.toString(),
                            country.name
                        )
                    }
                }

                binding.runtimeMovie.text =
                    movieData.runtime.toString() + getString(R.string.Minutes)

                for (genre in movieData.genres) {
                    if (binding.genreMovie.text == "") {
                        binding.genreMovie.text = genre.title
                    } else {
                        binding.genreMovie.text = getString(
                            R.string.comma,
                            binding.genreMovie.text.toString(),
                            genre.title
                        )
                    }
                }

                binding.descrMovie.text = movieData.overview

            }
            is AppState.Loading -> {
                binding.loadingLayout.visibility = View.VISIBLE
            }
            is AppState.Error -> {
                binding.loadingLayout.visibility = View.GONE
                data.error.message?.let {
                    binding.loadingLayout.showSnackBar(it, R.string.ReloadMsg) {
                        viewModel.getMovieFromRemoteSource()
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.v("Debug1", "MovieInfoFragment onViewCreated")
        super.onViewCreated(view, savedInstanceState)

        val movieId = arguments?.getInt(BUNDLE_EXTRA)
        movieId?.let {
            val observer = Observer<AppState> { appState ->
                renderData(appState)
            }
            viewModel.getData(movieId).observe(viewLifecycleOwner, observer)
            viewModel.getMovieFromRemoteSource()
        }
    }


    companion object {
        const val BUNDLE_EXTRA = "movieId"

        fun newInstance(bundle: Bundle): MovieInfoFragment {
            val fragment = MovieInfoFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}