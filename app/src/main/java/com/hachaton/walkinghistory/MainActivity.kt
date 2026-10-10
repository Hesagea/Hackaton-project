package com.hachaton.walkinghistory
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MainActivity : ComponentActivity() {
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().load(applicationContext, getSharedPreferences("osmdroid", Context.MODE_PRIVATE))

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            var locationText by remember { mutableStateOf("Kliknij przycisk, aby pokazać mapę") }
            var currentGeoPoint by remember { mutableStateOf<GeoPoint?>(null) }

            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                val hasPermission = permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                        permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)

                if (hasPermission) {
                    locationText = "Szukam sygnału GPS..."

                    fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                        .addOnSuccessListener { location ->
                            if (location != null) {
                                // Zapisujemy współrzędne - to ukryje przycisk i pokaże mapę
                                currentGeoPoint = GeoPoint(location.latitude, location.longitude)
                            } else {
                                locationText = "Nie udało się pobrać. Upewnij się, że GPS jest włączony."
                            }
                        }
                } else {
                    locationText = "Odmówiono dostępu do lokalizacji"
                }
            }

            // JEŚLI NIE MAMY LOKALIZACJI -> Pokaż ekran z przyciskiem
            if (currentGeoPoint == null) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = locationText)
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(onClick = {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }) {
                        Text("Pokaż mnie na mapie")
                    }
                }
            }
            // JEŚLI MAMY LOKALIZACJĘ -> Pokaż TYLKO mapę na cały ekran
            else {
                AndroidView(
                    factory = { context ->
                        MapView(context).apply {
                            setMultiTouchControls(true)
                            controller.setZoom(18.0) // Trochę większe przybliżenie domyślne
                            controller.setCenter(currentGeoPoint)

                            // Dodanie czerwonej pinezki w miejscu użytkownika
                            val marker = Marker(this).apply {
                                position = currentGeoPoint
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                title = "Tu jesteś!"
                                // Ta linijka wymusza użycie standardowej, ładnej czerwonej pinezki z systemu Android
                                icon = androidx.core.content.ContextCompat.getDrawable(
                                    context,
                                    R.drawable.ic_red_pin
                                )
                            }
                            overlays.add(marker)
                        }
                    },
                    update = { view ->
                        view.controller.setCenter(currentGeoPoint)
                    },
                    // Modifier.fillMaxSize() sprawia, że mapa zajmuje 100% ekranu
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}