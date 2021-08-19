package com.android.filmlibrary.view.genres

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.filmlibrary.Constant
import com.android.filmlibrary.GlobalVariables
import com.android.filmlibrary.R
import com.android.filmlibrary.databinding.GenresFragmentBinding
import com.android.filmlibrary.model.AppState
import com.android.filmlibrary.model.data.Genre
import com.android.filmlibrary.model.data.MoviesByGenre
import com.android.filmlibrary.view.showSnackBar
import com.android.filmlibrary.viewmodel.genres.GenresViewModel

class GenresFragment : Fragment() {

    companion object {
        fun newInstance() = GenresFragment()
    }

    private lateinit var recyclerView: RecyclerView
    private var moviesByGenres: List<MoviesByGenre> = ArrayList()
    private var genres: List<Genre> = ArrayList()
    private val adapter = GenresFragmentAdapter()
    private val viewModel: GenresViewModel by lazy {
        ViewModelProvider(this).get(GenresViewModel::class.java)
    }

    private var _binding: GenresFragmentBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = GenresFragmentBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun renderGenres(data: AppState) {
        when (data) {
            is AppState.SuccessGenres -> {
                Log.v(
                    "Debug1",
                    "CategoriesFragment renderData AppState.SuccessCategories data.categoriesData.size=" + data.genreData.size
                )
                val genresData = data.genreData
                binding.loadingLayoutCat.visibility = View.GONE
                fillData(genresData)
            }
            is AppState.Loading -> {
                binding.loadingLayoutCat.visibility = View.VISIBLE
            }
            is AppState.Error -> {
                binding.loadingLayoutCat.visibility = View.GONE
                data.error.message?.let {
                    binding.loadingLayoutCat.showSnackBar(it, R.string.ReloadMsg) {
                        viewModel.getGenresFromRemoteSource()
                    }
                }
            }
        }
    }

    private fun renderMoviesByGenres(data: AppState) {
        when (data) {
            is AppState.SuccessMoviesByGenres -> {
                Log.v(
                    "Debug1",
                    "CategoriesFragment renderData AppState.SuccessMoviesByCategory data.moviesByCategory.size=" + data.moviesByGenres.size
                )
                moviesByGenres = data.moviesByGenres
                binding.loadingLayoutCat.visibility = View.GONE
                adapter.fillMoviesByGenres(moviesByGenres, genres)
            }
            is AppState.Loading -> {
                binding.loadingLayoutCat.visibility = View.VISIBLE
            }
            is AppState.Error -> {
                binding.loadingLayoutCat.visibility = View.GONE
                data.error.message
                data.error.message?.let {
                    binding.loadingLayoutCat.showSnackBar(it, R.string.ReloadMsg) {
                        viewModel.getMoviesByGenresFromRemoteSource(genres)
                    }
                }
            }
        }
    }

    private fun fillData(genreData: List<Genre>) {
        Log.v("Debug1", "CategoriesFragment fillData")
        genres = genreData

        viewModel.getMoviesByGenresFromRemoteSource(genres)

        val observer = Observer<AppState> { appState ->
            renderMoviesByGenres(appState)
        }
        viewModel.getData2().observe(viewLifecycleOwner, observer)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.v("Debug1", "CategoriesFragment onViewCreated")

        recyclerView = binding.rvCat
        recyclerView.layoutManager = GridLayoutManager(context, Constant.MOVIES_ADAPTER_COUNT_SPAN)
        recyclerView.adapter = adapter

        if ((requireActivity().application as GlobalVariables).genres.isNotEmpty())
            genres = (requireActivity().application as GlobalVariables).genres

        if ((requireActivity().application as GlobalVariables).moviesByGenres.isNotEmpty())
            moviesByGenres = (requireActivity().application as GlobalVariables).moviesByGenres


        adapter.setOnGenresClickListener { categoryId ->
            activity?.supportFragmentManager?.let {
                Log.v(
                    "Debug1",
                    "CategoriesFragment onViewCreated setOnCategoryClickListener categoryId=$categoryId"
                )
                var genre = genres.first()
                for (genreItem in genres) {
                    if (genreItem.id == categoryId) {
                        genre = genreItem
                    }
                }
                val navHostFragment: NavHostFragment =
                    activity?.supportFragmentManager?.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                navHostFragment.navController.navigate(
                    Constant.NAVIGATE_FROM_GENRES_TO_MOVIES_BY_GENRE, //Вынес в константы
                    Bundle().apply {
                        putParcelable("category", genre)
                    }
                )
            }
        }

        adapter.setOnMovieClickListener { movie ->
            Log.v(
                "Debug1",
                "CategoriesFragment onViewCreated setOnMovieClickListener movieId=$movie.id"
            )
            val navHostFragment: NavHostFragment =
                activity?.supportFragmentManager?.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            navHostFragment.navController.navigate(
                Constant.NAVIGATE_FROM_GENRES_TO_MOVIE_INFO,
                Bundle().apply {
                    putParcelable("Movie", movie)
                }
            )
        }

        if (moviesByGenres.isNotEmpty()) {
            Log.v(
                "Debug1",
                "MoviesByGenreFragment onViewCreated moviesBySearch.isNotEmpty() moviesBySearch="
            )
            adapter.fillMoviesByGenres(moviesByGenres, genres)
        } else {
            val observer = Observer<AppState> { appState ->
                renderGenres(appState)
            }
            viewModel.getData().observe(viewLifecycleOwner, observer)
            viewModel.getGenresFromRemoteSource()
        }
    }

    override fun onStop() {
        super.onStop()
        (requireActivity().application as GlobalVariables).genres = genres
        (requireActivity().application as GlobalVariables).moviesByGenres = moviesByGenres
        Log.v("Debug1", "MoviesByGenreFragment onStop")
    }
}