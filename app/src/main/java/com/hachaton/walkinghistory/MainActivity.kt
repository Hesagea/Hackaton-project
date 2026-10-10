package com.hachaton.walkinghistory

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.hachaton.walkinghistory.ui.screens.HomeScreen
import com.hachaton.walkinghistory.ui.screens.SettingsScreen
import com.hachaton.walkinghistory.ui.screens.WelcomeScreen
import com.hachaton.walkinghistory.ui.theme.WalkingHistoryTheme

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
                                username = savedUsername,
                                onSettingsClick = {
                                    currentScreen = "settings"
                                }
                            )
                        }
                        "settings" -> {
                            SettingsScreen(
                                onBackClick = {
                                    currentScreen = "home"
                                }
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
