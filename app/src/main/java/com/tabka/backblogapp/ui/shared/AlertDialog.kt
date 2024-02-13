package com.tabka.backblogapp.ui.shared

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LocalMinimumTouchTargetEnforcement
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.tabka.backblogapp.network.models.AlertDialog

private val TAG = "AlertDialog"
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CustomAlertDialog(header: String?, message: String?, dismissText: String?, dismissTextColor: Color?,
                      acceptText: String?, acceptTextColor: Color?, onDismiss: () -> Unit, onExit: () -> Unit) {
    Dialog(
        onDismissRequest = { onDismiss() }, properties = DialogProperties(
            dismissOnBackPress = false, dismissOnClickOutside = false
        )
    ) {
        Card(
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth().padding(0.dp).height(IntrinsicSize.Min),
            elevation = 0.dp
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                Text(
                    text = header ?: "",
                    modifier = Modifier.padding(8.dp, 16.dp, 8.dp, 2.dp)
                        .align(Alignment.CenterHorizontally).fillMaxWidth(), fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = message ?: "",
                    modifier = Modifier.padding(8.dp, 2.dp, 8.dp, 16.dp)
                        .align(Alignment.CenterHorizontally).fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Divider(color = Color.Gray, modifier = Modifier.fillMaxWidth().width(1.dp))
                Row(Modifier.padding(top = 0.dp)) {
                    CompositionLocalProvider(
                        LocalMinimumTouchTargetEnforcement provides false,
                    ) {
                        TextButton(
                            onClick = { onDismiss() },
                            Modifier
                                .fillMaxWidth()
                                .padding(0.dp)
                                .weight(1F)
                                .border(0.dp, Color.Transparent)
                                .height(48.dp),
                            elevation = ButtonDefaults.elevation(0.dp, 0.dp),
                            shape = RoundedCornerShape(0.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(text = dismissText ?: "", color = dismissTextColor ?: Color.DarkGray)
                        }
                    }
                    Divider(color = Color.Gray, modifier =
                    Modifier.fillMaxHeight().width(1.dp))
                    CompositionLocalProvider(
                        LocalMinimumTouchTargetEnforcement provides false,
                    ) {
                        TextButton(
                            onClick = {
                                onExit.invoke()
                            },
                            Modifier
                                .fillMaxWidth()
                                .padding(0.dp)
                                .weight(1F)
                                .border(0.dp, color = Color.Transparent)
                                .height(48.dp),
                            elevation = ButtonDefaults.elevation(0.dp, 0.dp),
                            shape = RoundedCornerShape(0.dp),
                            contentPadding = PaddingValues()
                        ) {
                            Text(text = acceptText ?: "", color = acceptTextColor ?: Color.Blue)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShowAlertDialog(alertDialog: AlertDialog, setAlertDialogState: (AlertDialog) -> Unit) {
    Log.d(TAG, "The alert dialog: $alertDialog")
    if (alertDialog.isVisible) {
        CustomAlertDialog(
            header = alertDialog.header,
            message = alertDialog.message,
            dismissText = alertDialog.dismiss?.text,
            dismissTextColor = alertDialog.dismiss?.textColor,
            acceptText = alertDialog.accept?.text,
            acceptTextColor = alertDialog.accept?.textColor,
            onDismiss = {
                setAlertDialogState(
                    AlertDialog(
                        isVisible = false
                    )
                )
            },
            onExit = {
                Log.d(TAG, "Do something else")
                alertDialog.accept?.action?.let { it() }
                setAlertDialogState(
                    AlertDialog(
                        isVisible = false
                    )
                )
            })
    }
}

@Composable
fun UpdateAlertDialog(alertDialog: MutableState<AlertDialog>, newDialog: AlertDialog) {
    alertDialog.value = newDialog
}