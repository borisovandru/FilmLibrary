package com.android.filmlibrary.view.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.filmlibrary.Constant
import com.android.filmlibrary.Constant.NAME_PARCEBLE_MOVIE
import com.android.filmlibrary.Constant.NAME_PARCEBLE_SEARCH
import com.android.filmlibrary.Constant.THRESHOLD
import com.android.filmlibrary.GlobalVariables
import com.android.filmlibrary.R
import com.android.filmlibrary.databinding.SearchFragmentBinding
import com.android.filmlibrary.model.AppState
import com.android.filmlibrary.model.data.MoviesList
import com.android.filmlibrary.view.showSnackBar
import com.android.filmlibrary.viewmodel.search.SearchViewModel

class SearchFragment : Fragment() {

    companion object {
        const val BUNDLE_EXTRA = NAME_PARCEBLE_SEARCH
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
    private var moviesBySearch = MoviesList(
        listOf(),
        0,
        0
    )

    private var searchHistory: List<String> = mutableListOf()

    private lateinit var adapterAC: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = SearchFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun renderData(data: AppState) {
        when (data) {
            is AppState.SuccessSearch -> {
                moviesBySearch = data.moviesBySearches
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

    private fun renderDataSearchHistory(data: AppState) {
        when (data) {
            is AppState.SuccessGetSearchHistory -> {
                searchHistory = data.searchHistory
                adapterAC.clear()
                adapterAC.addAll(searchHistory)
                adapterAC.notifyDataSetChanged()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.searchQuery.threshold = THRESHOLD
        adapterAC =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, searchHistory)
        adapterAC.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.searchQuery.setAdapter(adapterAC)

        recyclerView = binding.rvSearch
        recyclerView.layoutManager = GridLayoutManager(context, Constant.MOVIES_ADAPTER_COUNT_SPAN2)
        recyclerView.adapter = adapter

        if ((requireActivity().application as GlobalVariables).moviesBySearch.results.isNotEmpty()) {
            moviesBySearch = (requireActivity().application as GlobalVariables).moviesBySearch
        }

        if (((requireActivity().application as GlobalVariables).seachString) != "") {
            binding.searchQuery.setText((requireActivity().application as GlobalVariables).seachString)
        }

        adapter.setOnMovieClickListener { movie ->
            val bundle = Bundle()
            bundle.putParcelable(NAME_PARCEBLE_MOVIE, movie)
            val navHostFragment: NavHostFragment =
                activity?.supportFragmentManager?.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            navHostFragment.navController.navigate(
                Constant.NAVIGATE_FROM_SEARCH_TO_MOVIE_INFO,
                bundle
            )
        }

        if (moviesBySearch.results.isNotEmpty()) {
            adapter.fillMoviesBySearch(moviesBySearch)
        }

        binding.searchButton.setOnClickListener {
            val observer = Observer<AppState> { appState ->
                renderData(appState)
            }
            viewModel.setData(
                binding.searchQuery.text.toString(),
                (requireActivity().application as GlobalVariables).settings.adult
            )
                .observe(viewLifecycleOwner, observer)
            viewModel.getSearchDataFromRemoteSource()

            val observer2 = Observer<AppState> { appState ->
                renderDataSearchHistory(appState)
            }
            viewModel.getSearchHistory().observe(viewLifecycleOwner, observer2)
            viewModel.getSearchHistory2()
        }
    }

    override fun onStop() {
        super.onStop()
        (requireActivity().application as GlobalVariables).moviesBySearch = moviesBySearch
        (requireActivity().application as GlobalVariables).seachString =
            binding.searchQuery.text.toString()
    }
}