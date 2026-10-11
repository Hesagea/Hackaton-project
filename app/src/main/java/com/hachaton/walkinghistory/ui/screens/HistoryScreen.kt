package com.hachaton.walkinghistory.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hachaton.walkinghistory.ui.theme.Brown
import com.hachaton.walkinghistory.ui.theme.Green
import com.hachaton.walkinghistory.ui.theme.WalkingHistoryTheme

data class HistoryEntry(
    val date: String,
    val placesCount: Int
)

@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onItemClick: (HistoryEntry) -> Unit = {}
) {
    val historyItems = listOf(
        HistoryEntry("11.10.2026", 4),
        HistoryEntry("10.10.2026", 3)
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            // Górny pasek z nagłówkiem Historia
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
                    text = "Historia",
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
            items(historyItems) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemClick(item) }
                        .padding(vertical = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1. Data po lewej stronie
                    Text(
                        text = item.date,
                        style = MaterialTheme.typography.titleLarge,
                        color = Brown,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(140.dp)
                    )

                    // 2. Pionowa linia rozdzielająca
                    VerticalDivider(
                        modifier = Modifier
                            .height(28.dp)
                            .padding(horizontal = 12.dp),
                        color = Brown.copy(alpha = 0.4f),
                        thickness = 1.5.dp
                    )

                    // 3. Ilość miejsc wyrównana do lewej strony od linii, bez emoji, taki sam rozmiar tekstu
                    Text(
                        text = "${item.placesCount} miejsca",
                        style = MaterialTheme.typography.titleLarge,
                        color = Brown,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
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
fun HistoryScreenPreview() {
    WalkingHistoryTheme {
        HistoryScreen()
    }
}
