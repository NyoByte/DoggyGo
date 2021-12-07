package pe.edu.ulima.doggygo.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import pe.edu.ulima.doggygo.R
import pe.edu.ulima.doggygo.model.User
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.location.*
import com.google.android.gms.maps.*

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.type.DateTime
import java.util.*
import kotlin.time.Duration.Companion.seconds


class PaseoDetalleWalkerFragment: Fragment(),
    OnMapReadyCallback {

    private var user: User? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var map: GoogleMap
    private lateinit var mapView: MapView
    companion object {
        const val REQUEST_CODE_LOCATION = 0
    }

    private var lat:Double = 0.0
    private var lng:Double = 0.0
    private lateinit var listLatLng: MutableList<LatLng>
    private var activeWalk:Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v:View = inflater.inflate(R.layout.fragment_paseo_detalle_walker, container, false)
        user = arguments?.getSerializable("user") as User
        mapView = v.findViewById(R.id.mapView) as MapView
        initGoogleMap(savedInstanceState)
        return v
    }

    private fun initGoogleMap(savedInstanceState: Bundle?){
        var mapViewBundle:Bundle? = null
        if(savedInstanceState!=null){
            mapViewBundle = savedInstanceState.getBundle("MapViewBundleKey")
        }
        mapView.onCreate(mapViewBundle)
        mapView.getMapAsync(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        getLastLocation()

        view.findViewById<Button>(R.id.btnIniciar).setOnClickListener {
            activeWalk = true
            listLatLng = MutableList<LatLng>(1) {
                LatLng(lat, lng)
            }
            view.findViewById<Button>(R.id.btnIniciar).isEnabled = false
            view.findViewById<Button>(R.id.btnFinalizar).isEnabled = true
        }

        view.findViewById<Button>(R.id.btnFinalizar).setOnClickListener {
            activeWalk = false
            listLatLng.forEach {
                println("Lat: ${it.latitude} - Lng: ${it.longitude}")
            }
            println(listLatLng.size)
            view.findViewById<Button>(R.id.btnFinalizar).isEnabled = false
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        var mapViewBundle = outState.getBundle("MapViewBundleKey")
        if(mapViewBundle == null){
            mapViewBundle = Bundle()
            outState.putBundle("MapViewBundleKey", mapViewBundle)
        }
        mapView.onSaveInstanceState(mapViewBundle)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.addMarker(
            MarkerOptions()
                .position(LatLng(-12.037642, -76.929706))
                .title("Marker1")
        )
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return
        }
        map.isMyLocationEnabled = true
        //googleMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(-12.037642,-76.929706)))
        //map.setOnMyLocationButtonClickListener(this)
        //map.setOnMyLocationClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
        mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    // Permisos
    private fun getLastLocation(){
        val permission = ContextCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)

        val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { habilitado ->
            if (habilitado) {
                getLocation()
                map.setMyLocationEnabled(true)
            }
        }

        if(permission != PackageManager.PERMISSION_GRANTED){
            //No tenemos permisos:
            val noPodemosPedirPermisos = ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
            if (noPodemosPedirPermisos){
                Toast.makeText(requireActivity(), "Habilitar permisos manualmente", Toast.LENGTH_SHORT).show()
            }else {
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION),100)
                requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }else{
            //Sí tenemos permisos:
            getLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        // Ya tenemos permisos, podemos obtener localizacion
        fusedLocationClient.lastLocation.addOnSuccessListener {
            lat = it.latitude
            lng = it.longitude
            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(LatLng(lat,lng),18f)
            )
        }
        fusedLocationClient.lastLocation.addOnFailureListener {
            Log.e("APIGoogle", it.message!!)
        }

    }
    @SuppressLint("MissingPermission")
    fun startLocationUpdates(){
        // Configurar para obtener localizacion mas actualizada
        locationCallback = object : LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult?){
                if(locationResult != null){
                    for(location in locationResult.locations) {
                        Log.d("getLocationGoogle", "${location.latitude} , ${location.longitude} - ${Date().seconds}")
                        if(activeWalk) {
                            val newLatLng = LatLng(location.latitude, location.longitude)
                            listLatLng.add(newLatLng)
                        }
                    }
                }
            }
        }
        fusedLocationClient.requestLocationUpdates(createLocationRequest(), locationCallback, Looper.getMainLooper())
    }

    fun createLocationRequest(): LocationRequest {
        val locationRequest = LocationRequest.create().apply{
            this.interval = 10000 //seg
            this.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        return locationRequest
    }
}