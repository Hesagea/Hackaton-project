package com.hachaton.walkinghistory.ui.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

@Composable
@Preview(showBackground = true)
fun MyMapView(modifier: Modifier = Modifier) {
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
        // Na urządzeniu/emulatorze ładujemy pełną interaktywną mapę osmdroid
        AndroidView(
            modifier = modifier.fillMaxSize(),
            factory = { ctx ->
                val config = Configuration.getInstance()
                config.load(ctx, ctx.getSharedPreferences("osm_config", Context.MODE_PRIVATE))
                config.userAgentValue = ctx.packageName

                MapView(ctx).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    controller.setZoom(12.0)
                    controller.setCenter(GeoPoint(52.2297, 21.0122))
                }
            }
        )
    }
}
