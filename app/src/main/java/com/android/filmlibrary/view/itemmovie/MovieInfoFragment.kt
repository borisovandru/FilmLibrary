package com.android.filmlibrary.view.itemmovie

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
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        Log.v("Debug1", "MovieInfoFragment onCreateView")
        _binding = FragmentMovieInfoBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun renderData(data: AppState) {
        Log.v("Debug1", "MovieInfoFragment renderData")
        when (data) {
            is AppState.SuccessMovie -> {
                val movieData = data.movieData
                binding.loadingLayout.visibility = View.GONE
                binding.titleMovie.text = movieData.title
                binding.yearMovie.text = movieData.year.toString()
                binding.descrMovie.text = movieData.description
                Glide.with(this)
                    .load(BASE_IMAGE_URL + IMAGE_POSTER_SIZE_1 + movieData.posterUrl)
                    .into(binding.imageMovie)

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