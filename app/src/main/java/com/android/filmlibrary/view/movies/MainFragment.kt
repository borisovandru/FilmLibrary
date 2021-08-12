package com.android.filmlibrary.view.movies

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.android.filmlibrary.Constant.MOVIES_ADAPTER_COUNT_SPAN
import com.android.filmlibrary.Constant.NAVIGATE_TO_MOVIEBYCAT
import com.android.filmlibrary.Constant.NAVIGATE_TO_MOVIEINFO
import com.android.filmlibrary.R
import com.android.filmlibrary.databinding.MainFragmentBinding
import com.android.filmlibrary.model.AppState
import com.android.filmlibrary.model.data.Category
import com.android.filmlibrary.model.data.MoviesByCategories
import com.android.filmlibrary.view.showSnackBar
import com.android.filmlibrary.viewmodel.movies.MainFragmentViewModel


class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private var moviesByCategory: List<MoviesByCategories> = ArrayList()
    private var categories: List<Category> = ArrayList()
    private val adapter = MainFragmentAdapter()
    private val viewModel: MainFragmentViewModel by lazy {
        ViewModelProvider(this).get(MainFragmentViewModel::class.java)
    }

    private var _binding: MainFragmentBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = MainFragmentBinding.inflate(inflater, container, false)
        return _binding?.root

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun renderCategories(data: AppState) {
        when (data) {
            is AppState.SuccessCategories -> {
                Log.v(
                    "Debug1",
                    "MainFragment renderData AppState.SuccessCategories data.catgoriesData.size=" + data.categoriesData.size
                )
                val categoriesData = data.categoriesData
                binding.loadingLayoutCat.visibility = View.GONE
                fillData(categoriesData)
            }
            is AppState.Loading -> {
                binding.loadingLayoutCat.visibility = View.VISIBLE
            }
            is AppState.Error -> {
                binding.loadingLayoutCat.visibility = View.GONE
                data.error.message?.let {
                    binding.loadingLayoutCat.showSnackBar(it, R.string.ReloadMsg) {
                        viewModel.getCategoriesFromRemoteSource()
                    }
                }
            }
        }
    }

    private fun renderMoviesByCategories(data: AppState) {
        when (data) {
            is AppState.SuccessMoviesByCategories -> {
                Log.v(
                    "Debug1",
                    "MainFragment renderData AppState.SuccessMoviesByCategory data.moviesByCategory.size=" + data.moviesByCategories.size
                )
                val moviesByCategory = data.moviesByCategories
                binding.loadingLayoutCat.visibility = View.GONE
                fillMoviesByCategory(moviesByCategory)
            }
            is AppState.Loading -> {
                binding.loadingLayoutCat.visibility = View.VISIBLE
            }
            is AppState.Error -> {
                binding.loadingLayoutCat.visibility = View.GONE
                data.error.message
                data.error.message?.let {
                    binding.loadingLayoutCat.showSnackBar(it, R.string.ReloadMsg) {
                        viewModel.getCategoriesFromRemoteSource()
                    }
                }
            }
        }
    }

    private fun fillMoviesByCategory(moviesByCategory: List<MoviesByCategories>) {
        Log.v("Debug1", "MainFragment fillMoviesByCategory")
        this.moviesByCategory = moviesByCategory
        adapter.fillMoviesByCategory(moviesByCategory)
        val recyclerView = binding.rvCat
        recyclerView.layoutManager = GridLayoutManager(context, MOVIES_ADAPTER_COUNT_SPAN)
        recyclerView.adapter = adapter
    }

    private fun fillData(categoriesData: List<Category>) {
        Log.v("Debug1", "MainFragment fillData")
        categories = categoriesData

        viewModel.getMoviesByCategoriesFromRemoteSource(categories)

        val observer = Observer<AppState> { appState ->
            renderMoviesByCategories(appState)
        }
        viewModel.getData2().observe(viewLifecycleOwner, observer)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.v("Debug1", "MainFragment onViewCreated")

        adapter.setOnCategoryClickListener { categoryId ->
            activity?.supportFragmentManager?.let {
                Log.v(
                    "Debug1",
                    "MainFragment onViewCreated setOnCategoryClickListener categoryId=$categoryId"
                )
                var category = categories.first()
                for (categoryItem in categories)
                    if (categoryItem.id == categoryId)
                        category = categoryItem
                val navHostFragment: NavHostFragment =
                    activity?.supportFragmentManager?.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

                navHostFragment.navController.navigate(
                    NAVIGATE_TO_MOVIEBYCAT, //Вынес в константы
                    Bundle().apply {
                        putParcelable("category", category)
                    }
                )
            }
        }

        adapter.setOnMovieClickListener { movieId ->
            Log.v("Debug1", "MainFragment onViewCreated setOnMovieClickListener movieId=$movieId")
            val navHostFragment: NavHostFragment =
                activity?.supportFragmentManager?.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            navHostFragment.navController.navigate(
                NAVIGATE_TO_MOVIEINFO,  //Вынес в константы
                Bundle().apply {
                    putInt("movieId", movieId)
                }
            )
        }

        val observer = Observer<AppState> { appState ->
            renderCategories(appState)
        }
        viewModel.getData().observe(viewLifecycleOwner, observer)
        viewModel.getCategoriesFromRemoteSource()
    }

}