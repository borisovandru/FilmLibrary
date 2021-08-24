package com.android.filmlibrary.view.trends

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.filmlibrary.Constant
import com.android.filmlibrary.Constant.NAME_PARCEL_MOVIE
import com.android.filmlibrary.Constant.NAVIGATE_FROM_TRENDS_TO_MOVIE_INFO
import com.android.filmlibrary.GlobalVariables
import com.android.filmlibrary.R
import com.android.filmlibrary.databinding.TrendsFragmentBinding
import com.android.filmlibrary.model.AppState
import com.android.filmlibrary.model.data.MoviesByTrend
import com.android.filmlibrary.view.showSnackBar
import com.android.filmlibrary.viewmodel.thrends.ThrendsFragmentViewModel

class TrendsFragment : Fragment() {

    companion object {
        fun newInstance() = TrendsFragment()
    }

    private lateinit var recyclerView: RecyclerView
    private var moviesByTrend: List<MoviesByTrend> = ArrayList()
    private val adapter = TrendsFragmentAdapter()
    private val viewModel: ThrendsFragmentViewModel by lazy {
        ViewModelProvider(this).get(ThrendsFragmentViewModel::class.java)
    }

    private var _binding: TrendsFragmentBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = TrendsFragmentBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onStop() {
        super.onStop()
        (requireActivity().application as GlobalVariables).moviesByTrend = moviesByTrend
    }

    private fun renderTrends(data: AppState) {
        when (data) {
            is AppState.SuccessMoviesByTrends -> {
                moviesByTrend = data.moviesByTrends
                binding.loadingLayoutTrend.visibility = View.GONE
                adapter.fillMoviesByTrend(moviesByTrend)
            }
            is AppState.Loading -> {
                binding.loadingLayoutTrend.visibility = View.VISIBLE
            }
            is AppState.Error -> {
                binding.loadingLayoutTrend.visibility = View.GONE
                data.error.message?.let {
                    binding.loadingLayoutTrend.showSnackBar(it, R.string.ReloadMsg) {
                        viewModel.getTrendsFromRemoteSource()
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        recyclerView = binding.rvTrend
        recyclerView.layoutManager = GridLayoutManager(context, Constant.MOVIES_ADAPTER_COUNT_SPAN)
        recyclerView.adapter = adapter

        if ((requireActivity().application as GlobalVariables).moviesByTrend.isNotEmpty()) {
            moviesByTrend = (requireActivity().application as GlobalVariables).moviesByTrend
        }

        adapter.setOnMovieClickListener { movie ->
            val navHostFragment: NavHostFragment =
                activity?.supportFragmentManager?.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            navHostFragment.navController.navigate(
                NAVIGATE_FROM_TRENDS_TO_MOVIE_INFO,  //Вынес в константы
                Bundle().apply {
                    putParcelable(NAME_PARCEL_MOVIE, movie)
                }
            )
        }

        if (moviesByTrend.isNotEmpty()) {
            adapter.fillMoviesByTrend(this.moviesByTrend)
        } else {
            val observer = Observer<AppState> { appState ->
                renderTrends(appState)
            }
            viewModel.getData().observe(viewLifecycleOwner, observer)
            viewModel.getTrendsFromRemoteSource()
        }
    }
}