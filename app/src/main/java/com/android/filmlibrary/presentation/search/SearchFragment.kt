package com.android.filmlibrary.presentation.search

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
import com.android.filmlibrary.utils.Constant
import com.android.filmlibrary.utils.Constant.NAME_PARCEBLE_MOVIE
import com.android.filmlibrary.utils.Constant.THRESHOLD
import com.android.filmlibrary.utils.GlobalVariables.Companion.searchStringCache
import com.android.filmlibrary.utils.GlobalVariables.Companion.settings
import com.android.filmlibrary.R
import com.android.filmlibrary.databinding.SearchFragmentBinding
import com.android.filmlibrary.utils.AppState
import com.android.filmlibrary.utils.showSnackBar

class SearchFragment : Fragment() {

    companion object {
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
                binding.loadingLayoutSearch.visibility = View.GONE
                if (binding.searchQuery.text.toString() == "") {
                    binding.searchQuery.setText(data.moviesBySearches.searchString)
                }
                adapter.fillMoviesBySearch(data.moviesBySearches.searchResult)
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
        binding.searchQuery.setOnItemClickListener { parent, _, position, _ ->

            val observerOnClick = Observer<AppState> { appState ->
                renderData(appState)
            }

            viewModel.setData(
                parent.getItemAtPosition(position).toString(),
                settings.adult
            )
                .observe(viewLifecycleOwner, observerOnClick)
            viewModel.getSearchDataFromRemoteSource()
        }

        recyclerView = binding.rvSearch
        recyclerView.layoutManager = GridLayoutManager(context, Constant.MOVIES_ADAPTER_COUNT_SPAN2)
        recyclerView.adapter = adapter

        adapter.setOnMovieClickListener { movie ->
            val bundle = Bundle()
            bundle.putParcelable(NAME_PARCEBLE_MOVIE, movie)
            val navHostFragment: NavHostFragment? =
                activity?.supportFragmentManager?.findFragmentById(R.id.nav_host_container) as? NavHostFragment
            navHostFragment?.navController?.navigate(
                Constant.NAVIGATE_FROM_SEARCH_TO_MOVIE_INFO,
                bundle
            )
        }

        if (searchStringCache != "") {
            binding.searchQuery.setText(searchStringCache)
            val observerSearchResult = Observer<AppState> { appState ->
                renderData(appState)
            }
            viewModel.setData(
                binding.searchQuery.text.toString(),
                settings.adult
            )
                .observe(viewLifecycleOwner, observerSearchResult)
            viewModel.getSearchDataFromRemoteSource()
        }

        val observerSearchHistory = Observer<AppState> { appState ->
            renderDataSearchHistory(appState)
        }
        viewModel.getSearchHistory().observe(viewLifecycleOwner, observerSearchHistory)
        viewModel.getSearchHistory2()

        binding.searchButton.setOnClickListener {
            val observerOnClick = Observer<AppState> { appState ->
                renderData(appState)
            }
            viewModel.setData(
                binding.searchQuery.text.toString(),
                settings.adult
            )
                .observe(viewLifecycleOwner, observerOnClick)
            viewModel.getSearchDataFromRemoteSource()
        }
    }
}