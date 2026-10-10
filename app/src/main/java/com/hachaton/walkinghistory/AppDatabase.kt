package com.hachaton.walkinghistory

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Place::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun placeDao(): PlaceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "walking_history_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database.placeDao())
                    }
                }
            }

            private suspend fun populateInitialData(dao: PlaceDao) {
                val samplePlaces = listOf(
                    Place(
                        name = "Historic Town Square",
                        description = "The vibrant heart of the old town with centuries of history.",
                        latitude = 52.2297,
                        longitude = 21.0122
                    ),
                    Place(
                        name = "Ancient Castle Ruins",
                        description = "Remnants of a 14th-century fortress overlooking the river.",
                        latitude = 52.2350,
                        longitude = 21.0100
                    ),
                    Place(
                        name = "Old Cathedral",
                        description = "A magnificent gothic cathedral built in the Middle Ages.",
                        latitude = 52.2250,
                        longitude = 21.0200
                    )
                )
                dao.insertAll(samplePlaces)
            }
        }
    }
}
