package uz.umarxon.mapgetlocationevery15min

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialog
import uz.umarxon.mapgetlocationevery15min.Adapter.RvAdapter
import uz.umarxon.mapgetlocationevery15min.databinding.ActivityMapsBinding
import uz.umarxon.mapgetlocationevery15min.databinding.BottomDialogBinding
import uz.umarxon.mapgetlocationevery15min.service.UploadWork
import uz.umarxon.valyutaarxiv22122021.DB.Database.AppDatabase
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    lateinit var fusedLocatedProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    var marker: Marker? = null

    override fun onMapReady(googleMap: GoogleMap) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION,)
            } else {
                TODO("VERSION.SDK_INT < Q")
            }
            ActivityCompat.requestPermissions(this, permissions, 0)
        } else {

            mMap = googleMap

            fusedLocatedProviderClient = LocationServices.getFusedLocationProviderClient(this)

            binding.menu3.setOnClickListener {
                deviceLocation()
            }

            binding.menu.setOnClickListener {

                val d = BottomSheetDialog(this)

                val i = BottomDialogBinding.inflate(layoutInflater)

                d.setContentView(i.root)

                val adapter = RvAdapter(AppDatabase.getInstance(this).mapDao().getSize())

                i.rv.adapter = adapter

                d.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                d.setCancelable(true)

                d.show()
            }

            if (AppDatabase.getInstance(this).mapDao().getSize().isEmpty()) {
                val build = PeriodicWorkRequestBuilder<UploadWork>(15, TimeUnit.MINUTES)
                    .build()

                WorkManager.getInstance(this).enqueue(build)
            }

            // Add a marker in Sydney and move the camera
            val sydney = LatLng(40.383114011480565, 71.78271019770168)
            marker = mMap.addMarker(
                MarkerOptions().position(sydney).title(
                    "Marker in ${
                        getAddressFromLatLng(
                            this,
                            LatLng(sydney.latitude, sydney.longitude)
                        )
                    }"
                )
            )
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15.0f))

            deviceLocation()

            val l = ArrayList<LatLng>()
            for (i in AppDatabase.getInstance(this).mapDao().getSize()) {
                if (i.isSuccess == true) {
                    l.add(LatLng(i.lat!!, i.lng!!))
                }
            }
            var polyLine = mMap.addPolyline(
                PolylineOptions()
                    .addAll(l)
                    .clickable(true)
                    .color(Color.RED)
            )

            var a = false
            binding.menu2.setOnClickListener {
                if (a) {
                    a = false
                    var polyLine2 = mMap.addPolyline(
                        PolylineOptions()
                            .addAll(l)
                            .clickable(true)
                            .color(Color.RED)
                    )
                } else {
                    if (l.isNotEmpty()) {
                        a = true
                        val polygon2 = mMap.addPolygon(
                            PolygonOptions()
                                .addAll(l)
                                .clickable(true)
                                .fillColor(Color.GREEN)
                        )
                    } else {
                        Toast.makeText(this, "List bo'sh bo'lishi mumkin emas", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }

        }

    }

    @SuppressLint("MissingPermission")
    fun deviceLocation() {
        val locationTask: Task<Location> = fusedLocatedProviderClient.lastLocation
        locationTask.addOnSuccessListener {
            if (it != null) {

                mMap.addMarker(MarkerOptions().position(LatLng(it.latitude, it.longitude)))

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 11.0f))

                Toast.makeText(
                    this,
                    getAddressFromLatLng(this, LatLng(it.latitude, it.longitude)),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun getAddressFromLatLng(context: Context?, latLng: LatLng): String? {
        val geocoder: Geocoder
        val addresses: List<Address>
        geocoder = Geocoder(context, Locale.getDefault())
        return try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            addresses[0].getAddressLine(0)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}