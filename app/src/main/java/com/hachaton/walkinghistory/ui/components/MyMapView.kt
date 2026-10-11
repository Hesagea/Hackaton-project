package com.hachaton.walkinghistory.ui.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@Composable
@Preview(showBackground = true)
fun MyMapView(
    modifier: Modifier = Modifier,
    hasLocationPermission: Boolean = false
) {
    val isPreview = LocalInspectionMode.current

    if (isPreview) {
        // W trybie podglądu Android Studio renderujemy zastępcze tło podglądu mapy
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🗺️ Mapa OSM (Widok Mapy)", color = Color.DarkGray)
        }
    } else {
        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current

        val mapView = remember {
            MapView(context).apply {
                val config = Configuration.getInstance()
                config.load(context, context.getSharedPreferences("osm_config", Context.MODE_PRIVATE))
                config.userAgentValue = context.packageName

                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(16.0)
                controller.setCenter(GeoPoint(51.10729, 17.06012)) // Domyślnie Wrocław Kampus PWr

                val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), this).apply {
                    enableMyLocation()
                    enableFollowLocation()
                }
                overlays.add(locationOverlay)
            }
        }

        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_RESUME -> {
                        mapView.onResume()
                        val overlay = mapView.overlays.filterIsInstance<MyLocationNewOverlay>().firstOrNull()
                        overlay?.enableMyLocation()
                        overlay?.enableFollowLocation()
                    }
                    Lifecycle.Event.ON_PAUSE -> {
                        mapView.onPause()
                        val overlay = mapView.overlays.filterIsInstance<MyLocationNewOverlay>().firstOrNull()
                        overlay?.disableMyLocation()
                    }
                    else -> {}
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
                mapView.onDetach()
            }
        }

        AndroidView(
            factory = { mapView },
            modifier = modifier.fillMaxSize(),
            update = { view ->
                if (hasLocationPermission) {
                    view.onResume()
                    val overlay = view.overlays.filterIsInstance<MyLocationNewOverlay>().firstOrNull()
                    overlay?.enableMyLocation()
                    overlay?.enableFollowLocation()
                }
            }
        )
    }
}
