package com.tabka.backblogapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tabka.backblogapp.R
import com.tabka.backblogapp.ui.shared.AuthScreen
import com.tabka.backblogapp.ui.shared.CardGradient
import com.tabka.backblogapp.ui.viewmodels.AuthViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController) {
    val authViewModel = AuthViewModel()

    AuthScreen(navController) {
        CardGradient {
            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(12.dp)
            ) {
                val imgId = R.drawable.img_logo_80_80
                Image(
                    painter = painterResource(id = imgId),
                    contentDescription = "BackBlog logo",
                    modifier = Modifier
                        .height(80.dp)
                        .fillMaxWidth()
                        .testTag(imgId.toString())
                )

                Text("BackBlog", style = MaterialTheme.typography.headlineLarge)

                Text(text = "Login to collaborate",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF979C9E)),
                    modifier = Modifier.padding(bottom = 8.dp))

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
                TextField(
                    value = password,
                    // TODO - Update view model immediately
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth()
                        .testTag("PASSWORD_FIELD"),
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

                // Status Message
                var visible by remember { mutableStateOf(false) }
                var statusText by remember { mutableStateOf("") }
                var statusColor by remember { mutableStateOf(Color(0xFF4BB543)) }
                if (visible) {
                    Text(text = statusText,
                        style = MaterialTheme.typography.bodyMedium.copy(color = statusColor),
                        modifier = Modifier.padding(top = 6.dp).testTag("STATUS_MESSAGE"))
                }

                // Login Button
                Button(onClick = {
                    // Check if user has entered all fields
                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        CoroutineScope(Dispatchers.Main).launch {
                            val loginSuccessful = authViewModel.attemptLogin(email, password)
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
                    shape = RoundedCornerShape(35),
                    modifier = Modifier
                        .height(70.dp)
                        .width(150.dp)
                        .padding(12.dp)
                        .testTag("LOGIN_BUTTON")
                )
                {
                    Text("LOG IN", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                }

                Text(text = "Don't have an account?",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF979C9E)),
                    modifier = Modifier.padding(top = 12.dp))

                TextButton(onClick = { navController.navigate("signup")},
                    modifier = Modifier.testTag("GO_TO_SIGNUP_BUTTON")) {
                    Text("Sign up", style = MaterialTheme.typography.bodyLarge.copy(textDecoration = TextDecoration.Underline, fontWeight = FontWeight.SemiBold))
                }

            }
        }
    }
}