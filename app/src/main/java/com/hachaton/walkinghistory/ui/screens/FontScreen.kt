package com.hachaton.walkinghistory.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
import com.hachaton.walkinghistory.ui.theme.Brown
import com.hachaton.walkinghistory.ui.theme.Green
import com.hachaton.walkinghistory.ui.theme.WalkingHistoryTheme

@Composable
fun FontScreen(
    modifier: Modifier  = Modifier,
    onBackClick: () -> Unit = {}
) {
    // Stan suwaka od 0 do 4 (5 pozycji). Domyślnie środkowa pozycja (2.0f)
    var sliderPosition by rememberSaveable { mutableFloatStateOf(2f) }

    // 5 poziomów wielkości czcionki w pikselach sp
    val fontSizes = listOf(14.sp, 17.sp, 20.sp, 24.sp, 28.sp)
    val fontNames = listOf("Najmniejsza", "Mała", "Średnia (Domyślna)", "Duża", "Największa")

    val currentIndex = sliderPosition.roundToInt().coerceIn(0, 4)
    val currentFontSize = fontSizes[currentIndex]
    val currentFontName = fontNames[currentIndex]

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            // Górny pasek z nagłówkiem Czcionka
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
                    text = "Czcionka",
                    style = MaterialTheme.typography.titleLarge,
                    color = Brown,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ℹ️ Prosty opis działania suwaka
            Text(
                text = "Przesuń suwak poniżej, aby zmienić wielkość czcionki w aplikacji.",
                style = MaterialTheme.typography.bodyLarge,
                color = Brown,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // 🔤 Oznaczenia małe A i duże A nad suwakiem
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "A",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Brown
                )
                Text(
                    text = currentFontName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Brown
                )
                Text(
                    text = "A",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Brown
                )
            }

            // 🎚️ Suwak z 5 krokami (0..4, steps = 3 dają 5 pozycji)
            Slider(
                value = sliderPosition,
                onValueChange = { sliderPosition = it },
                valueRange = 0f..4f,
                steps = 3, // Daje 5 pozycji: 0, 1, 2 (środek), 3, 4
                colors = SliderDefaults.colors(
                    thumbColor = Brown,
                    activeTrackColor = Green,
                    inactiveTrackColor = Brown.copy(alpha = 0.2f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 🔍 Ramka z podglądem tekstu w wybranym rozmiarze
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Podgląd tekstu",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Brown.copy(alpha = 0.6f),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Text(
                        text = "To jest podgląd wybranej wielkości czcionki w aplikacji Walking History.",
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = currentFontSize, // Dynamicznie zmieniana wielkość tekstu
                        color = Brown,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FontScreenPreview() {
    WalkingHistoryTheme {
        FontScreen()
    }
}
