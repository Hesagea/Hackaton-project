package com.hachaton.walkinghistory

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NearbyPlacesManager(
    private val context: Context,
    private val placeDao: PlaceDao
) {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    suspend fun getPlacesAtCurrentLocation(radiusInKm: Double): List<Place> = withContext(Dispatchers.IO) {
        try {
            //Pobieranie aktualnej lokalizacji telefonu
            val locationTask = fusedLocationClient.lastLocation
            val currentGPS: Location = Tasks.await(locationTask) ?: return@withContext emptyList()

            // Pobieranie wszystkich miejsc z bazy danych Room
            val allPlacesInDatabase = placeDao.getAllPlacesList()

            // Zamiana kilometrow podanych przez użytkownika na metry
            val maxDistanceInMeters = radiusInKm * 1000

            // Przefiltrowanie listy za pomocą prostego testu odległości
            val placesNearby = allPlacesInDatabase.filter { place ->
                val result = FloatArray(1)

                // funkcja wrzuca odległość w metravh do tablicy 'result'
                Location.distanceBetween(
                    currentGPS.latitude, currentGPS.longitude, // (telefon)
                    place.latitude, place.longitude,           // (dane z bazy)
                    result
                )

                val distanceToPlace = result[0]

                // Zwaraca true tylko wtedy, gdy odległość jest mniejsza lub równa promieniowi
                distanceToPlace <= maxDistanceInMeters
            }

            return@withContext placesNearby

        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext emptyList()
        }
    }
}


