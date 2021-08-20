package com.android.filmlibrary.view.profile

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.filmlibrary.Constant
import com.android.filmlibrary.Constant.NAME_PARCEBLE_SETTINGS
import com.android.filmlibrary.GlobalVariables
import com.android.filmlibrary.R
import com.android.filmlibrary.databinding.ProfileFragmentBinding
import com.android.filmlibrary.model.AppState
import com.android.filmlibrary.view.itemmovie.MovieInfoFragment
import com.android.filmlibrary.view.showSnackBar
import com.android.filmlibrary.viewmodel.profile.ContactsAdapter
import com.android.filmlibrary.viewmodel.profile.ProfileViewModel

class ProfileFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private var _binding: ProfileFragmentBinding? = null
    private val binding
        get() = _binding!!

    private val adapter: ContactsAdapter by lazy {
        ContactsAdapter()
    }

    private val viewModel: ProfileViewModel by lazy {
        ViewModelProvider(this).get(ProfileViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = ProfileFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        (requireActivity().application as GlobalVariables).settings.adult =
            binding.switchAdult.isChecked
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val observer = Observer<AppState> { appState ->
            renderData(appState)
        }
        viewModel.getData().observe(viewLifecycleOwner, observer)
        viewModel.getDataFromRemoteSource()
        binding.switchAdult.isChecked =
            (requireActivity().application as GlobalVariables).settings.adult

        recyclerView = binding.rvContacts
        recyclerView.layoutManager = GridLayoutManager(context, Constant.MOVIES_ADAPTER_COUNT_SPAN2)
        recyclerView.adapter = adapter

        viewModel.contacts.observe(viewLifecycleOwner) {
            renderDataContacts(it)
        }
        checkPermission()
    }

    private fun renderData(data: AppState) {
        when (data) {
            is AppState.SuccessSettings -> {
                binding.loadingLayout.visibility = View.GONE
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
        const val BUNDLE_EXTRA = NAME_PARCEBLE_SETTINGS
        fun newInstance(bundle: Bundle): MovieInfoFragment {
            val fragment = MovieInfoFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private fun renderDataContacts(data: AppState) {
        when (data) {
            is AppState.SuccessGetContacts -> {
                binding.loadingLayout.visibility = View.GONE
                //binding.rvContacts.show()
                //binding.loadingLayout.hide()
                //adapter.contacts = data.contacts
                adapter.fillContacts(data.contacts)
            }
            is AppState.Loading -> {
                binding.rvContacts.hide()
                binding.loadingLayout.show()
            }
        }
    }

    private fun checkPermission() {
        context?.let {
            when {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.READ_CONTACTS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    getContacts()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS) -> {
                    AlertDialog.Builder(it)
                        .setTitle("Доступ к контактам")
                        .setMessage("Объяснение")
                        .setPositiveButton("Предоставить доступ") { _, _ ->
                            requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                        }
                        .setNegativeButton("Не надо") { dialog, _ -> dialog.dismiss() }
                        .create()
                        .show()
                }
                else -> requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getContacts()
            } else {
                context?.let {
                    AlertDialog.Builder(it)
                        .setTitle("Доступ к контактам")
                        .setMessage("Объяснение")
                        .setNegativeButton("Закрыть") { dialog, _ -> dialog.dismiss() }
                        .create()
                        .show()
                }
            }
        }

    private fun getContacts() {
        viewModel.getContacts()
    }

    private fun View.show(): View {
        if (visibility != View.VISIBLE) {
            visibility = View.VISIBLE
        }
        return this
    }

    private fun View.hide(): View {
        if (visibility != View.GONE) {
            visibility = View.GONE
        }
        return this
    }
}