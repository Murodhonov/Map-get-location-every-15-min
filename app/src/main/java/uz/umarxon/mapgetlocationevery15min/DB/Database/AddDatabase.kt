package uz.umarxon.valyutaarxiv22122021.DB.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import uz.umarxon.mapgetlocationevery15min.DB.Dao.MapDao
import uz.umarxon.mapgetlocationevery15min.DB.Entity.ModelMaps

@Database(entities = [ModelMaps::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun mapDao(): MapDao

    companion object {
        private var instanse: AppDatabase? = null

        @Synchronized
        fun getInstance(context: Context): AppDatabase {

            when (instanse) {
                null -> {
                    instanse = Room.databaseBuilder(context, AppDatabase::class.java, "maps_db")
                        .fallbackToDestructiveMigration()
                        .allowMainThreadQueries()
                        .build()
                }
            }

            return instanse!!
        }

    }
}