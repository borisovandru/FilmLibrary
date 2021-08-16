package com.android.filmlibrary.view.profile

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.android.filmlibrary.Constant.DETAILS_INTENT_FILTER
import com.android.filmlibrary.Constant.DETAILS_LOAD_RESULT_EXTRA
import com.android.filmlibrary.Constant.DETAILS_REQUEST_ERROR_EXTRA
import com.android.filmlibrary.Constant.DETAILS_REQUEST_ERROR_MESSAGE_EXTRA
import com.android.filmlibrary.Constant.DETAILS_RESPONSE_SUCCESS_EXTRA
import com.android.filmlibrary.Constant.SETTINGS_TMDB_IMAGE_SECURE_URL
import com.android.filmlibrary.Constant.SETTINGS_TMDB_IMAGE_URL
import com.android.filmlibrary.databinding.ProfileFragmentBinding
import com.android.filmlibrary.model.data.SettingsTMDB
import com.android.filmlibrary.services.DetailsService
import com.android.filmlibrary.view.itemmovie.MovieInfoFragment

class ProfileFragment : Fragment() {

    private lateinit var settingsTMDBBundle: SettingsTMDB
    private var _binding: ProfileFragmentBinding? = null
    private val binding
        get() = _binding!!
    private val loadResultsReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.getStringExtra(DETAILS_LOAD_RESULT_EXTRA)) {
                DETAILS_REQUEST_ERROR_EXTRA -> showError(
                    intent.getStringExtra(DETAILS_REQUEST_ERROR_MESSAGE_EXTRA)
                )
                DETAILS_RESPONSE_SUCCESS_EXTRA -> renderData(
                    intent.getStringExtra(SETTINGS_TMDB_IMAGE_URL),
                    intent.getStringExtra(SETTINGS_TMDB_IMAGE_SECURE_URL)
                )
            }
        }
    }

    private fun showError(processError: String?) {
        Toast.makeText(context, processError, Toast.LENGTH_LONG).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        Log.v("Debug1", "ProfileFragment onCreateView")
        _binding = ProfileFragmentBinding.inflate(inflater, container, false)

        context?.let {
            LocalBroadcastManager.getInstance(it)
                .registerReceiver(loadResultsReceiver, IntentFilter(DETAILS_INTENT_FILTER))
        }

        return binding.root
    }

    override fun onDestroyView() {
        Log.v("Debug1", "ProfileFragment onDestroyView")
        context?.let {
            LocalBroadcastManager.getInstance(it).unregisterReceiver(loadResultsReceiver)
        }
        _binding = null
        super.onDestroyView()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.v("Debug1", "ProfileFragment onViewCreated")
        settingsTMDBBundle = arguments?.getParcelable(BUNDLE_EXTRA) ?: SettingsTMDB()
        context?.let {
            it.startService(Intent(it, DetailsService::class.java))
        }
    }

    private fun renderData(imageBaseURL: String?, imageSecureBaseURL: String?) {
        Log.v("Debug1", "ProfileFragment renderData")
        binding.baseImageUrl.text = imageBaseURL
        binding.baseSecureImageUrl.text = imageSecureBaseURL
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