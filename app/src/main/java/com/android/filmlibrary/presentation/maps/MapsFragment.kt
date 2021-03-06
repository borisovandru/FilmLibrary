package com.android.filmlibrary.presentation.maps

import android.location.Address
import android.location.Geocoder
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.android.filmlibrary.utils.Constant
import com.android.filmlibrary.utils.Constant.MAPS_ZOOM
import com.android.filmlibrary.utils.Constant.MAX_RESULT_GEOCODER
import com.android.filmlibrary.R
import com.android.filmlibrary.databinding.MapsFragmentBinding
import java.io.IOException

class MapsFragment : Fragment() {

    private var _binding: MapsFragmentBinding? = null
    private val binding
        get() = _binding!!

    private lateinit var map: GoogleMap

    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap
        arguments?.let { bundle ->
            val address = bundle.getString(BUNDLE_EXTRA)
            address?.let {
                initSearchByAddress(it)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = MapsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun initSearchByAddress(searchText: String) {
        val geoCoder = Geocoder(requireContext())
        try {
            val addresses = geoCoder.getFromLocationName(searchText, MAX_RESULT_GEOCODER)
            if (addresses.isNotEmpty()) {
                goToAddress(addresses, binding.mapCont, searchText)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun goToAddress(
        addresses: MutableList<Address>,
        view: View,
        searchText: String,
    ) {
        val location = LatLng(
            addresses.first().latitude,
            addresses.first().longitude
        )
        view.post {
            setMarker(location, searchText)
            map.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    location,
                    MAPS_ZOOM
                )
            )
        }
    }

    private fun setMarker(
        location: LatLng,
        searchText: String,
    ): Marker? {
        return map.addMarker(
            MarkerOptions()
                .position(location)
                .title(searchText)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker))
        )
    }

    companion object {
        const val BUNDLE_EXTRA = Constant.NAME_PARCEBLE_MAP

        fun newInstance(bundle: Bundle): MapsFragment {
            val fragment = MapsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}