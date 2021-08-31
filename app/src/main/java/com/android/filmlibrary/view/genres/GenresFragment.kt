package com.android.filmlibrary.view.genres

import android.os.Bundle
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
import com.android.filmlibrary.Constant.NAME_PARCEBLE_GENRE
import com.android.filmlibrary.Constant.NAME_PARCEBLE_MOVIE
import com.android.filmlibrary.R
import com.android.filmlibrary.databinding.GenresFragmentBinding
import com.android.filmlibrary.model.AppState
import com.android.filmlibrary.model.data.Genre
import com.android.filmlibrary.view.showSnackBar
import com.android.filmlibrary.viewmodel.genres.GenresViewModel

class GenresFragment : Fragment() {

    companion object {
        fun newInstance() = GenresFragment()
    }

    private var genres: List<Genre> = ArrayList()

    private lateinit var recyclerView: RecyclerView

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
                genres = data.genreData
                binding.loadingLayoutCat.visibility = View.GONE
                fillData(genres)
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
                //moviesByGenres = data.moviesByGenres
                binding.loadingLayoutCat.visibility = View.GONE
                adapter.fillMoviesByGenres(data.moviesByGenres, genres)
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
        genres = genreData

        viewModel.getMoviesByGenresFromRemoteSource(genres)

        val observer = Observer<AppState> { appState ->
            renderMoviesByGenres(appState)
        }
        viewModel.getData2().observe(viewLifecycleOwner, observer)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        recyclerView = binding.rvCat
        recyclerView.layoutManager = GridLayoutManager(context, Constant.MOVIES_ADAPTER_COUNT_SPAN)
        recyclerView.adapter = adapter

        adapter.setOnGenresClickListener { categoryId ->
            activity?.supportFragmentManager?.let {
                var genre = genres.first()
                genres.forEach { genreItem ->
                    if (genreItem.id == categoryId) {
                        genre = genreItem
                    }
                }
                val navHostFragment: NavHostFragment? =
                    activity?.supportFragmentManager?.findFragmentById(R.id.nav_host_container) as? NavHostFragment
                navHostFragment?.let {
                    it.navController.navigate(
                        Constant.NAVIGATE_FROM_GENRES_TO_MOVIES_BY_GENRE, //Вынес в константы
                        Bundle().apply {
                            putParcelable(NAME_PARCEBLE_GENRE, genre)
                        }
                    )
                }
            }
        }

        adapter.setOnMovieClickListener { movie ->
            val navHostFragment: NavHostFragment? =
                activity?.supportFragmentManager?.findFragmentById(R.id.nav_host_container) as? NavHostFragment
            navHostFragment?.let {
                it.navController.navigate(
                    Constant.NAVIGATE_FROM_GENRES_TO_MOVIE_INFO,  //Вынес в константы
                    Bundle().apply {
                        putParcelable(NAME_PARCEBLE_MOVIE, movie)
                    }
                )
            }
        }

        val observer = Observer<AppState> { appState ->
            renderGenres(appState)
        }
        viewModel.getData().observe(viewLifecycleOwner, observer)
        viewModel.getGenresFromRemoteSource()
    }
}