package uz.umarxon.mapgetlocationevery15min.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import uz.umarxon.mapgetlocationevery15min.DB.Entity.ModelMaps
import uz.umarxon.valyutaarxiv22122021.DB.Database.AppDatabase
import java.util.*

class UploadWork(var context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {

    override fun doWork(): Result {

        deviceLocation()

        return Result.success()
    }

    fun deviceLocation() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            val fusedLocatedProviderClient = LocationServices.getFusedLocationProviderClient(context)

            val locationTask = fusedLocatedProviderClient.lastLocation

            locationTask.addOnSuccessListener {
                if (it != null) {
                    AppDatabase.getInstance(context).mapDao().addLocation(ModelMaps(it.latitude,it.longitude,true,"",getAddressFromLatLng(context, LatLng(it.latitude, it.longitude))))
                } else {
                    Log.d("Murodhonov", "deviceLocation value is: null")
                    AppDatabase.getInstance(context).mapDao().addLocation(ModelMaps(0.0,0.0,false,"null",""))
                }
            }
            locationTask.addOnFailureListener {
                Log.d("Murodhonov", it.message.toString())
                AppDatabase.getInstance(context).mapDao().addLocation(ModelMaps(0.0,0.0,false,"${it.message}",""))
            }
        }
    }

    private fun getAddressFromLatLng(context: Context?, latLng: LatLng): String? {
        val addresses: List<Address>
        val geocoder = Geocoder(context, Locale.getDefault())
        return try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            addresses[0].getAddressLine(0)
        } catch (e: Exception) {
            e.printStackTrace()
            "Unnamed road"
        }
    }


}
