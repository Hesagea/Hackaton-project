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

    suspend fun getClosestPlace(radiusInKm: Double): Place? = withContext(Dispatchers.IO) {
        try {
            val locationTask = fusedLocationClient.lastLocation
            val currentGPS: Location = Tasks.await(locationTask) ?: return@withContext null

            val allPlacesInDatabase = placeDao.getAllPlacesList()
            val maxDistanceInMeters = radiusInKm * 1000

            val closestPlace = allPlacesInDatabase.mapNotNull { place ->
                val result = FloatArray(1)
                Location.distanceBetween(
                    currentGPS.latitude, currentGPS.longitude,
                    place.latitude, place.longitude,
                    result
                )
                val distance = result[0]

                if (distance <= maxDistanceInMeters) Pair(place, distance) else null
            }
                // Wybieramy miejsce z najmniejszą odległością i wyciągamy sam obiekt Place
                .minByOrNull { it.second }?.first

            return@withContext closestPlace

        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }
}