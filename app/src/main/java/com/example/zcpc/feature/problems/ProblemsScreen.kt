package com.example.zcpc.feature.problems

import android.app.AlertDialog
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.zcpc.core.util.openCustomTab
import com.example.zcpc.domain.model.SolvedProblem
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProblemsRoute(
    modifier: Modifier = Modifier,
    viewModel: ProblemsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProblemsScreen(
        uiState = uiState,
        modifier = modifier,
        onRefresh = { viewModel.refreshProblems() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProblemsScreen(
    uiState: ProblemsUiState,
    modifier: Modifier = Modifier,
    onRefresh: () -> Unit
) {
    val context = LocalContext.current
    var selectedTag by remember { mutableStateOf<String?>(null) }
    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text("Problem Stats") }) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize(), contentAlignment = Alignment.Center) {
            when (uiState) {
                is ProblemsUiState.Loading -> CircularProgressIndicator()
                is ProblemsUiState.Error -> Text(text = uiState.message, color = MaterialTheme.colorScheme.error)
                is ProblemsUiState.Success -> {
                    if (selectedTag != null) {
                        val problemsForTag = uiState.problemsByTag[selectedTag] ?: emptyList()
                        TagProblemsDialog(
                            tag = selectedTag!!,
                            problems = problemsForTag,
                            onDismiss = { selectedTag = null },
                            onProblemClick = { contestId, index ->
                                val url = "https://codeforces.com/problemset/problem/$contestId/$index"
                                openCustomTab(context, url)
                            }
                        )
                    }
                    PullToRefreshBox(
                        isRefreshing = uiState.isRefreshing,
                        onRefresh = onRefresh,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 140.dp),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Total Solved
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                                ) {
                                    Column(modifier = Modifier.padding(24.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Total Solved", style = MaterialTheme.typography.titleMedium)
                                        Text("${uiState.totalSolved}", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            // Bar Chart
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Column {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    RatingBarChart(distribution = uiState.ratingDistribution)
                                }
                            }

                            // Topics Title
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Column {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text("Topics Mastered", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Grid of Tags
                            items(uiState.tagCounts) { (tag, count) ->
                                Card(
                                    modifier = Modifier.clickable { selectedTag = tag },
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(text = tag.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                                        Text(text = "$count solved", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RatingBarChart(distribution: List<Pair<Int, Int>>) {
    if(distribution.isEmpty()) return

    val maxCount = distribution.maxOf { it.second }.coerceAtLeast(1)
    var isAnimated by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isAnimated = true
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Rating Distribution",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Bottom,
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            itemsIndexed(distribution) { index, (rating, count) ->
                val targetFraction = (count.toFloat() / maxCount) * 0.8f

                val animatedFraction by animateFloatAsState(
                    targetValue = if(isAnimated) targetFraction else 0f,
                    animationSpec = tween(
                        durationMillis = 800,
                        delayMillis = index * 40,
                        easing = FastOutSlowInEasing
                    ),
                    label = "BarHeightAnimation"
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Text(
                        text = count.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .width(36.dp)
                            .fillMaxHeight(fraction = animatedFraction)
                            .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                            .background(MaterialTheme.colorScheme.primary)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = rating.toString(),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
@Composable
private fun TagProblemsDialog(
    tag: String,
    problems: List<SolvedProblem>,
    onDismiss: () -> Unit,
    onProblemClick: (contestId: Int, index: String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = tag.replaceFirstChar { it.uppercase() } + " Problems",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(problems, key = { it.name }) { problem ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if(problem.contestId != null) {
                                    onProblemClick(problem.contestId, problem.index)
                                }
                            }
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "${problem.contestId}${problem.index} - ${problem.name}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (problem.rating > 0) {
                            Text(
                                text = "Rating: ${problem.rating}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Divider(modifier = Modifier.padding(top = 12.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}