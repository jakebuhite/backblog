package com.tabka.backblogapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tabka.backblogapp.R

private val TAG = "SearchScreen"

@Composable
fun SearchScreen(navController: NavController) {
    val hasBackButton = false
    val isMovieDetails = false
    val pageTitle = "Search"

    BaseScreen(navController, hasBackButton, isMovieDetails, pageTitle) {
        SearchBarPlaceholder(navController)
        Spacer(modifier = Modifier.height(30.dp))
        BrowseCategories(navController)
        Spacer(modifier = Modifier.height(75.dp))
        //FriendsAdded()
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
                modifier = Modifier
                    .clickable { navController.navigate("search_results") }
                    .testTag("SEARCH_BAR_LAYOUT")

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
                )
            }
        }
    }
}

@Composable
fun BrowseCategories(navController: NavController) {
    Row(modifier = Modifier.fillMaxSize()) {
        Text(
            "Browse Categories", style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.testTag("BROWSE_CATEGORIES_TITLE")
        )
    }

    Spacer(modifier = Modifier.height(15.dp))

    /*    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
            items(4) { index ->
                Category()
            }
        }*/

    val genreList = listOf(
        ("Action" to 28 to R.drawable.action),
        ("Adventure" to 12 to R.drawable.adventure),
        ("Animation" to 16 to R.drawable.animation),
        ("Comedy" to 35 to R.drawable.comedy)
    )

    val genreList2 = listOf(
        ("Crime" to 80 to R.drawable.crime),
        ("Drama" to 18 to R.drawable.horror),
        ("Family" to 10751 to R.drawable.horror),
        ("Fantasy" to 14 to R.drawable.horror)
    )


    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.height(210.dp)
    ) {
        items(genreList) { genre ->
            Category(navController, genre)
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .height(200.dp)
    ) {
        items(genreList2) { genre ->
            Category(navController, genre)
        }
    }
}


@Composable
fun Category(navController: NavController, genre: Pair<Pair<String, Int>, Int>) {
    Card(
        modifier = Modifier
            .height(100.dp)
            .width(185.dp)
            .padding(end = 10.dp, bottom = 10.dp)
            .clickable { navController.navigate("category_results_${genre.first.second}_${genre.first.first}") },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Box(
            modifier = Modifier
                /*.height(100.dp)
                .width(183.dp)*/
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = genre.second),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
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
                text = genre.first.first,
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