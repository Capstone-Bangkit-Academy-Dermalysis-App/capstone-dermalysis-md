package com.dermalisys.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dermalisys.data.Converters
import com.dermalisys.data.remote.response.getuserpredict.DataItem

@Database(
    entities = [DataItem::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class HistoryDatabase : RoomDatabase() {

    companion object {
        @Volatile
        private var INSTANCE: HistoryDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): HistoryDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    HistoryDatabase::class.java, "history_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}