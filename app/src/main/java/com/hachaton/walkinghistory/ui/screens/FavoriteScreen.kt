package com.hachaton.walkinghistory.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Info // <-- Nowa ikona dla samego tekstu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.hachaton.walkinghistory.AppDatabase
import com.hachaton.walkinghistory.Place
import com.hachaton.walkinghistory.TtsManager
import com.hachaton.walkinghistory.ui.theme.Brown
import com.hachaton.walkinghistory.ui.theme.Green
import com.hachaton.walkinghistory.ui.theme.WalkingHistoryTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import com.hachaton.walkinghistory.ui.screens.HistoryManager

// --- BEZPIECZNY MANAGER ULUBIONYCH ---
class FavoritesManager(context: Context) {
    private val prefs = context.getSharedPreferences("walking_history_favorites", Context.MODE_PRIVATE)

    fun isFavorite(placeName: String): Boolean {
        return prefs.getBoolean(placeName, false)
    }

    fun toggleFavorite(placeName: String): Boolean {
        val currentState = isFavorite(placeName)
        prefs.edit().putBoolean(placeName, !currentState).apply()
        return !currentState
    }
}

@Composable
fun FavoriteScreen(
    modifier: Modifier  = Modifier,
    onBackClick: () -> Unit = {}
) {


    val context = LocalContext.current

    val historyManager = remember { HistoryManager(context) }

    val db = remember { AppDatabase.getDatabase(context) }
    val favoritesManager = remember { FavoritesManager(context) }
    val ttsManager = remember { TtsManager(context) }
    val coroutineScope = rememberCoroutineScope()

    var wszystkieMiejsca by remember { mutableStateOf<List<Place>>(emptyList()) }
    var ulubioneMiejsca by remember { mutableStateOf<List<Place>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    var showAddDialog by remember { mutableStateOf(false) }

    // Rozdzielone stany na wyświetlanie tekstu i odtwarzanie dźwięku
    var miejsceTekst by remember { mutableStateOf<Place?>(null) }
    var miejsceDzwiek by remember { mutableStateOf<Place?>(null) }

    fun zaladujUlubione() {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                val wszystkie = db.placeDao().getAllPlacesList()
                wszystkieMiejsca = wszystkie
                ulubioneMiejsca = wszystkie.filter { favoritesManager.isFavorite(it.name) }
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        zaladujUlubione()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Green)
                    .statusBarsPadding()
                    .height(70.dp)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Cofnij",
                        tint = Brown,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Ulubione",
                    style = MaterialTheme.typography.titleLarge,
                    color = Brown,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Brown,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Dodaj do ulubionych",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (!isLoading) {
                if (ulubioneMiejsca.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.FavoriteBorder,
                            contentDescription = "Puste",
                            tint = Brown.copy(alpha = 0.5f),
                            modifier = Modifier.size(100.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Nie masz jeszcze ulubionych miejsc.\nKliknij przycisk +, aby je dodać!",
                            color = Brown.copy(alpha = 0.7f),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(ulubioneMiejsca) { place ->
                            Card(
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = Green),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    val krotkaNazwa = place.name.substringBefore("(").trim()
                                    Text(
                                        text = krotkaNazwa,
                                        color = Brown,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.weight(1f)
                                    )

                                    // 1. PRZYCISK TEKSTU (CZYTANIE RĘCZNE)
                                    IconButton(onClick = { miejsceTekst = place }) {
                                        Icon(
                                            imageVector = Icons.Filled.Info,
                                            contentDescription = "Pokaż tekst",
                                            tint = Brown,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }

                                    // 2. PRZYCISK ODTWARZANIA (LEKTOR)
                                    IconButton(onClick = {
                                        miejsceDzwiek = place
                                        val pelnyTekst = "${place.name}. ${place.description} ${place.narration} ${place.scene}"
                                        historyManager.addEntry(place.name, isFromFavorites = true)
                                        ttsManager.speak(pelnyTekst)
                                    }) {
                                        Icon(
                                            imageVector = Icons.Filled.PlayArrow,
                                            contentDescription = "Odtwórz",
                                            tint = Brown,
                                            modifier = Modifier.size(36.dp)
                                        )
                                    }

                                    // 3. PRZYCISK USUWANIA Z ULUBIONYCH
                                    IconButton(onClick = {
                                        favoritesManager.toggleFavorite(place.name)
                                        zaladujUlubione()
                                    }) {
                                        Icon(
                                            imageVector = Icons.Filled.Favorite,
                                            contentDescription = "Usuń z ulubionych",
                                            tint = Brown,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // --- OKIENKO WYBORU NOWYCH MIEJSC DO DODANIA ---
            if (showAddDialog) {
                Dialog(onDismissRequest = { showAddDialog = false }) {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                        modifier = Modifier.fillMaxWidth().padding(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Dodaj do ulubionych",
                                style = MaterialTheme.typography.titleLarge,
                                color = Brown,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            val dostepneMiejsca = wszystkieMiejsca.filter { !favoritesManager.isFavorite(it.name) }

                            if (dostepneMiejsca.isEmpty()) {
                                Text(
                                    text = "Wszystkie miejsca są już w ulubionych!",
                                    color = Brown.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 16.dp)
                                )
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxWidth().heightIn(max = 300.dp)
                                ) {
                                    items(dostepneMiejsca) { place ->
                                        Button(
                                            onClick = {
                                                favoritesManager.toggleFavorite(place.name)
                                                zaladujUlubione()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Green, contentColor = Brown),
                                            shape = RoundedCornerShape(16.dp),
                                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                        ) {
                                            val krotkaNazwa = place.name.substringBefore("(").trim()
                                            Text(
                                                text = krotkaNazwa,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { showAddDialog = false },
                                colors = ButtonDefaults.buttonColors(containerColor = Brown, contentColor = Color.White),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.fillMaxWidth(0.85f).height(48.dp)
                            ) {
                                Text(text = "ZAMKNIJ", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // --- OKIENKO Z SAMYM TEKSTEM DO CZYTANIA ---
            if (miejsceTekst != null) {
                Dialog(onDismissRequest = { miejsceTekst = null }) {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                        modifier = Modifier.fillMaxWidth().padding(12.dp).heightIn(max = 550.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val krotkaNazwa = miejsceTekst!!.name.substringBefore("(").trim()
                            val tekstDlaUzytkownika = "${miejsceTekst!!.description}\n\n${miejsceTekst!!.narration}\n\n${miejsceTekst!!.scene}"

                            Text(
                                text = krotkaNazwa,
                                color = Brown,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            Column(
                                modifier = Modifier
                                    .weight(1f, fill = false)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Text(
                                    text = tekstDlaUzytkownika,
                                    color = Brown,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Justify,
                                    lineHeight = 22.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = { miejsceTekst = null },
                                colors = ButtonDefaults.buttonColors(containerColor = Brown)
                            ) {
                                Text("Zamknij", color = Color.White)
                            }
                        }
                    }
                }
            }

            // --- OKIENKO ODTWARZANIA GŁOSU (BEZ TEKSTU) ---
            if (miejsceDzwiek != null) {
                Dialog(onDismissRequest = {
                    ttsManager.stop()
                    miejsceDzwiek = null
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
                            val krotkaNazwa = miejsceDzwiek!!.name.substringBefore("(").trim()

                            Text(
                                text = "Lektor czyta historię:",
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
                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = {
                                    ttsManager.stop()
                                    miejsceDzwiek = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Brown)
                            ) {
                                Text("Zatrzymaj", color = Color.White)
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
fun FavoriteScreenPreview() {
    WalkingHistoryTheme {
        FavoriteScreen()
    }
}