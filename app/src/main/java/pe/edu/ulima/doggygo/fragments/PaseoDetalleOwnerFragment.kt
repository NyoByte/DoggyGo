package pe.edu.ulima.doggygo.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import pe.edu.ulima.doggygo.R
import pe.edu.ulima.doggygo.manager.ReviewManager
import pe.edu.ulima.doggygo.manager.WalkManager
import pe.edu.ulima.doggygo.model.User
import pe.edu.ulima.doggygo.model.Walk
import java.util.*

class PaseoDetalleOwnerFragment: Fragment(),
    OnMapReadyCallback {

    private var user: User? = null
    private var walk: Walk? = null
    private var telf: String? = null
    private lateinit var walkManager: WalkManager

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
        val v: View = inflater.inflate(R.layout.fragment_paseo_detalle_owner, container, false)
        user = arguments?.getSerializable("user") as User
        walk = arguments?.getSerializable("walk") as Walk
        telf = arguments?.getSerializable("telf") as String
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
        val btnCancelar = view.findViewById<Button>(R.id.btnCancelar)
        val btnCalificar= view.findViewById<Button>(R.id.btnCalificar)
        val btnPhone = view.findViewById<Button>(R.id.btnPhone)
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
            tviTime.text = "0min"
            btnCancelar.isEnabled = true
            btnCalificar.isEnabled = false
        }else if (walk!!.walkStarted != null && walk!!.walkEnded == null){
            flag = false
            activeWalk = true
            val currentTime = (Timestamp.now().toDate().time - walk!!.walkStarted?.toDate()!!.time)
            val timeMin = currentTime/60000
            val timeSeg = (currentTime%60000)/1000
            if(timeMin==0L){
                tviTime.text = timeSeg.toString() + "seg"
            }else{
                tviTime.text = timeMin.toString() + "min" + timeSeg.toString() + "seg"
            }
            btnCancelar.isEnabled = false
            btnCalificar.isEnabled = false
        }else if (walk!!.walkStarted != null && walk!!.walkEnded != null){
            val currentTime = (walk!!.walkEnded?.toDate()!!.time - walk!!.walkStarted?.toDate()!!.time)
            val timeMin = currentTime/60000
            val timeSeg = (currentTime%60000)/1000
            if(timeMin==0L){
                tviTime.text = timeSeg.toString() + "seg"
            }else{
                tviTime.text = timeMin.toString() + "min" + timeSeg.toString() + "seg"
            }
            btnCancelar.isEnabled = false
            btnCalificar.isEnabled = true

            cboPee.isChecked = walk?.pee!!
            cboPoo.isChecked = walk?.poo!!
        }

        btnCancelar.setOnClickListener{
            Log.e("ButtonCancelar", "click")
        }

        btnCalificar.setOnClickListener{
            val materialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext())

            val customDialog = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_rate_walk, null, false)
            val rbaCalificarAnuncio = customDialog.findViewById<RatingBar>(R.id.rbaCalificarAnuncio)
            val eteCommentCalificarAnuncio = customDialog.findViewById<EditText>(R.id.eteCommentCalificarAnuncio)

            val reviewManager = ReviewManager(requireContext())

            materialAlertDialogBuilder.setView(customDialog)
                .setTitle("Calificar")
                .setMessage("Ingrese la calificación y un comentario")
                .setPositiveButton("Calificar"){ dialog, _ ->
                    reviewManager.createReview(user?.id!!, walk?.dogWalkerId!!, rbaCalificarAnuncio.rating, eteCommentCalificarAnuncio.text.toString(), {
                        Toast.makeText(requireActivity(), "Calificación publicada correctamente", Toast.LENGTH_SHORT).show()
                    }, {
                        Toast.makeText(requireActivity(), "Ocurrió un error al publicar la calificación", Toast.LENGTH_SHORT).show()
                    })
                    dialog.dismiss()
                }
                .setNegativeButton("Cancelar"){dialog, _ ->
                    Toast.makeText(requireActivity(), "Operación cancelada", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                .show()
        }

        btnPhone.text = "${telf}"
        btnPhone.setOnClickListener{
            Log.e("ButtonPhone", "click")
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:<${telf}>")
            startActivity(intent)
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
                if(walk?.walkEnded != null && walk?.listLatLng != null){
                    map.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(LatLng(walk!!.listLatLng!![0].latitude, walk!!.listLatLng!![0].longitude),18f)
                    )
                }else {
                    map.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), 18f)
                    )
                }
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
                        if(activeWalk) {
                            println("")
                        }else{
                            println("")
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