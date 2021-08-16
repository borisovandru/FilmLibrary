package com.android.filmlibrary.view.search

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.filmlibrary.Constant
import com.android.filmlibrary.GlobalVariables
import com.android.filmlibrary.R
import com.android.filmlibrary.databinding.SearchFragmentBinding
import com.android.filmlibrary.model.AppState
import com.android.filmlibrary.model.data.Movie
import com.android.filmlibrary.view.showSnackBar
import com.android.filmlibrary.viewmodel.search.SearchViewModel

class SearchFragment : Fragment() {

    companion object {
        const val BUNDLE_EXTRA = "search"
        fun newInstance(bundle: Bundle): SearchFragment {
            val fragment = SearchFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var recyclerView: RecyclerView
    private val viewModel: SearchViewModel by lazy {
        ViewModelProvider(this).get(SearchViewModel::class.java)
    }
    private val adapter = SearchFragmentAdapter()
    private var _binding: SearchFragmentBinding? = null
    private val binding
        get() = _binding!!
    private var moviesBySearch = listOf<Movie>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        Log.v("Debug1", "SearchFragment onCreateView")

        _binding = SearchFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        Log.v("Debug1", "SearchFragment onDestroyView")
        _binding = null
        super.onDestroyView()
    }

    private fun renderData(data: AppState) {
        Log.v("Debug1", "SearchFragment renderData")
        when (data) {
            is AppState.SuccessSearch -> {
                moviesBySearch = data.moviesBySearch
                binding.loadingLayoutSearch.visibility = View.GONE
                adapter.fillMoviesBySearch(moviesBySearch)
            }
            is AppState.Loading -> {
                binding.loadingLayoutSearch.visibility = View.VISIBLE
            }
            is AppState.Error -> {

                binding.loadingLayoutSearch.visibility = View.GONE
                data.error.message?.let {
                    binding.loadingLayoutSearch.showSnackBar(it, R.string.ReloadMsg) {
                        viewModel.getSearchDataFromRemoteSource()
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.v("Debug1", "SearchFragment onViewCreated")

        recyclerView = binding.rvSearch
        recyclerView.layoutManager = GridLayoutManager(context, Constant.MOVIES_ADAPTER_COUNT_SPAN2)
        recyclerView.adapter = adapter

        if ((requireActivity().application as GlobalVariables).moviesBySearch.isNotEmpty())
            moviesBySearch = (requireActivity().application as GlobalVariables).moviesBySearch

        adapter.setOnMovieClickListener { movieId ->
            val bundle = Bundle()
            bundle.putInt("movieId", movieId)
            val navHostFragment: NavHostFragment =
                activity?.supportFragmentManager?.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            navHostFragment.navController.navigate(
                Constant.NAVIGATE_FROM_SEARCH_TO_MOVIE_INFO,
                bundle
            )
        }

        if (moviesBySearch.isNotEmpty()) {
            Log.v(
                "Debug1",
                "SearchFragment onViewCreated moviesBySearch.isNotEmpty() moviesBySearch.size=" + moviesBySearch.size
            )
            adapter.fillMoviesBySearch(moviesBySearch)
        }

        binding.searchButton.setOnClickListener {
            Log.v(
                "Debug1",
                "SearchFragment onViewCreated binding.searchQuery.text.toString()=" + binding.searchQuery.text.toString()
            )
            val observer = Observer<AppState> { appState ->
                renderData(appState)
            }
            viewModel.getData(binding.searchQuery.text.toString())
                .observe(viewLifecycleOwner, observer)
            viewModel.getSearchDataFromRemoteSource()
        }
    }

    override fun onStop() {
        super.onStop()
        (requireActivity().application as GlobalVariables).moviesBySearch = moviesBySearch
        Log.v("Debug1", "SearchFragment onStop")
    }

}