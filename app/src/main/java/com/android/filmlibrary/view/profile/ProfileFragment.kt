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
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.filmlibrary.Constant
import com.android.filmlibrary.GlobalVariables.Companion.settings
import com.android.filmlibrary.R
import com.android.filmlibrary.model.AppState
import com.android.filmlibrary.model.data.Contact
import com.android.filmlibrary.view.hide
import com.android.filmlibrary.view.show
import com.android.filmlibrary.view.trends.TrendsFragment
import com.android.filmlibrary.viewmodel.profile.ProfileViewModel

class ProfileFragment : Fragment() {

    private var contacts: List<Contact> = ArrayList()
    private lateinit var recyclerView: RecyclerView
    private lateinit var switchAdult: SwitchCompat
    private lateinit var switchGeoFence: SwitchCompat
    private lateinit var switchWithPhone: SwitchCompat
    private lateinit var rvContacts: RecyclerView
    private lateinit var prContact: ProgressBar

    private val adapter: ContactsAdapter by lazy {
        ContactsAdapter()
    }

    private val viewModel: ProfileViewModel by lazy {
        ViewModelProvider(this).get(ProfileViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.profile_fragment, container, false)
        switchAdult = view.findViewById(R.id.switchAdult)
        switchGeoFence = view.findViewById(R.id.switchGeoFence)
        switchWithPhone = view.findViewById(R.id.switchWithPhone)

        rvContacts = view.findViewById(R.id.rv_contacts)

        prContact = view.findViewById(R.id.prContact)

        return view
    }

    override fun onDestroyView() {

        super.onDestroyView()
        Log.v("Debug1", "ProfileFragment onDestroyView")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.v("Debug1", "ProfileFragment onViewCreated savedInstanceState=" + savedInstanceState)

        switchAdult.isChecked =
            settings.adult
        switchAdult.setOnCheckedChangeListener { _, isChecked ->
            settings.adult = isChecked
        }

        switchGeoFence.isChecked =
            settings.geoFence
        switchGeoFence.setOnClickListener {
            settings.geoFence = switchGeoFence.isChecked

            if (!switchGeoFence.isChecked) {
                val trendsFragment = TrendsFragment.newInstance()
                trendsFragment.removeGeoFence(requireActivity())
            }
        }

        switchWithPhone.isChecked =
            settings.withPhone
        switchWithPhone.setOnCheckedChangeListener { _, isChecked ->
            settings.withPhone = isChecked
            getContacts(isChecked)
        }

        recyclerView = rvContacts
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

    private fun renderDataContacts(data: AppState) {
        when (data) {
            is AppState.SuccessGetContacts -> {
                rvContacts.show()
                prContact.hide()
                contacts = data.contacts
                adapter.fillContacts(data.contacts)
            }
            is AppState.Loading -> {
                rvContacts.hide()
                prContact.show()
            }
            else -> Log.v("Debug1", "ProfileFragment renderDataContacts else")
        }
    }

    private fun checkPermissionContact() {
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
        viewModel.getContacts(withPhone)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v("Debug1", "ProfileFragment onCreate")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Log.v("Debug1", "ProfileFragment onSaveInstanceState")
        super.onSaveInstanceState(outState)
        Log.v("Debug1", "ProfileFragment onSaveInstanceState2")
    }
}