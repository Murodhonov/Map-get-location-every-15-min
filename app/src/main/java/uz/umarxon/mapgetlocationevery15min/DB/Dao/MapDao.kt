package uz.umarxon.mapgetlocationevery15min.DB.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.rxjava3.core.Flowable
import uz.umarxon.mapgetlocationevery15min.DB.Entity.ModelMaps

@Dao
interface MapDao {

    @Query("select * from modelmaps")
    fun getAllLocations(): Flowable<List<ModelMaps>>

    @Query("select * from modelmaps")
    fun getSize(): List<ModelMaps>

    @Insert
    fun addLocation(modelMaps: ModelMaps)

    fun isFirst():Boolean{
        var a = false
        getAllLocations().isEmpty.doOnSuccess {
            a = it
        }
        return a
    }

}