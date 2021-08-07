package com.android.filmlibrary.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.filmlibrary.R
import com.android.filmlibrary.databinding.FragmentHomeBinding
import com.android.filmlibrary.model.AppState
import com.android.filmlibrary.model.data.Category
import com.android.filmlibrary.model.repository.Repository
import com.android.filmlibrary.model.repository.RepositoryImpl
import com.android.filmlibrary.viewmodel.MainViewModel


class HomeFragment : Fragment() {

    private val adapter: MoviesAdapter = MoviesAdapter()

    private var _binding: FragmentHomeBinding? = null
    private val binding
        get() = _binding!!

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }
    private lateinit var repository: Repository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        repository = RepositoryImpl()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter.setOnClickView { movie ->
            activity?.supportFragmentManager?.let { manager ->
                val bundle: Bundle = Bundle()
                bundle.putParcelable(DescriptionMovieFragment.BUNDLE_EXTRA, movie)
                manager.beginTransaction()
                    .add(R.id.container, DescriptionMovieFragment.newInstance(bundle))
                    .addToBackStack("")
                    .commitAllowingStateLoss()
            }
        }
        val observer = Observer<AppState> { renderData(it) }
        viewModel.getLifeData().observe(viewLifecycleOwner, observer)
        viewModel.getWeatherFromLocalSource()
    }

    private fun renderData(appState: AppState) {
        when (appState) {
            is AppState.Success -> {
                binding.loadingLayout.hide()
                initListCategory(appState.listCategory)
            }
            is AppState.Loading -> {
                binding.loadingLayout.show()
            }
            is AppState.Error -> {
                binding.loadingLayout.hide()
                binding.parentLayout.showSnackBar(R.string.snackBarError, R.string.snackBarReload) {
                    viewModel.getWeatherFromLocalSource()
                }
            }
        }
    }

    private fun initListCategory(listCategory: List<Category>) {
        for (category in listCategory)
            binding.parentLayout.addView(createCategory(category))
    }

    private fun createCategory(category: Category): LinearLayout {

        val recyclerView = context?.let { RecyclerView(it) }?.let { recyclerView ->
            adapter.setMoviesData(category.movies)
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            recyclerView.adapter = adapter
            recyclerView
        }
        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.VERTICAL
        val tv = TextView(context)
        tv.textSize = 30F
        tv.text = category.categoryName
        layout.addView(tv)
        layout.addView(recyclerView)
        return layout
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}