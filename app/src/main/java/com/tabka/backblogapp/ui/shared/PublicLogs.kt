package com.tabka.backblogapp.ui.shared

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.tabka.backblogapp.R
import com.tabka.backblogapp.network.models.LogData
import com.tabka.backblogapp.network.models.tmdb.MovieData
import com.tabka.backblogapp.ui.screens.LogEntry
import com.tabka.backblogapp.ui.viewmodels.LogViewModel
import kotlin.math.ceil

@Composable
fun PublicLogs(navController: NavHostController, publicLogs: List<LogData>, logViewModel: LogViewModel) {
    Text("Public Logs", style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier
            .padding(bottom = 8.dp)
            .testTag("PAGE_SUB_TITLE"))
    DisplayPublicLogs(navController = navController, allLogs = publicLogs, logViewModel = logViewModel)
}

@Composable
fun DisplayPublicLogs(navController: NavHostController, allLogs: List<LogData>?, logViewModel: LogViewModel) {
    val multiplier = ceil(allLogs!!.size / 2.0).toInt()
    val containerHeight: Dp = (185 * multiplier).dp

    Box(
        modifier = Modifier
            .height(containerHeight)
            .fillMaxWidth()
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(allLogs, { it.logId ?: 0 }) { log ->
                var painter by remember { mutableStateOf<Painter?>(null) }
                var movieData by remember { mutableStateOf<Pair<MovieData?, String>>(null to "") }
                val movieId = log.movieIds?.firstOrNull()

                LaunchedEffect(log.logId, movieId) {
                    movieId?.let {
                        logViewModel.fetchMovieDetails(it) { result ->
                            result.data?.let { data ->
                                movieData = data
                            }
                        }
                    }
                }

                painter = if (movieData.first != null) {
                    rememberAsyncImagePainter("https://image.tmdb.org/t/p/w500/${movieData.second}")
                } else {
                    painterResource(id = R.drawable.emptylog)
                }
                LogEntry(navController = navController, log.logId ?: "", log.name ?: "", painter!!)
            }
            /*items(allLogs.size, key = { index -> allLogs[index].logId!! }) { index ->
                val log = allLogs[index]
                var painter by remember { mutableStateOf<Painter?>(null) }
                var movieData by remember { mutableStateOf<Pair<MovieData?, String>>(null to "") }

                painter = if (movieData.first != null) {
                    rememberAsyncImagePainter("https://image.tmdb.org/t/p/w500/${movieData.second}")
                } else {
                    painterResource(id = R.drawable.emptylog)
                }

                Card(
                    modifier = Modifier
                        .size(175.dp)
                        .clickable { navController.navigate("public_log_details_${log.logId}") },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors( containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Image(
                            painter = painter!!,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    color = Color.Black.copy(alpha = 0.75f),
                                    shape = RoundedCornerShape(5.dp)
                                )
                        )

                        // Text overlay
                        Text(
                            text = "${log.name}",
                            style = MaterialTheme.typography.headlineMedium,
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
            }*/
        }
    }
}