package com.hachaton.walkinghistory.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hachaton.walkinghistory.R
import com.hachaton.walkinghistory.ui.theme.Brown
import com.hachaton.walkinghistory.ui.theme.Green
import com.hachaton.walkinghistory.ui.theme.WalkingHistoryTheme

data class SettingOption(
    val title: String,
    val iconResId: Int? = null
)

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit = {},
    onOptionClick: (String) -> Unit = {}
) {
    val options = listOf(
        SettingOption("Znajomi", R.drawable.czlowiek),
        SettingOption("Ulubione", R.drawable.serduszko),
        SettingOption("Powiadomienia", R.drawable.dzwonek),
        SettingOption("Czcionka", null),
        SettingOption("Historia", R.drawable.historia)
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            // Górny pasek z automatycznym odstępem odzegarka (statusBarsPadding)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Green)
                    .statusBarsPadding() // 👈 Omija zegarek, baterię i pasek stanu systemu
                    .height(70.dp)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Strzałka powrotu z wygodnym powiększonym obszarem kliknięcia
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

                // Nagłówek Ustawienia umieszczony poniżej zegarka
                Text(
                    text = "Ustawienia",
                    style = MaterialTheme.typography.titleLarge,
                    color = Brown,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(vertical = 12.dp, horizontal = 20.dp)
        ) {
            items(options) { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOptionClick(option.title) }
                        .padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Obrazek po lewej lub powiększone symbole "A a"
                    if (option.iconResId != null) {
                        Image(
                            painter = painterResource(id = option.iconResId),
                            contentDescription = option.title,
                            modifier = Modifier.size(48.dp)
                        )
                    } else {
                        // Duże A i małe a
                        Row(
                            modifier = Modifier.size(48.dp),
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "A",
                                style = MaterialTheme.typography.titleLarge,
                                color = Brown,
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "a",
                                style = MaterialTheme.typography.titleLarge,
                                color = Brown,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    // Powiększone nazwy nagłówków opcji
                    Text(
                        text = option.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = Brown,
                        fontSize = 28.sp
                    )
                }

                HorizontalDivider(
                    color = Brown.copy(alpha = 0.2f),
                    thickness = 1.dp
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SettingsScreenPreview() {
    WalkingHistoryTheme {
        SettingsScreen()
    }
}
