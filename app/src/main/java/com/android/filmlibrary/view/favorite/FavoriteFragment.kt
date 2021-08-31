package com.android.filmlibrary.view.favorite

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
import com.android.filmlibrary.Constant.NAME_PARCEBLE_MOVIE
import com.android.filmlibrary.GlobalVariables.Companion.favMoviesCache
import com.android.filmlibrary.R
import com.android.filmlibrary.databinding.FavoriteFragmentBinding
import com.android.filmlibrary.model.AppState
import com.android.filmlibrary.model.data.Movie
import com.android.filmlibrary.view.showSnackBar
import com.android.filmlibrary.viewmodel.favorite.FavoriteViewModel

class FavoriteFragment : Fragment() {

    companion object {
        fun newInstance() = FavoriteFragment()
    }

    private lateinit var recyclerView: RecyclerView

    private val viewModel: FavoriteViewModel by lazy {
        ViewModelProvider(this).get(FavoriteViewModel::class.java)
    }

    private val adapter = FavoriteFragmentAdapter()
    private var _binding: FavoriteFragmentBinding? = null
    private val binding
        get() = _binding!!
    private var favMovies = listOf<Movie>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FavoriteFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun renderData(data: AppState) {
        when (data) {
            is AppState.SuccessGetFavoriteMovies -> {
                favMovies = data.favMovies
                binding.loadingLayoutFav.visibility = View.GONE
                adapter.fillMoviesBySearch(favMovies)
            }
            is AppState.Loading -> {
                binding.loadingLayoutFav.visibility = View.VISIBLE
            }
            is AppState.Error -> {

                binding.loadingLayoutFav.visibility = View.GONE
                data.error.message?.let {
                    binding.loadingLayoutFav.showSnackBar(it, R.string.ReloadMsg) {
                        viewModel.getFavoriteFromLocalDB()
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = binding.rvFav
        recyclerView.layoutManager = GridLayoutManager(context, Constant.MOVIES_ADAPTER_COUNT_SPAN2)
        recyclerView.adapter = adapter

        if (favMoviesCache.isNotEmpty()) {
            favMovies = favMoviesCache
        }

        val observer = Observer<AppState> { appState ->
            renderData(appState)
        }
        viewModel.getFavoriteStart()
            .observe(viewLifecycleOwner, observer)
        viewModel.getFavoriteFromLocalDB()

        adapter.setOnMovieClickListener { movie ->
            val bundle = Bundle()
            bundle.putParcelable(NAME_PARCEBLE_MOVIE, movie)
            val navHostFragment: NavHostFragment? =
                activity?.supportFragmentManager?.findFragmentById(R.id.nav_host_container) as? NavHostFragment
            navHostFragment?.navController?.navigate(
                Constant.NAVIGATE_FROM_FAV_TO_MOVIE_INFO,
                bundle
            )
        }

        if (favMovies.isNotEmpty()) {
            adapter.fillMoviesBySearch(favMovies)
        }
    }

    override fun onStop() {
        super.onStop()
        favMoviesCache = favMovies
    }
}