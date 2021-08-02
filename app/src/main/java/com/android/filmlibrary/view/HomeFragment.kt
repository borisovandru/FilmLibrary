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
import com.android.filmlibrary.databinding.FragmentHomeBinding
import com.android.filmlibrary.model.AppState
import com.android.filmlibrary.model.data.Category
import com.android.filmlibrary.model.repository.Repository
import com.android.filmlibrary.model.repository.RepositoryImpl
import com.android.filmlibrary.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private var _bainding: FragmentHomeBinding? = null
    private val binding
        get() = _bainding!!
    private lateinit var viewModel: MainViewModel
    private lateinit var repository: Repository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bainding = FragmentHomeBinding.inflate(inflater, container, false)
        repository = RepositoryImpl()


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val observer = Observer<AppState> { renderData(it) }
        viewModel.getLifeData().observe(viewLifecycleOwner, observer)
        viewModel.getWeatherFromLocalSource()
    }

    private fun renderData(appState: AppState) {
        when (appState) {
            is AppState.Success -> {
                val listCategory = appState.listCategory
                binding.loadingLayout.visibility = View.GONE
                initListCategory(listCategory)
            }
            is AppState.Loading -> {
                binding.loadingLayout.visibility = View.VISIBLE
            }
            is AppState.Error -> {
                binding.loadingLayout.visibility = View.GONE
                Snackbar
                    .make(binding.parentLayout, "Error", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Reload") { viewModel.getWeatherFromLocalSource() }
                    .show()

            }
        }
    }

    private fun initListCategory(listCategery: List<Category>) {
        for (categegory in listCategery)
            binding.parentLayout.addView(createCategory(categegory))
    }

    private fun createCategory(category: Category): LinearLayout {
        val recyclerView = context?.let { RecyclerView(it) }

        if (recyclerView != null) {
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            recyclerView.adapter = MoviesAdapter(category.movies, resources)
        }
        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.VERTICAL
        val tv = TextView(context)
        tv.textSize = 30F
        tv.setText(category.categoryName)
        layout.addView(tv)
        layout.addView(recyclerView)
        return layout
    }

    override fun onDestroy() {
        super.onDestroy()
        _bainding = null
    }

}