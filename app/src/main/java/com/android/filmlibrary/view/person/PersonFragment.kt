package com.android.filmlibrary.view.person

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.android.filmlibrary.Constant
import com.android.filmlibrary.R
import com.android.filmlibrary.databinding.PersonFragmentBinding
import com.android.filmlibrary.model.AppState
import com.android.filmlibrary.model.data.PersonMini
import com.android.filmlibrary.view.showSnackBar
import com.android.filmlibrary.viewmodel.person.PersonViewModel
import com.bumptech.glide.Glide

class PersonFragment : Fragment() {

    private val viewModel: PersonViewModel by lazy {
        ViewModelProvider(this).get(PersonViewModel::class.java)
    }
    private var personMini: PersonMini? = null
    private var personId: Int = -1
    private var _binding: PersonFragmentBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = PersonFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun renderData(data: AppState) {
        when (data) {
            is AppState.SuccessGetPerson -> {
                val person = data.person

                binding.birthdayPerson.visibility = View.VISIBLE
                person.birthday?.let {
                    binding.birthdayPerson.text = it
                }

                binding.placeBirthPerson.visibility = View.VISIBLE
                person.placeOfBirth?.let { address ->
                    binding.placeBirthPerson.text = address
                    binding.placeBirthPerson.setOnClickListener {

                        val bundle = Bundle()
                        bundle.putString(Constant.NAME_PARCEBLE_MAP, address)
                        val navHostFragment: NavHostFragment =
                            activity?.supportFragmentManager?.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                        navHostFragment.navController.navigate(
                            Constant.NAVIGATE_FROM_PERSON_TO_MAP,
                            bundle
                        )
                    }
                }

                binding.biographyPerson.visibility = View.VISIBLE
                person.biography?.let {
                    binding.biographyPerson.text = it
                }

                binding.progressBarbirthdayPerson.visibility = View.GONE
                binding.progressBarPlaceBirthPerson.visibility = View.GONE
                binding.progressBarBiographyPerson.visibility = View.GONE

            }
            is AppState.Loading -> {
                binding.progressBarbirthdayPerson.visibility = View.VISIBLE
                binding.progressBarPlaceBirthPerson.visibility = View.VISIBLE
                binding.progressBarBiographyPerson.visibility = View.VISIBLE
            }
            is AppState.Error -> {
                binding.progressBarbirthdayPerson.visibility = View.VISIBLE
                binding.progressBarPlaceBirthPerson.visibility = View.VISIBLE
                binding.progressBarBiographyPerson.visibility = View.VISIBLE
                data.error.message?.let {
                    binding.progressBarBiographyPerson.showSnackBar(it, R.string.ReloadMsg) {
                        viewModel.getPersonFromRemoteSource(personId)
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            personMini = it.getParcelable(BUNDLE_EXTRA)

            personMini?.let { personMini1 ->

                personMini1.profilePath?.let { profilePage ->
                    if (profilePage != "" && profilePage != "-") {
                        Glide.with(this)
                            .load(Constant.BASE_IMAGE_URL + Constant.IMAGE_POSTER_SIZE_1 + profilePage)
                            .into(binding.imagePerson)
                    } else {
                        binding.imagePerson.setImageResource(Constant.EMPTY_POSTER)
                    }
                }
                binding.namePerson.text = personMini1.name

                personId = personMini1.id
                personId.let { personId ->
                    //Ставим наблюдателя на получения данных о персоне
                    val observer = Observer<AppState> { appState ->
                        renderData(appState)
                    }
                    viewModel.getPersonStart()
                        .observe(viewLifecycleOwner, observer)
                    viewModel.getPersonFromRemoteSource(personId)

                }
            }
        }

    }

    companion object {
        const val BUNDLE_EXTRA = Constant.NAME_PARCEBLE_PERSON

        fun newInstance(bundle: Bundle): PersonFragment {
            val fragment = PersonFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}