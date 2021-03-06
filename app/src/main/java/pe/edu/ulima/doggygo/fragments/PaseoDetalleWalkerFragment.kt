package pe.edu.ulima.doggygo.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Paint
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
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
import com.google.android.gms.maps.model.*

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.google.type.DateTime
import pe.edu.ulima.doggygo.manager.UserManager
import pe.edu.ulima.doggygo.manager.WalkManager
import pe.edu.ulima.doggygo.model.Walk
import java.util.*
import kotlin.time.Duration.Companion.seconds


class PaseoDetalleWalkerFragment: Fragment(),
    OnMapReadyCallback {

    private var user: User? = null
    private var walk: Walk? = null
    private lateinit var walkManager:WalkManager

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var map: GoogleMap
    private lateinit var mapView: MapView
    companion object {
        const val REQUEST_CODE_LOCATION = 0
    }

    private var lat:Double = 0.0
    private var lng:Double = 0.0
    private lateinit var listLatLngCurrent: MutableList<GeoPoint>
    private var activeWalk:Boolean = false
    private var flag:Boolean = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v:View = inflater.inflate(R.layout.fragment_paseo_detalle_walker, container, false)
        user = arguments?.getSerializable("user") as User
        walk = arguments?.getSerializable("walk") as Walk
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
        walkManager =  WalkManager(requireActivity().applicationContext)

        val btnIniciar = view.findViewById<Button>(R.id.btnIniciar)
        val btnFinalizar= view.findViewById<Button>(R.id.btnFinalizar)
        val cboPee = view.findViewById<CheckBox>(R.id.cboPee)
        val cboPoo = view.findViewById<CheckBox>(R.id.cboPoo)
        val tviTime = view.findViewById<TextView>(R.id.tviTime)
        val tviPetName = view.findViewById<TextView>(R.id.tviPetName)
        val tviDate = view.findViewById<TextView>(R.id.tviDate)
        val tviDuration = view.findViewById<TextView>(R.id.tviDuration)
        val tviDistance = view.findViewById<TextView>(R.id.tviDistance)

        tviPetName.text = walk!!.dogName
        if(walk!!.time >=12){
            tviDate.text = "${walk!!.date} - ${walk!!.time-12}pm"
        }else{
            tviDate.text = "${walk!!.date} - ${walk!!.time}am"
        }
        tviDuration.text = "${walk!!.duration}min"

        if(walk!!.walkStarted == null && walk!!.walkEnded == null){
            flag = true
            activeWalk = false
            btnIniciar.isEnabled = true
            btnFinalizar.isEnabled = false
            tviTime.text = "0min"
        }else if (walk!!.walkStarted != null && walk!!.walkEnded == null){
            flag = false
            activeWalk = true
            btnIniciar.isEnabled = false
            btnFinalizar.isEnabled = true
            var currentTime = (Timestamp.now().toDate().time - walk!!.walkStarted?.toDate()!!.time)
            //currentTime = currentTime.div(6000)
            tviTime.text = currentTime.toString() + "min"
        }else if (walk!!.walkStarted != null && walk!!.walkEnded != null){
            flag = false
            activeWalk = false
            btnIniciar.isEnabled = false
            btnFinalizar.isEnabled = false
            cboPee.isEnabled = false
            cboPoo.isEnabled = false
            println("TEST NYO START====================================\n")
            println("\nCheckbox Peee: "+cboPee.isChecked)
            cboPee!!.isChecked = walk!!.pee
            cboPoo.setChecked(walk!!.poo)
            println("\nPee Firebase: "+walk!!.pee)
            tviDistance.text = walk!!.pee.toString()
            println("\nCheckbox Peee: "+cboPee.isChecked)
            println("TEST NYO STOP====================================")
            val currentTime = (walk!!.walkEnded?.toDate()!!.time - walk!!.walkStarted?.toDate()!!.time)
            val TimeMin = currentTime/60000
            val TimeSeg = (currentTime%60000)/1000
            if(TimeMin==0L){
                tviTime.text = TimeSeg.toString() + "seg"
            }else{
                tviTime.text = TimeMin.toString() + "min" + TimeSeg.toString() + "seg"
            }

        }

        btnIniciar.setOnClickListener {
            activeWalk = true
            listLatLngCurrent = MutableList<GeoPoint>(1){
                GeoPoint(lat,lng)
            }
            btnIniciar.isEnabled = false
            btnFinalizar.isEnabled = true
            val newWalk = walk!!.apply {
                this.walkStarted = Timestamp.now()
            }
            walkManager.updateWalk(newWalk.id, newWalk)
        }

        btnFinalizar.setOnClickListener {
            activeWalk = false
            val newWalk = walk!!.apply {
                this.walkEnded = Timestamp.now()
                this.listLatLng = listLatLngCurrent
                this.pee = cboPee.isChecked
                this.poo = cboPoo.isChecked
            }
            walkManager.updateWalk(newWalk.id, newWalk)
            btnFinalizar.isEnabled = false
            polylineWalk()
        }

        view.findViewById<Button>(R.id.btnTest).setOnClickListener {
            println("============|${cboPee.isChecked}|============")
            if(cboPee.isChecked){
                cboPee.isChecked = false
            }else{
                cboPee.isChecked = true
            }
        }
    }

    private fun polylineWalk(){
        listLatLngCurrent = walk!!.listLatLng!!
        val polylineOpt = PolylineOptions()
        listLatLngCurrent.forEach {
            polylineOpt.add(LatLng(it.latitude, it.longitude))
        }

        map.clear()
        map.addPolyline(polylineOpt.geodesic(true))
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
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return
        }
        map.isMyLocationEnabled = true
        if(walk!!.listLatLng != null){
            polylineWalk()
            val startMark = MarkerOptions()
                .position(LatLng(walk!!.listLatLng!![0].latitude, walk!!.listLatLng!![0].longitude))
                .title("Inicio")
            map.addMarker(startMark)
        }
        if(walk!!.walkStarted != null && walk!!.walkEnded != null){
            polylineWalk()
            val startMark = MarkerOptions()
                .position(LatLng(walk!!.listLatLng!![0].latitude, walk!!.listLatLng!![0].longitude))
                .title("Inicio")
            val eFin = walk!!.listLatLng!!.size - 1
            val endMark = MarkerOptions()
                .position(LatLng(walk!!.listLatLng!![eFin].latitude, walk!!.listLatLng!![eFin].longitude))
                .title("Final")
            map.addMarker(startMark)
            map.addMarker(endMark)
        }
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
            //S?? tenemos permisos:
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
                            Log.i("NyoTest",walk!!.id)
                            val newLatLng = GeoPoint(location.latitude, location.longitude)
                            listLatLngCurrent.add(newLatLng)
                        }else{
                            if(!flag){
                                listLatLngCurrent = walk!!.listLatLng!!
                            }
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