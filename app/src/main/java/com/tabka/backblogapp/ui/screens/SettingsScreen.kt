package com.tabka.backblogapp.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tabka.backblogapp.R
import com.tabka.backblogapp.network.models.UserData
import com.tabka.backblogapp.ui.shared.LoadingSpinner
import com.tabka.backblogapp.util.DataResult
import com.tabka.backblogapp.ui.viewmodels.SettingsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(navController: NavController) {
    val hasBackButton = true
    val pageTitle = "Settings"
    val settingsViewModel = SettingsViewModel()

    BaseScreen(navController, hasBackButton, pageTitle) {
        // Returns a scope that's cancelled when SettingsScreen is removed from composition
        var userData by remember { mutableStateOf<UserData?>(null) }

        LaunchedEffect(Unit) {
            when (val result = settingsViewModel.getUserData()) {
                is DataResult.Success -> {
                    userData = result.item
                }
                is DataResult.Failure -> {
                    val e = result.throwable.message
                    Log.d("SettingsScreen", "Error getting the user's data $e")
                }
            }
        }

        if (userData == null) {
            LoadingSpinner()
        } else {
            SettingsForm(userData!!)
        }
    }
}

@Composable
fun SettingsForm(userData: UserData) {
    Column {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ){
            Image(
                // TODO: properly access avatar
                painter = painterResource(id = R.drawable.avatar1),
                contentDescription = "loading spinner",
                modifier = Modifier
                    .height(120.dp)
                    .width(120.dp)
            )
            Button(onClick = { /* TODO */ },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF3891E1)
                ),
                shape = RoundedCornerShape(35),
                modifier = Modifier
                    .height(70.dp)
                    .width(200.dp)
                    .padding(12.dp)
            )
            {
                Text("Change Avatar", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
            }
        }

        // Username field
        var username by remember { mutableStateOf("") }
        TextField(
            value = username,
            // TODO - Update view model immediately
            onValueChange = { username = it },
            label = { Text("Username") },
            placeholder = { Text(userData.username!!) },
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            textStyle = TextStyle(color = Color(0xE6FFFFFF)),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF373737),
                focusedLabelColor = Color(0xFF979C9E),
                unfocusedLabelColor = Color(0xFF979C9E),
                unfocusedBorderColor = Color(0xFF373737),
                backgroundColor = Color(0xFF373737)
            ),
            singleLine = true
        )

        // Old Password field
        var password by remember { mutableStateOf("") }
        TextField(
            value = password,
            // TODO - Update view model immediately
            onValueChange = { password = it },
            label = { Text("Old Password") },
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            textStyle = TextStyle(color = Color(0xE6FFFFFF)),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF373737),
                focusedLabelColor = Color(0xFF979C9E),
                unfocusedLabelColor = Color(0xFF979C9E),
                unfocusedBorderColor = Color(0xFF373737),
                backgroundColor = Color(0xFF373737)
            ),
            singleLine = true
        )

        // New Password field
        var newPassword by remember { mutableStateOf("") }
        TextField(
            value = newPassword,
            // TODO - Update view model immediately
            onValueChange = { newPassword = it },
            label = { Text("New Password") },
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            textStyle = TextStyle(color = Color(0xE6FFFFFF)),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF373737),
                focusedLabelColor = Color(0xFF979C9E),
                unfocusedLabelColor = Color(0xFF979C9E),
                unfocusedBorderColor = Color(0xFF373737),
                backgroundColor = Color(0xFF373737)
            ),
            singleLine = true
        )

        // Update settings button
        Button(onClick = {
            CoroutineScope(Dispatchers.Main).launch {
                // Check if password matches

                /*if (loginSuccessful.first) {
                    statusText = "Login successful. Redirecting..."
                    statusColor = Color(0xFF4BB543)
                    visible = true
                    delay(1000)
                    navController.navigate("friends")
                } else {
                    // Display error text message
                    statusText = loginSuccessful.second
                    statusColor = Color(0xFFCC0000)
                    visible = true
                }*/
            }
        },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFF3891E1)
            ),
            shape = RoundedCornerShape(35),
            modifier = Modifier
                .height(70.dp)
                .width(200.dp)
                .padding(12.dp)
        )
        {
            Text("Update Settings", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
        }
    }
}