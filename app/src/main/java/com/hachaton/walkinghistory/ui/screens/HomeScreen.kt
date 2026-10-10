package com.hachaton.walkinghistory.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hachaton.walkinghistory.R
import com.hachaton.walkinghistory.ui.components.MyMapView
import com.hachaton.walkinghistory.ui.theme.Brown
import com.hachaton.walkinghistory.ui.theme.Green
import com.hachaton.walkinghistory.ui.theme.WalkingHistoryTheme

@Composable
fun HomeScreen(
    username: String = "",
    onStartClick: () -> Unit = {},
    onStarClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // Pasek narzędzi na dole o idealnej symetrii
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .background(Green)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Lewa sekcja (Gwiazdka) - 1/3 szerokości paska
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = onStarClick,
                        modifier = Modifier.size(60.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.gwiazdka),
                            contentDescription = "Gwiazdka",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                // 2. Środkowa sekcja (Przycisk START) - 1/3 szerokości paska (dokładny środek ekranu)
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = onStartClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Brown,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.height(52.dp)
                    ) {
                        Text(
                            text = "START",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // 3. Prawa sekcja (Kołowrotek) - 1/3 szerokości paska
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = onSettingsClick,
                        modifier = Modifier.size(60.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.kolowrotek),
                            contentDescription = "Ustawienia",
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
            // Mapka po środku ekranu
            MyMapView()
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
