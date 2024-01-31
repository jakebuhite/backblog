package com.tabka.backblogapp.ui.shared

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun PasswordTextField(
    label: String,
    onPasswordChange: (String) -> Unit
) {
    var password by remember { mutableStateOf("") }
    TextField(
        value = password,
        onValueChange = {
            password = it
            onPasswordChange(it)
        },
        label = { Text(label) },
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
}