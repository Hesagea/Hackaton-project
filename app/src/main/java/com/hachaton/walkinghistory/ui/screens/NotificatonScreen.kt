package com.hachaton.walkinghistory.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hachaton.walkinghistory.ui.theme.Brown
import com.hachaton.walkinghistory.ui.theme.Green
import com.hachaton.walkinghistory.ui.theme.WalkingHistoryTheme

@Composable
fun NotificationScreen(
    modifier: Modifier  = Modifier,
    onBackClick: () -> Unit = {}
) {
    // Stan dla dwóch opcji powiadomień
    var isNotificationsEnabled by rememberSaveable { mutableStateOf(true) }
    var isSoundEnabled by rememberSaveable { mutableStateOf(true) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            // Górny pasek z nagłówkiem Powiadomienia
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
                    text = "Powiadomienia",
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
                .padding(vertical = 12.dp, horizontal = 20.dp)
        ) {
            // 1. Opcja: Otrzymywanie powiadomień
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isNotificationsEnabled = !isNotificationsEnabled }
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Nazwa opcji po lewej stronie
                Text(
                    text = "Otrzymywanie powiadomień",
                    style = MaterialTheme.typography.titleLarge,
                    color = if (isNotificationsEnabled) Brown else Brown.copy(alpha = 0.4f),
                    fontSize = 22.sp,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Suwak / przełącznik po prawej stronie
                Switch(
                    checked = isNotificationsEnabled,
                    onCheckedChange = { isNotificationsEnabled = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Green,
                        uncheckedThumbColor = Brown.copy(alpha = 0.6f),
                        uncheckedTrackColor = Color.White
                    )
                )
            }

            HorizontalDivider(
                color = Brown.copy(alpha = 0.2f),
                thickness = 1.dp
            )

            // 2. Opcja: Dźwięk
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isSoundEnabled = !isSoundEnabled }
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Nazwa opcji po lewej stronie
                Text(
                    text = "Dźwięk",
                    style = MaterialTheme.typography.titleLarge,
                    color = if (isSoundEnabled) Brown else Brown.copy(alpha = 0.4f),
                    fontSize = 22.sp,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Suwak / przełącznik po prawej stronie
                Switch(
                    checked = isSoundEnabled,
                    onCheckedChange = { isSoundEnabled = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Green,
                        uncheckedThumbColor = Brown.copy(alpha = 0.6f),
                        uncheckedTrackColor = Color.White
                    )
                )
            }

            HorizontalDivider(
                color = Brown.copy(alpha = 0.2f),
                thickness = 1.dp
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NotificationScreenPreview() {
    WalkingHistoryTheme {




        NotificationScreen()
    }
}
