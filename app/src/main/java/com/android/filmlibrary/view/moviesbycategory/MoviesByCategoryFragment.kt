package com.android.filmlibrary.view.moviesbycategory

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.android.filmlibrary.Constant.MOVIES_ADAPTER_COUNT_SPAN2
import com.android.filmlibrary.R
import com.android.filmlibrary.viewmodel.moviesbycategory.MoviesByCategoryViewModel
import com.android.filmlibrary.databinding.MoviesByCategoryFragmentBinding
import com.android.filmlibrary.model.AppState
import com.android.filmlibrary.model.data.Category
import com.android.filmlibrary.model.data.MoviesByGenre
import com.android.filmlibrary.view.itemmovie.MovieInfoFragment
import com.android.filmlibrary.view.showSnackBar


class MoviesByCategoryFragment : Fragment() {

    private val moviesByCategoryViewModel by viewModels<MoviesByCategoryViewModel>()
    private lateinit var movies: MoviesByGenre
    private val viewModel: MoviesByCategoryViewModel by lazy {
        ViewModelProvider(this).get(moviesByCategoryViewModel::class.java)
    }
    private val adapter = MoviesByCategoryAdapter()
    private var _binding: MoviesByCategoryFragmentBinding? = null
    private val binding
        get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = MoviesByCategoryFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun renderData(data: AppState) {
        when (data) {
            is AppState.SuccessMoviesByCategory -> {
                val moviesByCategory = data.moviesByCategory
                binding.loadingLayoutB.visibility = View.GONE
                fillData(moviesByCategory)
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

    private fun fillData(movieData: MoviesByGenre) {
        movies = movieData
        adapter.fillMovies(movieData)
        val recyclerView = binding.rvCatB
        recyclerView.layoutManager = GridLayoutManager(context, MOVIES_ADAPTER_COUNT_SPAN2)
        recyclerView.adapter = adapter

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        adapter.setOnMovieClickListener { movieId ->
            val bundle = Bundle()
            bundle.putInt("movieId", movieId)

            val navHostFragment: NavHostFragment =
                activity?.supportFragmentManager?.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            navHostFragment.navController.navigate(
                R.id.action_moviesByCategoryFragment_to_movieInfoFragment,
                bundle
            )
        }

        val category = arguments?.getParcelable<Category>(BUNDLE_EXTRA)
        category?.let {
            Log.v("Debug1", "MoviesByCategoryFragment onViewCreated category.id=" + category.id)
            val observer = Observer<AppState> { appState ->
                renderData(appState)
            }
            viewModel.getData(category).observe(viewLifecycleOwner, observer)
            viewModel.getDataFromRemoteSource()
        }

    }

    companion object {
        const val BUNDLE_EXTRA = "category"
        fun newInstance(bundle: Bundle): MovieInfoFragment {
            val fragment = MovieInfoFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

}