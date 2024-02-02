package com.tabka.backblogapp.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.*
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tabka.backblogapp.R

private val TAG = "SearchScreen"

@Composable
fun SearchScreen(navController: NavController) {
    val hasBackButton = false
    val pageTitle = "Search"

    BaseScreen(navController, hasBackButton, pageTitle) {
        SearchBarPlaceholder(navController)
        Spacer(modifier = Modifier.height(40.dp))
        BrowseCategories()
        Spacer(modifier = Modifier.height(40.dp))
        FriendsAdded()
    }
}

@Composable
fun SearchBarPlaceholder(navController: NavController) {
    var text by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(5.dp)
        ) {
            Row(
                modifier = Modifier.clickable {  }
            ) {
                TextField(
                    value = text,
                    onValueChange = {},
                    enabled = false,
                    placeholder = { Text("Search for a movie") },
                    maxLines = 1,
                    leadingIcon = {
                        androidx.compose.material.Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.Gray, // Adjust the tint color
                            modifier = Modifier.padding(8.dp)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .clickable { navController.navigate("search_results") }
                )
            }
        }
    }
}

@Composable
fun BrowseCategories() {
    Row(modifier = Modifier.fillMaxSize()) {
        Text("Browse Categories", style = MaterialTheme.typography.headlineMedium)
    }

    Spacer(modifier = Modifier.height(15.dp))

/*    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
        items(4) { index ->
            Category()
        }
    }*/

    val genreList = listOf(
        "Action",
        "Adventure",
        "Animation",
        "Comedy",
        "Crime",
        "Drama",
        "Family",
        "Fantasy"
    )

    LazyRow() {
        items(genreList) { genre ->
            Column {
                Category(genre)
            }
        }
    }
}


@Composable
fun Category(genre: String) {
    Card(
        modifier = Modifier
            .height(95.dp)
            .width(190.dp)
            .padding(end = 20.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .height(95.dp)
                .width(175.dp)
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.creator),
                contentDescription = null,
                contentScale = ContentScale.Crop,
            )


            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = Color.Black.copy(alpha = 0.75f), // Transparent black color
                        shape = RoundedCornerShape(5.dp)
                    )
            )

            // Text overlay
            Text(
                text = genre,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .align(Alignment.Center)
                    .wrapContentHeight(align = Alignment.CenterVertically)
            )
        }
    }
}

@Composable
fun FriendsAdded() {
    Row(modifier = Modifier.fillMaxSize()) {
        Text("Friends Recently Added", style = MaterialTheme.typography.headlineMedium)
    }

    Spacer(modifier = Modifier.height(15.dp))

    LazyRow(modifier = Modifier.fillMaxWidth()) {
        items(4) { index ->
            FriendMovie()
        }
    }
}

@Composable
fun FriendMovie() {
    Column(modifier = Modifier.padding(end = 10.dp)) {
        Column(modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Card(
                    modifier = Modifier
                        .height(180.dp)
                        .width(140.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
                )
                {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.creator),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                        )
                    }
                }
            }
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)) {

                Text(
                    text = "The Creator",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .wrapContentWidth(align = Alignment.CenterHorizontally)
                )
            }
        }
    }
}