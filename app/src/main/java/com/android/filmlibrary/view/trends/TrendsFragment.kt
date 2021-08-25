package com.android.filmlibrary.view.trends

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.filmlibrary.Constant
import com.android.filmlibrary.Constant.NAME_PARCEBLE_MOVIE
import com.android.filmlibrary.Constant.NAVIGATE_FROM_TRENDS_TO_MOVIE_INFO
import com.android.filmlibrary.GlobalVariables
import com.android.filmlibrary.R
import com.android.filmlibrary.databinding.TrendsFragmentBinding
import com.android.filmlibrary.model.AppState
import com.android.filmlibrary.model.data.MoviesByTrend
import com.android.filmlibrary.services.GeoFenceService
import com.android.filmlibrary.view.showSnackBar
import com.android.filmlibrary.viewmodel.thrends.ThrendsFragmentViewModel
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
    private var mMap: GoogleMap? = null
    private lateinit var googleApiClient: GoogleApiClient

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
        googleApiClient.disconnect()
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

        if ((requireActivity().application as GlobalVariables).moviesByTrend.isNotEmpty())
            moviesByTrend = (requireActivity().application as GlobalVariables).moviesByTrend

        adapter.setOnMovieClickListener { movie ->
            val navHostFragment: NavHostFragment =
                activity?.supportFragmentManager?.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            navHostFragment.navController.navigate(
                NAVIGATE_FROM_TRENDS_TO_MOVIE_INFO,  //Вынес в константы
                Bundle().apply {
                    putParcelable(NAME_PARCEBLE_MOVIE, movie)
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

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.mapTrends) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        requestPermissions()
        createGoogleApiClient()
        initNotificationChannel()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        currentMarker = googleMap.addMarker(
            MarkerOptions()
                .position(LatLng(0.0, 0.0))
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_current))
                .title(getString(R.string.CurrentPosition))
        )

        mMap?.let {
            it.setOnMapLongClickListener { latLng ->
                Log.v(
                    "GeoFence",
                    "setOnMapLongClickListener latitude=" + latLng.latitude + ", longitude=" + latLng.longitude
                )
                val marker: Marker? = addMarker(latLng)
                marker?.let {
                    val geofence: Geofence = createGeofence(it)
                    createGeofencingRequest(geofence)
                }
            }
        }
    }

    // Добавление меток на карту
    fun addMarker(location: LatLng): Marker? {
        val title =
            location.latitude.toString() + "," + location.longitude.toString()
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
                    .radius(150.0)
                    .strokeColor(Color.BLUE)
            )
        }
        return marker
    }

    private fun createGeofencingRequest(geofence: Geofence) {
        val builder = GeofencingRequest.Builder()
        // вешаем триггеры на вход, перемещение внутри и выход из зоны
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER or GeofencingRequest.INITIAL_TRIGGER_EXIT or GeofencingRequest.INITIAL_TRIGGER_DWELL)
        builder.addGeofence(geofence) // Добавим геозону
        val geoFenceRequest =
            builder.build() // это запрос на добавление геозоны (параметры только что задавали, теперь строим)
        // создадим интент, при сигнале от Google Play будет вызываться этот интент, а интент настроен на запуск службы, обслуживающей всё это
        val geoService = Intent(requireContext(), GeoFenceService::class.java)
        // интент будет работать через этот класс
        val pendingIntent = PendingIntent
            .getService(requireContext(), 0, geoService, PendingIntent.FLAG_UPDATE_CURRENT)
        // это клиент геозоны, собственно он и занимается вызовом нашей службы
        val geoClient = LocationServices.getGeofencingClient(requireContext())
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions()
        }
        geoClient.addGeofences(
            geoFenceRequest,
            pendingIntent
        ) // добавляем запрос запрос геозоны и указываем, какой интент будет при этом срабатывать
    }

    private fun createGeofence(marker: Marker): Geofence {
        // создаем геозону через построитель.
        return Geofence.Builder()
            .setRequestId(marker.title.toString()) // Здесь указывается имя геозоны (вернее это идентификатор, но он строковый)
            // типа геозоны, вход, перемещение внутри, выход
            .setTransitionTypes(GeofencingRequest.INITIAL_TRIGGER_ENTER or GeofencingRequest.INITIAL_TRIGGER_EXIT or GeofencingRequest.INITIAL_TRIGGER_DWELL)
            .setCircularRegion(
                marker.position.latitude,
                marker.position.longitude,
                150f
            ) // Координаты геозоны
            .setExpirationDuration(Geofence.NEVER_EXPIRE) // Геозона будет постоянной, пока не удалим геозону или приложение
            .setLoiteringDelay(1000) // Установим временную задержку в мс между событиями входа в зону и перемещения в зоне
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
        googleApiClient.connect()
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
            10000,
            10f,
            object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    val lat = location.latitude // Широта
                    val lng = location.longitude // Долгота
                    // Перепестить карту на текущую позицию
                    val currentPosition = LatLng(lat, lng)

                    currentMarker?.let {
                        val prevPosition = it.position
                        if (!(prevPosition.longitude == 0.0 && prevPosition.latitude == 0.0)) {
                            mMap?.addPolyline(
                                PolylineOptions()
                                    .add(prevPosition, currentPosition)
                                    .color(Color.RED)
                                    .width(5f)
                            )
                        }
                        it.position = currentPosition
                    }
                    mMap?.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            currentPosition,
                            15.toFloat()
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
            if (grantResults.size == 1 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                requireActivity().getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
            val importance = NotificationManager.IMPORTANCE_LOW
            val mChannel = NotificationChannel("2", "name", importance)
            notificationManager.createNotificationChannel(mChannel)
        }
    }
}