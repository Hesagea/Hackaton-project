package com.hachaton.walkinghistory.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hachaton.walkinghistory.R
import com.hachaton.walkinghistory.ui.theme.Brown
import com.hachaton.walkinghistory.ui.theme.Green
import com.hachaton.walkinghistory.ui.theme.Orange
import com.hachaton.walkinghistory.ui.theme.WalkingHistoryTheme

data class FriendProfile(
    val id: Int,
    val name: String,
    val points: Int,
    val avatarResId: Int
)

@Composable
fun FriendsScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
    // Stan pola wyszukiwarki
    var searchQuery by rememberSaveable { mutableStateOf("") }

    // Dwa profile pod ustawkę
    val allFriends = listOf(
        FriendProfile(1, "Anna Kowalska", 1250, R.drawable.czlowiek),
        FriendProfile(2, "Piotr Nowak", 890, R.drawable.czlowiek)
    )

    // Filtrowanie listy na podstawie wpisanego tekstu w wyszukiwarce
    val filteredFriends = allFriends.filter {
        it.name.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            // Górny pasek z nagłówkiem Znajomi
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
                    text = "Znajomi",
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
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // 🔍 Wyszukiwarka znajomych na górze
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Szukaj znajomego...", fontSize = 18.sp) },
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Brown,
                    unfocusedBorderColor = Brown,
                    focusedTextColor = Brown,
                    unfocusedTextColor = Brown,
                    focusedPlaceholderColor = Brown.copy(alpha = 0.5f),
                    unfocusedPlaceholderColor = Brown.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // 👥 Lista znajomych z profilami i punktami
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(filteredFriends) { friend ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Zdjęcie profilowe
                        Image(
                            painter = painterResource(id = friend.avatarResId),
                            contentDescription = "Zdjęcie profilowe ${friend.name}",
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        // Nazwa oraz punkty
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = friend.name,
                                style = MaterialTheme.typography.titleLarge,
                                color = Brown,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "⭐ ${friend.points} punktów",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Orange,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }

                    HorizontalDivider(
                        color = Brown.copy(alpha = 0.2f),
                        thickness = 1.dp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FriendsScreenPreview() {
    WalkingHistoryTheme {
        FriendsScreen()
    }
}
