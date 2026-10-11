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
import androidx.compose.foundation.layout.fillMaxWidth
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

        // 1. Inicjalizacja konfiguracji mapy OSMDroid (wymagane przed użyciem mapy)
        Configuration.getInstance().load(applicationContext, getSharedPreferences("osmdroid", Context.MODE_PRIVATE))

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            // Zmienne przechowujące stan interfejsu
            var locationText by remember { mutableStateOf("Kliknij przycisk, aby pokazać mapę") }
            var currentGeoPoint by remember { mutableStateOf<GeoPoint?>(null) } // Przechowuje współrzędne dla mapy

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
                                locationText = "Udało się! Oto Twoja lokalizacja:"
                                // Zapisujemy pobrane współrzędne w formacie wymaganym przez mapę
                                currentGeoPoint = GeoPoint(location.latitude, location.longitude)
                            } else {
                                locationText = "Nie udało się pobrać. Upewnij się, że GPS jest włączony."
                            }
                        }
                } else {
                    locationText = "Odmówiono dostępu do lokalizacji"
                }
            }

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

                Spacer(modifier = Modifier.height(32.dp))

                // 2. Jeśli mamy współrzędne (currentGeoPoint nie jest nullem), wyświetlamy mapę
                if (currentGeoPoint != null) {
                    AndroidView(
                        factory = { context ->
                            MapView(context).apply {
                                // Konfiguracja mapy
                                setMultiTouchControls(true) // Pozwala na przybliżanie palcami
                                controller.setZoom(17.0) // Poziom przybliżenia (od 1 do ok. 20)
                                controller.setCenter(currentGeoPoint)

                                // Dodanie czerwonej pinezki w miejscu użytkownika
                                val marker = Marker(this)
                                marker.position = currentGeoPoint
                                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                marker.title = "Tu jesteś!"
                                overlays.add(marker)
                            }
                        },
                        // Odświeżanie mapy, gdy zmienią się współrzędne
                        update = { view ->
                            view.controller.setCenter(currentGeoPoint)
                        },
                        // Mapa zajmie szerokość ekranu i będzie wysoka na 400 dp
                        modifier = Modifier.fillMaxWidth().height(400.dp)
                    )
                }
            }
        }
    }
}