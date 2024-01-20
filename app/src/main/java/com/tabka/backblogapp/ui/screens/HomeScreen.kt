package com.tabka.backblogapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tabka.backblogapp.R

@Composable
fun HomeScreen(bottomPadding: Dp) {
    val pageTitle = "What's Next?"
    BaseScreen(pageTitle) {
            WatchNextCard()
            Spacer(Modifier.height(40.dp))
            LogsList()
            Spacer(
                Modifier
                    .statusBarsPadding()
                    .navigationBarsPadding()
            )
        }
        Spacer(Modifier.height(20.dp))
}

@Preview
@Composable
fun WatchNextCard() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Priority Log title
        Row() {
            Text("From Personal Log", style = MaterialTheme.typography.titleSmall)
        }

        Spacer(modifier = Modifier.height(5.dp))

        // Next Movie image
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Box(
                    modifier = Modifier.height(200.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.tenetdefault),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(5.dp))

        // Movie Title, Rating, Year, Complete button
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ){
            // Movie information
            Column(modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
            )
            {
                // Title
                Row() {
                    Text(text = "Tenet", style = MaterialTheme.typography.headlineMedium)
                }

                Row() {
                    // Rating
                    Column() {
                        Text(text = "PG-13", style = MaterialTheme.typography.bodySmall)
                    }

                    Spacer(modifier = Modifier.width(5.dp))

                    // Release Date
                    Column(
                    ) {
                        Text(text = "2022", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            // Complete button
            Column(modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
                horizontalAlignment = Alignment.End
            ) {
                Image(
                    painter = painterResource(id = R.drawable.checkbutton2),
                    contentDescription = "Check icon",
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }

   /* // Watch Next data
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = "Tenet", style = MaterialTheme.typography.headlineMedium)
            Text(text = "PG-13 2020", style = MaterialTheme.typography.bodySmall)
        }
        Spacer(Modifier.weight(1f))
        Image(
            painter = painterResource(id = R.drawable.check_icon),
            contentDescription = "Check icon",
            modifier = Modifier.size(40.dp)
        )
    }*/
}

@Composable
fun LogsList() {
    // My Logs Title
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            Text("My Logs", style = MaterialTheme.typography.headlineMedium)
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
            /*.padding(end = 5.dp)*/,
            horizontalAlignment = Alignment.End
        ) {
            Image(
                painter = painterResource(id = R.drawable.newlog),
                contentDescription = "New Log button",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(35.dp)
            )
        }
    }

    Spacer(modifier = Modifier.height(15.dp))

    // Grid of logs
    val logItems = (1..10).toList()

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        val itemsPerRow = 2
        val rows = logItems.chunked(itemsPerRow)

        rows.forEach { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                rowItems.forEach { index ->
                    Card(
                        modifier = Modifier.size(175.dp),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(175.dp)
                                .fillMaxSize()
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.creator),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                            )

                            // Text overlay
                            Text(
                                text = "Item $index",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .size(175.dp)
                                    .wrapContentHeight(align = Alignment.CenterVertically)
                                    .drawBehind {
                                        drawRoundRect(
                                            color = Color.Black,
                                            cornerRadius = CornerRadius(20.dp.toPx()),
                                            alpha = 0.75f

                                        )
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}

    /*Box(
                      modifier = Modifier
                          .size(180.dp),
                      contentAlignment = Alignment.Center
                  ) {
                      // Background image with alpha
                      Image(
                          painter = painterResource(id = R.drawable.sample_log),
                          contentDescription = null,
                          modifier = Modifier
                              .fillMaxSize()
                              .clip(MaterialTheme.shapes.medium)
                              .background(Color.Black.copy(alpha = 0.35f))
                      )
*/

/*@Composable
fun BackgroundGradient() {
    // Define the gradient colors
    val lightGrey = Color(0xFF37414A)
    val darkGrey = Color(0xFF191919)

    val gradientColors = listOf(lightGrey, darkGrey)

    // Create a vertical gradient brush
    Box(modifier = Modifier.background(Brush.verticalGradient(gradientColors)))
}*/
