package com.android.filmlibrary.view.moviesbygenre

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.filmlibrary.Constant
import com.android.filmlibrary.Constant.NAME_PARCEBLE_GENRE
import com.android.filmlibrary.Constant.NAME_PARCEBLE_MOVIE
import com.android.filmlibrary.Constant.NAVIGATE_FROM_MOVIES_BY_GENRES_TO_MOVIE_INFO
import com.android.filmlibrary.GlobalVariables.Companion.moviesByGenreCache
import com.android.filmlibrary.GlobalVariables.Companion.moviesListCache
import com.android.filmlibrary.R
import com.android.filmlibrary.databinding.MoviesByGenreFragmentBinding
import com.android.filmlibrary.model.AppState
import com.android.filmlibrary.model.data.Genre
import com.android.filmlibrary.model.data.MoviesByGenre
import com.android.filmlibrary.model.data.MoviesList
import com.android.filmlibrary.view.itemmovie.MovieInfoFragment
import com.android.filmlibrary.view.showSnackBar
import com.android.filmlibrary.viewmodel.moviesbygenre.MoviesByGenreViewModel

class MoviesByGenreFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    private val moviesByCategoryViewModel by viewModels<MoviesByGenreViewModel>()
    private val viewModel: MoviesByGenreViewModel by lazy {
        ViewModelProvider(this).get(moviesByCategoryViewModel::class.java)
    }

    private val adapter = MoviesByGenreAdapter()

    private var _binding: MoviesByGenreFragmentBinding? = null
    private val binding
        get() = _binding!!

    private lateinit var genre: Genre

    private var moviesByGenre: MoviesByGenre =
        MoviesByGenre(Genre(), MoviesList(mutableListOf(), 0, 0))

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = MoviesByGenreFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun renderData(data: AppState) {
        when (data) {
            is AppState.SuccessMoviesByGenre -> {
                binding.loadingLayoutB.visibility = View.GONE
                moviesByGenre = data.moviesByGenre
                adapter.fillMovies(data.moviesByGenre)
            }
            is AppState.Loading -> {
                binding.loadingLayoutB.visibility = View.VISIBLE
            }
            is AppState.Error -> {

                binding.loadingLayoutB.visibility = View.GONE
                data.error.message?.let {
                    binding.loadingLayoutB.showSnackBar(it, R.string.ReloadMsg) {
                        viewModel.getDataFromRemoteSource()
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        recyclerView = binding.rvCatB
        recyclerView.layoutManager = GridLayoutManager(context, Constant.MOVIES_ADAPTER_COUNT_SPAN2)
        recyclerView.adapter = adapter

        if (moviesListCache.results.isNotEmpty())
            moviesByGenre = moviesByGenreCache

        adapter.setOnMovieClickListener { movie ->
            val bundle = Bundle()
            bundle.putParcelable(NAME_PARCEBLE_MOVIE, movie)

            val navHostFragment: NavHostFragment? =
                activity?.supportFragmentManager?.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
            navHostFragment?.navController?.navigate(
                NAVIGATE_FROM_MOVIES_BY_GENRES_TO_MOVIE_INFO,
                bundle
            )
        }

        if (moviesByGenre.movies.results.isNotEmpty()) {
            adapter.fillMovies(moviesByGenre)
        }

        genre = arguments?.getParcelable(BUNDLE_EXTRA)!!
        genre.let {
            val observer = Observer<AppState> { appState ->
                renderData(appState)
            }
            viewModel.getData(genre).observe(viewLifecycleOwner, observer)
            viewModel.getDataFromRemoteSource()
        }
    }

    override fun onStop() {
        super.onStop()
        moviesByGenreCache = moviesByGenre
    }

    companion object {
        const val BUNDLE_EXTRA = NAME_PARCEBLE_GENRE
        fun newInstance(bundle: Bundle): MovieInfoFragment {
            val fragment = MovieInfoFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}