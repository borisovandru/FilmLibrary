package com.android.filmlibrary.view.trends

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.filmlibrary.Constant
import com.android.filmlibrary.Constant.DEFAULT_LAT
import com.android.filmlibrary.Constant.DEFAULT_LONG
import com.android.filmlibrary.Constant.DEFAULT_U
import com.android.filmlibrary.Constant.DEFAULT_V
import com.android.filmlibrary.Constant.GEOFENCE_DEFAULT_WIDTH
import com.android.filmlibrary.Constant.GEOFENCE_MIN_DIST
import com.android.filmlibrary.Constant.GEOFENCE_MIN_TIME
import com.android.filmlibrary.Constant.GEOFENCE_MOVE_CAM
import com.android.filmlibrary.Constant.GEOFENCE_PAR_1
import com.android.filmlibrary.Constant.GEOFENCE_RADIUS
import com.android.filmlibrary.Constant.GEOFENCE_TIMEOUT
import com.android.filmlibrary.Constant.NAME_PARCEBLE_MOVIE
import com.android.filmlibrary.Constant.NAVIGATE_FROM_TRENDS_TO_MOVIE_INFO
import com.android.filmlibrary.Constant.NOTIFY_CH
import com.android.filmlibrary.Constant.NOTIFY_NAME
import com.android.filmlibrary.GlobalVariables.Companion.settings
import com.android.filmlibrary.R
import com.android.filmlibrary.databinding.TrendsFragmentBinding
import com.android.filmlibrary.model.AppState
import com.android.filmlibrary.model.data.Cinema
import com.android.filmlibrary.services.GeoFenceService
import com.android.filmlibrary.view.showSnackBar
import com.android.filmlibrary.viewmodel.thrends.TrendsFragmentViewModel
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class TrendsFragment : Fragment(), OnMapReadyCallback {

    var currentMarker: Marker? = null
    private var geoFenceOn: Boolean = true
    private var mMap: GoogleMap? = null

    private lateinit var googleApiClient: GoogleApiClient

    companion object {
        fun newInstance() = TrendsFragment()
        private var cinemas = mutableListOf<Cinema>()
    }

    private lateinit var recyclerView: RecyclerView

    private val adapter = TrendsFragmentAdapter()
    private val viewModel: TrendsFragmentViewModel by lazy {
        ViewModelProvider(this).get(TrendsFragmentViewModel::class.java)
    }

    private var _binding: TrendsFragmentBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = TrendsFragmentBinding.inflate(inflater, container, false)

        geoFenceOn = settings.geoFence

        return _binding?.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onStop() {
        super.onStop()

        if (geoFenceOn) {
            googleApiClient.disconnect()
        }
    }

    private fun renderTrends(data: AppState) {
        when (data) {
            is AppState.SuccessMoviesByTrends -> {
                binding.loadingLayoutTrend.visibility = View.GONE
                adapter.fillMoviesByTrend(data.moviesByTrends)
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

        adapter.setOnMovieClickListener { movie ->
            val navHostFragment: NavHostFragment? =
                activity?.supportFragmentManager?.findFragmentById(R.id.nav_host_container) as? NavHostFragment
            navHostFragment?.let {
                it.navController.navigate(
                    NAVIGATE_FROM_TRENDS_TO_MOVIE_INFO,
                    Bundle().apply {
                        putParcelable(NAME_PARCEBLE_MOVIE, movie)
                    }
                )
            }
        }

        val observer = Observer<AppState> { appState ->
            renderTrends(appState)
        }
        viewModel.getData().observe(viewLifecycleOwner, observer)
        viewModel.getTrendsFromRemoteSource()

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.mapTrends) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        if (geoFenceOn) {
            binding.headerCinema.visibility = View.VISIBLE
            binding.mapTrends.visibility = View.VISIBLE
            requestPermissions()
            createGoogleApiClient()
            initNotificationChannel()
        } else {
            binding.headerCinema.visibility = View.GONE
            binding.mapTrends.visibility = View.GONE
        }
    }

    private fun setCinemaAddress() {
        cinemas.forEach { item ->
            val itemLocation = LatLng(
                item.coordinates.latitude,
                item.coordinates.longitude
            )
            val marker: Marker? = addMarker(itemLocation, item.name)
            marker?.let {
                val geofence: Geofence = createGeofence(it)
                item.pendingIntent = createGeofencingRequest(geofence)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        currentMarker = googleMap.addMarker(
            MarkerOptions()
                .position(LatLng(DEFAULT_LAT, DEFAULT_LONG))
                .anchor(DEFAULT_U, DEFAULT_V)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_current))
                .title(getString(R.string.CurrentPosition))
        )

        val latitude = resources.getStringArray(R.array.latitude)
        val longitude = resources.getStringArray(R.array.longitude)
        val cinemaNames = resources.getStringArray(R.array.cinemaNames)

        if (latitude.size == longitude.size) {
            for (i in latitude.indices) {
                cinemas.add(
                    Cinema(
                        cinemaNames[i],
                        LatLng(latitude[i].toDouble(), longitude[i].toDouble()), null
                    )
                )
            }
        }
        if (geoFenceOn) {
            setCinemaAddress()
        }

        mMap?.let { googleMap1 ->
            googleMap1.setOnMapLongClickListener { latLng ->
                Log.v(
                    "GeoFence",
                    "setOnMapLongClickListener latitude=" + latLng.latitude + ", longitude=" + latLng.longitude
                )
                val marker: Marker? = addMarker(latLng, "")
                marker?.let {
                    val geofence: Geofence = createGeofence(it)
                    createGeofencingRequest(geofence)
                }
            }
        }
    }

    // Добавление меток на карту
    private fun addMarker(location: LatLng, nameFence: String): Marker? {
        val title =
            if (nameFence == "") {
                location.latitude.toString() + "," + location.longitude.toString()
            } else {
                nameFence
            }

        var marker: Marker? = null
        mMap?.let {
            marker = it.addMarker(
                MarkerOptions()
                    .position(location)
                    .title(title)
            )
            it.addCircle(
                CircleOptions()
                    .center(location)
                    .radius(GEOFENCE_RADIUS)
                    .strokeColor(Color.BLUE)
            )
        }
        return marker
    }

    private fun createGeofencingRequest(geofence: Geofence): PendingIntent {
        val builder = GeofencingRequest.Builder()
        // вешаем триггеры на вход, перемещение внутри и выход из зоны
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER or GeofencingRequest.INITIAL_TRIGGER_EXIT or GeofencingRequest.INITIAL_TRIGGER_DWELL)
        builder.addGeofence(geofence) // Добавим геозону
        val geoFenceRequest =
            builder.build() // это запрос на добавление геозоны (параметры только что задавали, теперь строим)
        // создадим интент, при сигнале от Google Play будет вызываться этот интент, а интент настроен на запуск службы, обслуживающей всё это
        val geoService = Intent(requireActivity(), GeoFenceService::class.java)
        // интент будет работать через этот класс
        val pendingIntent = PendingIntent
            .getService(requireContext(), 0, geoService, PendingIntent.FLAG_UPDATE_CURRENT)
        // это клиент геозоны, собственно он и занимается вызовом нашей службы
        val geoClient = LocationServices.getGeofencingClient(requireActivity())
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions()
        }
        geoClient.addGeofences(
            geoFenceRequest,
            pendingIntent
        ) // добавляем запрос запрос геозоны и указываем, какой интент будет при этом срабатывать
        return pendingIntent
    }

    fun removeGeoFence(activity: Activity) {
        Log.v("Debug1", "removeGeoFence")
        val geoClient = LocationServices.getGeofencingClient(activity)

        cinemas.forEach { item ->

            item.pendingIntent?.let {
                geoClient.removeGeofences(it).run {
                    addOnSuccessListener {
                        Log.v("Debug1", "removeGeoFence Success")
                        Toast.makeText(
                            activity,
                            "Удалена ГеоЗона " + item.name,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    addOnFailureListener {
                        Log.v("Debug1", "removeGeoFence Failure")
                        Toast.makeText(
                            activity,
                            "Не смог удалить ГеоЗону " + item.name,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun createGeofence(marker: Marker): Geofence {
        // создаем геозону через построитель.
        var titleGeoFence = ""
        marker.title?.let {
            titleGeoFence = it
        }
        return Geofence.Builder()
            .setRequestId(titleGeoFence) // Здесь указывается имя геозоны (вернее это идентификатор, но он строковый)
            // типа геозоны, вход, перемещение внутри, выход
            .setTransitionTypes(GeofencingRequest.INITIAL_TRIGGER_ENTER or GeofencingRequest.INITIAL_TRIGGER_EXIT or GeofencingRequest.INITIAL_TRIGGER_DWELL)
            .setCircularRegion(
                marker.position.latitude,
                marker.position.longitude,
                GEOFENCE_PAR_1
            ) // Координаты геозоны
            .setExpirationDuration(Geofence.NEVER_EXPIRE) // Геозона будет постоянной, пока не удалим геозону или приложение
            .setLoiteringDelay(GEOFENCE_TIMEOUT) // Установим временную задержку в мс между событиями входа в зону и перемещения в зоне
            .build()
    }

    // Геозоны работают через службы Google Play
    // поэтому надо создать клиента этой службы
    // И соединится со службой
    private fun createGoogleApiClient() {
        // Создаем клиента службы GooglePlay
        googleApiClient = GoogleApiClient.Builder(requireContext())
            .addApi(LocationServices.API) // Укажем, что нам нужна геолокация
            .build()
    }

    override fun onStart() {
        super.onStart()
        if (geoFenceOn) {
            googleApiClient.connect()
        }
        // Соединимся со службой
        Log.v("GeoFence", "connect to googleApiClient")
    }

    // Запрос координат
    private fun requestLocation() {
        // Если пермиссии все таки нет - то просто выйдем, приложение не имеет смысла
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return
        // Получить менеджер геолокаций
        val locationManager =
            requireActivity().getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        // Будем получать геоположение через каждые 10 секунд или каждые 10 метров
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
            GEOFENCE_MIN_TIME,
            GEOFENCE_MIN_DIST,
            object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    val lat = location.latitude // Широта
                    val lng = location.longitude // Долгота
                    // Перепестить карту на текущую позицию
                    val currentPosition = LatLng(lat, lng)

                    currentMarker?.let {
                        val prevPosition = it.position
                        if (!(prevPosition.longitude == DEFAULT_LAT && prevPosition.latitude == DEFAULT_LONG)) {
                            mMap?.addPolyline(
                                PolylineOptions()
                                    .add(prevPosition, currentPosition)
                                    .color(Color.RED)
                                    .width(GEOFENCE_DEFAULT_WIDTH)
                            )
                        }
                        it.position = currentPosition
                    }
                    mMap?.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            currentPosition,
                            GEOFENCE_MOVE_CAM
                        )
                    )
                }

                override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            })
    }

    // Запрос пермиссий
    private fun requestPermissions() {
        // Проверим на пермиссии, и если их нет, запросим у пользователя
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // запросим координаты
            requestLocation()
        } else {
            // пермиссии нет, будем запрашивать у пользователя
            requestLocationPermissions()
        }
    }

    // Запрос пермиссии для геолокации
    private fun requestLocationPermissions() {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            // Запросим эти две пермиссии у пользователя
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                Constant.PERMISSION_REQUEST_CODE
            )
        }
    }

    // Это результат запроса у пользователя пермиссии
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constant.PERMISSION_REQUEST_CODE) {   // Это та самая пермиссия, что мы запрашивали?
            if (grantResults.isNotEmpty() &&
                grantResults.first() == PackageManager.PERMISSION_GRANTED
            ) {
                // Все препоны пройдены и пермиссия дана
                // Запросим координаты
                requestLocation()
            }
        }
    }

    // На Андроидах версии 26 и выше необходимо создавать канал нотификации
    // На старых версиях канал создавать не надо
    private fun initNotificationChannel() {
        val notificationManager =
            requireActivity().getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
        val importance = NotificationManager.IMPORTANCE_LOW
        val mChannel = NotificationChannel(NOTIFY_CH, NOTIFY_NAME, importance)
        notificationManager.createNotificationChannel(mChannel)
    }
}