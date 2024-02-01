package com.tabka.backblogapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tabka.backblogapp.R
import org.burnoutcrew.reorderable.detectReorderAfterLongPress

@Composable
fun SearchScreen(navController: NavController) {
    val hasBackButton = false
    val pageTitle = "Search"

    BaseScreen(navController, hasBackButton, pageTitle) {
        /*Text("Click here to go to results page",
            modifier = Modifier.clickable { navController.navigate("search_results") }
        )*/
        /*Card() {
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(16.dp)
                .clickable {
                    // Navigate to another page with the search query
                    navController.navigate("search_results")
                }
            ) {
                Text("Search for a movie")
            }
        }*/
        SearchBar(navController)
        Spacer(modifier = Modifier.height(20.dp))
        BrowseCategories()
        Spacer(modifier = Modifier.height(10.dp))
        FriendsAdded()
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

 /*   LazyRow() {
        items(genreList) { genre ->
            Column {
                Category(genre)
            }
        }
    }*/
    
    LazyHorizontalGrid(rows = GridCells.Fixed(2),
        modifier = Modifier.height(160.dp)) {
        items(genreList) { genre ->
            Category(genre)
        }
    }
}


@Composable
fun Category(genre: String) {
    Card(
        modifier = Modifier
            .height(80.dp)
            .width(183.dp)
            .padding(end = 10.dp, bottom = 10.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .height(80.dp)
                .width(183.dp)
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
                        .height(160.dp)
                        .width(120.dp),
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