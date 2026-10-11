package com.hachaton.walkinghistory.ui.screens

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import kotlin.math.roundToInt

// --- IMPORTY BAZY I LEKTORA ---
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import com.hachaton.walkinghistory.AppDatabase
import com.hachaton.walkinghistory.NearbyPlacesManager
import com.hachaton.walkinghistory.Place
import com.hachaton.walkinghistory.TtsManager

import com.hachaton.walkinghistory.R
import com.hachaton.walkinghistory.ui.components.MyMapView
import com.hachaton.walkinghistory.ui.theme.Brown
import com.hachaton.walkinghistory.ui.theme.Green
import com.hachaton.walkinghistory.ui.theme.WalkingHistoryTheme

import com.hachaton.walkinghistory.ui.screens.HistoryManager
import com.hachaton.walkinghistory.ui.theme.Orange

@Composable
fun HomeScreen(
    modifier: Modifier  = Modifier,
    username: String = "",
    onStartClick: () -> Unit = {},
    onStarClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
) {
    val displayName = if (username.isNotBlank()) username else "Użytkowniku"
    val context = LocalContext.current

    // STANY Z ORYGINALNEGO KODU
    var showFocusDialog by rememberSaveable { mutableStateOf(false) }
    var focusStep by rememberSaveable { mutableFloatStateOf(2f) }

    // NOWE STANY
    var wybraneMiejsce by remember { mutableStateOf<Place?>(null) }
    var miejscaZBazy by remember { mutableStateOf<List<Place>>(emptyList()) }

    val ttsManager = remember { TtsManager(context) }
    val coroutineScope = rememberCoroutineScope() // Pozwala uruchamiać rzeczy w tle po kliknięciu

    // Inicjalizacja bazy i Managera Miejsc
    val db = remember { AppDatabase.getDatabase(context) }
    val nearbyPlacesManager = remember { NearbyPlacesManager(context, db.placeDao()) }

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true
        hasLocationPermission = isGranted
    }

    val historyManager = remember { HistoryManager(context) }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        // Pobieranie danych z bazy z zabezpieczeniem przed "wyścigiem"
        val db = AppDatabase.getDatabase(context)
        withContext(Dispatchers.IO) {
            var pobrane = db.placeDao().getAllPlacesList()

            // Jeśli lista jest pusta, dajemy bazie chwilę na zapisanie danych w tle
            var proby = 0
            while (pobrane.isEmpty() && proby < 10) {
                kotlinx.coroutines.delay(300) // Czekamy 0.3 sekundy
                pobrane = db.placeDao().getAllPlacesList() // Pytamy bazę ponownie
                proby++
            }

            // Wrzucamy dane do naszego interfejsu
            miejscaZBazy = pobrane
        }
    }

    val focusOptions = listOf("Bliskie zeru", "3 - 6 sekund", "10 sekund", "15 sekund", "30 sekund i więcej")

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            // Górny pasek z powitaniem użytkownika
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Green)
                    .statusBarsPadding()
                    .height(70.dp)
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Witaj $displayName!",
                    style = MaterialTheme.typography.titleLarge,
                    color = Brown,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onProfileClick) {
                    Image(
                        painter = painterResource(id = R.drawable.braz_pokolorowany),
                        contentDescription = "Profil",
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        },
        bottomBar = {
            // Pasek narzędzi na dole
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(Green)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Lewa sekcja (Gwiazdka w idealnie wyśrodkowanym brązowym boxie)
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(25.dp))
                            .background(Orange)
                            .clickable { onStarClick() }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.gwiazdkabezowa),
                            contentDescription = "Gwiazdka",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                // 2. Środkowa sekcja (Przycisk START - otwiera okno z pytaniem)
                Box(
                    modifier = Modifier.weight(1.8f),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = { showFocusDialog = true }, // 👈 Otwiera okienko z pytaniem
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Brown,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(30.dp),
                        modifier = Modifier.height(72.dp)
                    ) {
                        Text(
                            text = "START",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // 3. Prawa sekcja (Kołowrotek w idealnie wyśrodkowanym brązowym boxie)
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(25.dp))
                            .background(Orange)
                            .clickable { onSettingsClick() }
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.kolowrotekbezowy),
                            contentDescription = "Ustawienia",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            MyMapView(hasLocationPermission = hasLocationPermission)

            // 1. Okienko z czasem skupienia
            if (showFocusDialog) {
                Dialog(onDismissRequest = { showFocusDialog = false }) {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        modifier = Modifier.fillMaxWidth().padding(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Jaki masz czas skupienia?",
                                style = MaterialTheme.typography.titleLarge,
                                color = Brown,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            val currentIndex = focusStep.roundToInt().coerceIn(0, 4)
                            Text(
                                text = focusOptions[currentIndex],
                                style = MaterialTheme.typography.titleLarge,
                                color = Brown,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            Slider(
                                value = focusStep,
                                onValueChange = { focusStep = it },
                                valueRange = 0f..4f,
                                steps = 3,
                                colors = SliderDefaults.colors(
                                    thumbColor = Brown,
                                    activeTrackColor = Green,
                                    inactiveTrackColor = Brown.copy(alpha = 0.2f)
                                ),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Bliskie zeru", fontSize = 12.sp, color = Brown.copy(alpha = 0.7f))
                                Text("30s i więcej", fontSize = 12.sp, color = Brown.copy(alpha = 0.7f))
                            }
                            Spacer(modifier = Modifier.height(24.dp))

                            // Przycisk ROZPOCZNIJ
                            Button(
                                onClick = {
                                    showFocusDialog = false // Zamykamy suwak

                                    // Uruchamiamy wyszukiwanie lokalizacji w tle
                                    coroutineScope.launch {
                                        // Ustawiamy promień na 5 kilometrów (5.0), żeby na 100% zadziałało na prezentacji z wnętrza budynku!
                                        val closestPlace = nearbyPlacesManager.getClosestPlace(5.0)

                                        if (closestPlace != null) {
                                            wybraneMiejsce = closestPlace // To wyświetla kartę informacyjną

                                            // Łączymy teksty i odpalamy lektora
                                            val pelnyTekst = "${closestPlace.name} ${closestPlace.description} ${closestPlace.narration} ${closestPlace.scene}"

                                            ttsManager.speak(pelnyTekst)
                                            historyManager.addEntry(closestPlace.name, isFromFavorites = false)
                                            onStartClick() // Powiadamia aplikację o starcie
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Brown, contentColor = Color.White),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.fillMaxWidth(0.85f).height(52.dp)
                            ) {
                                Text(text = "ROZPOCZNIJ", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            if (wybraneMiejsce != null) {
                Dialog(onDismissRequest = {
                    ttsManager.stop()
                    wybraneMiejsce = null
                }) {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                        modifier = Modifier.fillMaxWidth().padding(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val krotkaNazwa = wybraneMiejsce!!.name.substringBefore("(").trim()

                            Text(
                                text = "Jesteś w pobliżu:",
                                color = Brown,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = krotkaNazwa,
                                color = Brown,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Lektor czyta teraz narrację dla tego budynku. Miłego spaceru!",
                                color = Brown,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = {
                                    ttsManager.stop() // Przerwij mówienie
                                    wybraneMiejsce = null // Zamknij okienko
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Brown)
                            ) {
                                Text("Zatrzymaj i zamknij", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    WalkingHistoryTheme {
        HomeScreen(username = "Użytkownik")
    }
}
