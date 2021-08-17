package com.android.filmlibrary.view.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.android.filmlibrary.R
import com.android.filmlibrary.databinding.ProfileFragmentBinding
import com.android.filmlibrary.model.AppState
import com.android.filmlibrary.view.itemmovie.MovieInfoFragment
import com.android.filmlibrary.view.showSnackBar
import com.android.filmlibrary.viewmodel.profile.ProfileViewModel

class ProfileFragment : Fragment() {

    private var _binding: ProfileFragmentBinding? = null
    private val binding
        get() = _binding!!

    private val viewModel: ProfileViewModel by lazy {
        ViewModelProvider(this).get(ProfileViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        Log.v("Debug1", "ProfileFragment onCreateView")
        _binding = ProfileFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        Log.v("Debug1", "ProfileFragment onDestroyView")
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.v("Debug1", "ProfileFragment onViewCreated")
        val observer = Observer<AppState> { appState ->
            renderData(appState)
        }
        viewModel.getData().observe(viewLifecycleOwner, observer)
        viewModel.getDataFromRemoteSource()
    }

    private fun renderData(data: AppState) {
        Log.v("Debug1", "MovieInfoFragment renderData")
        when (data) {
            is AppState.SuccessSettings -> {
                val settingsTMDB = data.settingsTMDB
                binding.loadingLayout.visibility = View.GONE
                binding.baseImageUrl.text = settingsTMDB.imageBaseURL
                binding.baseSecureImageUrl.text = settingsTMDB.imageSecureBaseURL
            }
            is AppState.Loading -> {
                binding.loadingLayout.visibility = View.VISIBLE
            }
            is AppState.Error -> {
                binding.loadingLayout.visibility = View.GONE
                data.error.message?.let {
                    binding.loadingLayout.showSnackBar(it, R.string.ReloadMsg) {
                        viewModel.getDataFromRemoteSource()
                    }
                }
            }
        }
    }

    companion object {
        const val BUNDLE_EXTRA = "Settings"
        fun newInstance(bundle: Bundle): MovieInfoFragment {
            val fragment = MovieInfoFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}