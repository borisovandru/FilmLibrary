package com.android.filmlibrary.view.profile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.filmlibrary.Constant
import com.android.filmlibrary.GlobalVariables.Companion.settings
import com.android.filmlibrary.R
import com.android.filmlibrary.databinding.ProfileFragmentBinding
import com.android.filmlibrary.model.AppState
import com.android.filmlibrary.model.data.Contact
import com.android.filmlibrary.view.itemmovie.MovieInfoFragment
import com.android.filmlibrary.view.hide
import com.android.filmlibrary.view.show
import com.android.filmlibrary.view.trends.TrendsFragment
import com.android.filmlibrary.viewmodel.profile.ProfileViewModel

class ProfileFragment : Fragment() {

    private var contacts: List<Contact> = ArrayList()
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
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.v("Debug1", "ProfileFragment onViewCreated")

        binding.switchAdult.isChecked =
            settings.adult
        binding.switchAdult.setOnCheckedChangeListener { _, isChecked ->
            settings.adult = isChecked
        }

        binding.switchGeoFence.isChecked =
            settings.geoFence
        binding.switchGeoFence.setOnClickListener {
            settings.geoFence = binding.switchGeoFence.isChecked

            if (!binding.switchGeoFence.isChecked) {
                val trendsFragment = TrendsFragment.newInstance()
                trendsFragment.removeGeoFence(requireActivity())
            }
        }

        binding.switchWithPhone.isChecked =
            settings.withPhone
        binding.switchWithPhone.setOnCheckedChangeListener { _, isChecked ->
            settings.withPhone = isChecked
            getContacts(isChecked)
        }

        recyclerView = binding.rvContacts
        recyclerView.layoutManager = GridLayoutManager(context, Constant.MOVIES_ADAPTER_COUNT_SPAN)
        recyclerView.adapter = adapter

        adapter.setOnContactClickListener { contact ->
            if (contact.numbers.isNotEmpty()) {
                checkPermissionCall(contact.numbers.first())
            }
        }
        val observerContact = Observer<AppState> { appState ->
            renderDataContacts(appState)
        }
        viewModel.getContactsStart().observe(viewLifecycleOwner, observerContact)
        checkPermissionContact()
    }

    companion object {
        fun newInstance(bundle: Bundle): MovieInfoFragment {
            val fragment = MovieInfoFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private fun renderDataContacts(data: AppState) {
        Log.v("Debug1", "ProfileFragment renderDataContacts")
        when (data) {
            is AppState.SuccessGetContacts -> {
                Log.v("Debug1", "ProfileFragment renderDataContacts SuccessGetContacts")
                binding.rvContacts.show()
                binding.prContact.hide()
                contacts = data.contacts
                adapter.fillContacts(data.contacts)
            }
            is AppState.Loading -> {
                Log.v("Debug1", "ProfileFragment renderDataContacts Loading")
                binding.rvContacts.hide()
                binding.prContact.show()
            }
            else -> Log.v("Debug1", "ProfileFragment renderDataContacts else")
        }
    }

    private fun checkPermissionContact() {
        Log.v("Debug1", "ProfileFragment checkPermissionContact")
        context?.let {
            when {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.READ_CONTACTS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    getContacts(settings.withPhone)
                }
                shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS) -> {
                    AlertDialog.Builder(it)
                        .setTitle(getString(R.string.accessContact))
                        .setMessage(getString(R.string.reasonContact))
                        .setPositiveButton(getString(R.string.setAccessContact)) { _, _ ->
                            requestPermissionLauncherContact.launch(Manifest.permission.READ_CONTACTS)
                        }
                        .setNegativeButton(getString(R.string.noAccess)) { dialog, _ -> dialog.dismiss() }
                        .create()
                        .show()
                }
                else -> requestPermissionLauncherContact.launch(Manifest.permission.READ_CONTACTS)
            }
        }
    }

    private fun makePhoneCall(number: String) {
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:$number")
        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                e.message,
                Toast.LENGTH_SHORT
            ).show()
            e.message
            e.printStackTrace()
        }
    }

    private fun checkPermissionCall(number: String) {
        context?.let {
            when {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.CALL_PHONE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    makePhoneCall(number)
                }
                shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE) -> {
                    AlertDialog.Builder(it)
                        .setTitle(getString(R.string.accsessToCall))
                        .setMessage(getString(R.string.reasonCall))
                        .setPositiveButton(getString(R.string.setAccessCall)) { _, _ ->
                            requestPermissionLauncherCall.launch(Manifest.permission.CALL_PHONE)
                        }
                        .setNegativeButton(getString(R.string.noAccess)) { dialog, _ -> dialog.dismiss() }
                        .create()
                        .show()
                }
                else -> requestPermissionLauncherCall.launch(Manifest.permission.CALL_PHONE)
            }
        }
    }

    private val requestPermissionLauncherContact =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getContacts(settings.withPhone)
            } else {
                context?.let {
                    AlertDialog.Builder(it)
                        .setTitle(getString(R.string.accessContact))
                        .setMessage(getString(R.string.reasonContact))
                        .setNegativeButton(getString(R.string.noAccess)) { dialog, _ -> dialog.dismiss() }
                        .create()
                        .show()
                }
            }
        }

    private val requestPermissionLauncherCall =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
            } else {
                context?.let {
                    AlertDialog.Builder(it)
                        .setTitle(getString(R.string.accsessToCall))
                        .setMessage(getString(R.string.reasonCall))
                        .setNegativeButton(getString(R.string.noAccess)) { dialog, _ -> dialog.dismiss() }
                        .create()
                        .show()
                }
            }
        }

    private fun getContacts(withPhone: Boolean) {
        Log.v("Debug1", "ProfileFragment getContacts")
        viewModel.getContacts(withPhone)
    }
}