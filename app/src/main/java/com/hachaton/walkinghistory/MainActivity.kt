package com.hachaton.walkinghistory

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.hachaton.walkinghistory.ui.screens.FavoriteScreen
import com.hachaton.walkinghistory.ui.screens.FontScreen
import com.hachaton.walkinghistory.ui.screens.FriendsScreen
import com.hachaton.walkinghistory.ui.screens.HistoryScreen
import com.hachaton.walkinghistory.ui.screens.HomeScreen
import com.hachaton.walkinghistory.ui.screens.NotificationScreen
import com.hachaton.walkinghistory.ui.screens.SettingsScreen
import com.hachaton.walkinghistory.ui.screens.StaredScreen
import com.hachaton.walkinghistory.ui.screens.WelcomeScreen
import com.hachaton.walkinghistory.ui.theme.WalkingHistoryTheme
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WalkingHistoryTheme {
                var currentScreen by rememberSaveable { mutableStateOf("welcome") }
                var savedUsername by rememberSaveable { mutableStateOf("") }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
                    when (currentScreen) {
                        "welcome" -> {
                            WelcomeScreen(
                                modifier = Modifier.padding(innerPadding),
                                onStartClick = { name ->
                                    savedUsername = name
                                    currentScreen = "home"
                                }
                            )
                        }
                        "home" -> {
                            HomeScreen(
                                modifier = Modifier.padding(innerPadding),
                                username = savedUsername,
                                onStarClick = {
                                    currentScreen = "stared"
                                },
                                onSettingsClick = {
                                    currentScreen = "settings"
                                }
                            )
                        }
                        "settings" -> {
                            SettingsScreen(
                                modifier = Modifier.padding(innerPadding),
                                onBackClick = {
                                    currentScreen = "home"
                                },
                                onOptionClick = { title ->
                                    when (title) {
                                        "Znajomi" -> currentScreen = "friends"
                                        "Ulubione" -> currentScreen = "favorite"
                                        "Powiadomienia" -> currentScreen = "notification"
                                        "Czcionka" -> currentScreen = "font"
                                        "Historia" -> currentScreen = "history"
                                    }
                                }
                            )
                        }
                        "stared" -> {
                            StaredScreen(
                                modifier = Modifier.padding(innerPadding),
                                onBackClick = { currentScreen = "home" }
                            )
                        }
                        "friends" -> {
                            FriendsScreen(
                                modifier = Modifier.padding(innerPadding),
                                onBackClick = { currentScreen = "settings" }
                            )
                        }
                        "favorite" -> {
                            FavoriteScreen(
                                modifier = Modifier.padding(innerPadding),
                                onBackClick = { currentScreen = "settings" }
                            )
                        }
                        "notification" -> {
                            NotificationScreen(
                                modifier = Modifier.padding(innerPadding),
                                onBackClick = { currentScreen = "settings" }
                            )
                        }
                        "font" -> {
                            FontScreen(
                                modifier = Modifier.padding(innerPadding),
                                onBackClick = { currentScreen = "settings" }
                            )
                        }
                        "history" -> {
                            HistoryScreen(
                                modifier = Modifier.padding(innerPadding),
                                onBackClick = { currentScreen = "settings" }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainActivityPreview() {
    WalkingHistoryTheme {
        WelcomeScreen()
    }
}
@Composable
fun EkranSzczegolowMiejsca(place: Place) {
    val context = LocalContext.current
    // Tworzymy instancję naszego menedżera tylko raz dla tego ekranu
    val ttsManager = remember { TtsManager(context) }

    Column {
        Text(text = place.name)
        Text(text = place.description)

        // Najzwyklejszy przycisk
        Button(
            onClick = { ttsManager.speak(place.narration) },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Posłuchaj narracji")
        }
    }
}
@Composable
fun EkranZListaMiejsc() {
    val context = LocalContext.current

    // Zmienna przechowująca naszą listę miejsc; na początku jest pusta
    var miejscaZBazy by remember { mutableStateOf<List<Place>>(emptyList()) }

    // Pobieranie danych z bazy w tle (uruchomi się tylko raz przy wejściu na ekran)
    LaunchedEffect(Unit) {
        val db = AppDatabase.getDatabase(context)
        val pobraneMiejsca = withContext(Dispatchers.IO) {
            db.placeDao().getAllPlacesList()
        }
        miejscaZBazy = pobraneMiejsca
    }

    // Gdy dane się załadują, pokazujemy je na ekranie
    if (miejscaZBazy.isNotEmpty()) {
        // Na potrzeby testu wyświetlamy po prostu pierwsze miejsce z listy (Serowiec C-13)
        val wybraneMiejsce = miejscaZBazy[0]

        // TO JEST TEN MOMENT: Przekazujemy pełne dane z bazy do Twojego przycisku!
        EkranSzczegolowMiejsca(place = wybraneMiejsce)
    } else {
        Text("Ładowanie wrocławskich legend...")
    }
}
