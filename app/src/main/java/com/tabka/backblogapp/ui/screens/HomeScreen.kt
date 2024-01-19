package com.tabka.backblogapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tabka.backblogapp.R

@Composable
fun HomeScreen(bottomPadding: Dp) {
    BaseScreen() {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 30.dp)
                .verticalScroll(rememberScrollState())
        ) {
            WatchNextCard()
            Spacer(Modifier.height(40.dp))
            LogsList()
            Spacer(
                Modifier.statusBarsPadding().navigationBarsPadding()
            )
            /*        Spacer(Modifier.height(20.dp))*/
        }
    }
}

@Composable
fun WatchNextCard() {
    Text("What's Next?", style = MaterialTheme.typography.headlineLarge)

    // Load img
    val whatNextImg = painterResource(id = R.drawable.sample_what_next)
    Image(
        painter = whatNextImg,
        contentDescription = "Watch next image",
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(
                ratio = whatNextImg.intrinsicSize.width /
                        whatNextImg.intrinsicSize.height
            )
    )

    // Watch Next data
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
    }
}

@Composable
fun LogsList() {
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(text = "My Logs", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.weight(1f))
        Image(
            painter = painterResource(id = R.drawable.add_icon),
            contentDescription = "Add icon",
            modifier = Modifier.size(40.dp)
        )
    }

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
                    Box(
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

                        // Text overlay
                        Text(
                            text = "Item $index",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

/*@Composable
fun BackgroundGradient() {
    // Define the gradient colors
    val lightGrey = Color(0xFF37414A)
    val darkGrey = Color(0xFF191919)

    val gradientColors = listOf(lightGrey, darkGrey)

    // Create a vertical gradient brush
    Box(modifier = Modifier.background(Brush.verticalGradient(gradientColors)))
}*/
