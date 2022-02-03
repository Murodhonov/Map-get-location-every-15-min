package uz.umarxon.mapgetlocationevery15min.DB.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

@Entity
class ModelMaps:Serializable {
    @PrimaryKey
    var id: Int? = null

    var lat:Double? = null
    var lng:Double? = null
    var time:String = SimpleDateFormat("dd/MM/yyyy | hh:mm:ss 'Z'").format(Date())
    var isSuccess:Boolean? = null
    var message:String? = null
    var address:String? = null

    constructor()
    constructor(
        lat: Double?,
        lng: Double?,
        isSuccess: Boolean?,
        message: String?,
        address: String?
    ) {
        this.lat = lat
        this.lng = lng
        this.isSuccess = isSuccess
        this.message = message
        this.address = address
    }


}