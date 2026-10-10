package com.hachaton.walkinghistory.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hachaton.walkinghistory.R
import com.hachaton.walkinghistory.ui.theme.Brown
import com.hachaton.walkinghistory.ui.theme.Green
import com.hachaton.walkinghistory.ui.theme.WalkingHistoryTheme

@Composable
fun WelcomeScreen(
    modifier: Modifier = Modifier,
    onStartClick: (String) -> Unit = {}
) {
    var username by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Green),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Witamy!",
            style = MaterialTheme.typography.titleLarge,
            color = Brown,
            fontSize = 70.sp, // Powiększony nagłówek główny
            modifier = Modifier.padding(top = 150.dp)
        )
        Text(
            text = "Podaj nazwę użytkownika",
            style = MaterialTheme.typography.titleLarge,
            color = Brown,
            fontSize = 18.sp, // Powiększony podtytuł
            modifier = Modifier.padding(top = 4.dp)
        )
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            placeholder = { Text("Nazwa użytkownika", fontSize = 18.sp) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (username.isNotBlank()) {
                        onStartClick(username)
                    }
                }
            ),
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
                .fillMaxWidth(0.85f)
                .padding(top = 24.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Image(
            painter = painterResource(id = R.drawable.strona_poczatkowa),
            contentDescription = "Zdjęcie na dole",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WelcomeScreenPreview() {
    WalkingHistoryTheme {
        WelcomeScreen()
    }
}
