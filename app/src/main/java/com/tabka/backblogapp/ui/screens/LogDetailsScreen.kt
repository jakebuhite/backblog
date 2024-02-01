package com.tabka.backblogapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tabka.backblogapp.R
import com.tabka.backblogapp.ui.viewmodels.LogDetailsViewModel
import kotlin.math.abs
import kotlin.math.ceil

private val TAG = "LogDetailsScreen"


@Composable
fun LogDetailsScreen(navController: NavController, logId: String?) {
    val logDetailsViewModel: LogDetailsViewModel = viewModel()
    val log = logDetailsViewModel.log!!

    val hasBackButton = true
    val pageTitle = log.name!!

    BaseScreen(navController, hasBackButton, pageTitle) {
        DetailBar()
        Spacer(modifier = Modifier.height(20.dp))
        LogButtons()
        Spacer(modifier = Modifier.height(20.dp))
        LogList()

    }
}

@Composable
fun DetailBar() {
    Row(modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically) {
        // Creator Picture
        Column(modifier = Modifier.padding(end = 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(35.dp),
                colorFilter = ColorFilter.tint(color = colorResource(id = R.color.white))
            )
        }

        // Collaborator Pictures
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(35.dp),
                colorFilter = ColorFilter.tint(color = colorResource(id = R.color.white))
            )
        }

        // Number of Movies
        Column(modifier = Modifier.padding(start = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text("7 Movies", style = androidx.compose.material3.MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun LogButtons() {
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
        .padding(horizontal = 7.dp),
        verticalAlignment = Alignment.CenterVertically) {

        Row(modifier = Modifier.weight(1F)) {
            // Collaborators Icon
            Column(modifier = Modifier
                .weight(1F)
                /*.width(60.dp)*/
                .fillMaxHeight()
                .padding(end = 10.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.user_add),
                    contentDescription = "Add Icon",
                    modifier = Modifier.size(35.dp)
                )
            }

            // Edit Log Icon
            Column(modifier = Modifier
                .weight(3F)
                .fillMaxHeight(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center) {
                Image(
                    painter = painterResource(id = R.drawable.edit),
                    contentDescription = "Edit",
                    modifier = Modifier.size(35.dp)
                )
            }
        }

        Row(modifier = Modifier.weight(1F)) {
            // Shuffle Icon
            Column(modifier = Modifier
                .weight(2F)
                .fillMaxHeight(),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center) {
                Image(
                    painter = painterResource(id = R.drawable.shuffle_arrow),
                    contentDescription = "Shuffle",
                    modifier = Modifier
                        .size(35.dp)
                        .fillMaxHeight()
                )
            }

            // Add Movie Icon
            Column(modifier = Modifier
                .weight(1F)
                .padding(start = 10.dp)
                .fillMaxHeight(),
                horizontalAlignment = Alignment.End) {
                Image(
                    painter = painterResource(id = R.drawable.add),
                    contentDescription = "Add Icon",
                    modifier = Modifier.size(50.dp)
                )
            }
        }
    }
}

@Composable
fun LogList() {

    val logs = listOf(
        "Aquaman and the Lost Kingdom",
        "NOPE",
        "The Batman",
        "Get Out",
        "Interstellar",
        "Joker",
        "The Creator",
        "Spider-Man"
    )

    val height: Dp = (70 * logs.size).dp

    LazyColumn(userScrollEnabled = false, modifier = Modifier.height(height)) {
        items(logs) { logName ->
            LogEntry(logName)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LogEntry(logName: String) {
    /*Row(modifier = swipeableModifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

    }*/
    Row(modifier = Modifier.fillMaxWidth()
        .padding(bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center) {

        // Movie Image
        Column(modifier = Modifier
            .weight(2F)
            .fillMaxHeight()) {
            Box(
                modifier = Modifier
                    .width(130.dp)
                    .height(70.dp)
                    .clip(RoundedCornerShape(5.dp))
            ) {
                /*val imageBaseURL =
                    "https://image.tmdb.org/t/p/w500/${movie.backdropPath}"*/
                Image(
                    painter = painterResource(id = R.drawable.creator),
                    contentDescription = "movie image",
                    contentScale = ContentScale.Crop
                )
            }
        }

        // Movie Title
        Column(modifier = Modifier
            .weight(3F)
            .fillMaxHeight()
            .height(70.dp)
            .padding(start = 8.dp),
            verticalArrangement = Arrangement.Center){
            Text(text = logName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color.White)
        }

        // Add Button
        Column(modifier = Modifier
            .weight(1F)
            .height(70.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            Image(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(35.dp),
                colorFilter = ColorFilter.tint(color = colorResource(id = R.color.white))
            )
        }
    }
}