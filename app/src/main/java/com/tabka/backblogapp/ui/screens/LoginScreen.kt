package com.tabka.backblogapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.Button
import androidx.compose.material.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tabka.backblogapp.R
import com.tabka.backblogapp.ui.shared.AuthScreen
import com.tabka.backblogapp.ui.shared.CardGradient
import com.tabka.backblogapp.ui.shared.PasswordTextField
import com.tabka.backblogapp.ui.viewmodels.AuthViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController) {
    val authViewModel = AuthViewModel()

    AuthScreen(navController) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CardGradient {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(12.dp)
                ) {

                    // BackBlog Heading
                    Row(modifier = Modifier.padding(top = 10.dp, bottom = 10.dp)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val imgId = R.drawable.img_logo_80_80
                            Image(
                                painter = painterResource(id = imgId),
                                contentDescription = "BackBlog logo",
                                modifier = Modifier
                                    .height(90.dp)
                                    .fillMaxWidth()
                                    .testTag(imgId.toString())
                                    .padding(bottom = 5.dp)
                            )

                            Text("BackBlog", style = MaterialTheme.typography.headlineLarge)

                            Text(
                                text = "Login to collaborate",
                                style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF979C9E))
                            )
                        }
                    }
                    
                    // Fields
                    Row() {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            // Email field
                            var email by remember { mutableStateOf("") }
                            TextField(
                                value = email,
                                // TODO - Update view model immediately
                                onValueChange = { email = it },
                                label = { Text("Email") },
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("EMAIL_FIELD"),
                                textStyle = TextStyle(color = Color(0xE6FFFFFF)),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = Color(0xFF373737),
                                    focusedLabelColor = Color(0xFF979C9E),
                                    unfocusedLabelColor = Color(0xFF979C9E),
                                    unfocusedBorderColor = Color(0xFF373737),
                                    backgroundColor = Color(0xFF373737)
                                ),
                                singleLine = true
                            )

                            // Password field
                            var password by remember { mutableStateOf("") }
                            PasswordTextField("Password") { pwd -> password = pwd }

                            // Status Message
                            var visible by remember { mutableStateOf(false) }
                            var statusText by remember { mutableStateOf("") }
                            var statusColor by remember { mutableStateOf(Color(0xFF4BB543)) }
                            if (visible) {
                                Text(
                                    text = statusText,
                                    style = MaterialTheme.typography.bodyMedium.copy(color = statusColor),
                                    modifier = Modifier.testTag("STATUS_MESSAGE"),
                                    textAlign = TextAlign.Center
                                )
                            }

                            Spacer(modifier = Modifier.height(30.dp))

                            // Login Button
                            Button(
                                onClick = {
                                    // Check if user has entered all fields
                                    if (email.isNotEmpty() && password.isNotEmpty()) {
                                        CoroutineScope(Dispatchers.Main).launch {
                                            val loginSuccessful =
                                                authViewModel.attemptLogin(email, password)
                                            if (loginSuccessful.first) {
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
                                            }
                                        }
                                    } else {
                                        statusText = "Please fill all the fields"
                                        statusColor = Color(0xFFCC0000)
                                        visible = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = Color(0xFF3891E1)
                                ),
                                shape = RoundedCornerShape(45),
                                modifier = Modifier
                                    .height(50.dp)
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp)
                                    .testTag("LOGIN_BUTTON")
                            )
                            {
                                Text(
                                    "LOG IN", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    // Sign Up
                    Row() {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Don't have an account?",
                                style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF979C9E)),
                            )
                            TextButton(onClick = { navController.navigate("signup") },
                                modifier = Modifier.testTag("GO_TO_SIGNUP_BUTTON")) {
                                Text(
                                    "Sign up",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        textDecoration = TextDecoration.Underline,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    modifier = Modifier.padding(bottom = 30.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}