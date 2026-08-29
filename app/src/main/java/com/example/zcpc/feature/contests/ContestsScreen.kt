package com.example.zcpc.feature.contests

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.zcpc.core.util.openCustomTab
import com.example.zcpc.domain.model.Contest
import com.example.zcpc.domain.model.ContestPhase
import java.nio.file.WatchEvent
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun ContestsRoute(
    modifier: Modifier = Modifier,
    viewModel: ContestsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    ContestsScreen(
        uiState = uiState,
        modifier = modifier,
        onRetry = { viewModel.loadContests() },
        onFilterSelected = { filter -> viewModel.setFilter(filter) },
        onContestClick = { contestId ->
            val url = "https://codeforces.com/contestRegistration/$contestId"
            openCustomTab(context, url)
        }
    )
}

@Composable
internal fun ContestsScreen(
    uiState: ContestsUiState,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit,
    onFilterSelected: (ContestFilter) -> Unit,
    onContestClick: (Int) -> Unit
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (uiState) {
            is ContestsUiState.Loading -> CircularProgressIndicator()
            is ContestsUiState.Error -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = uiState.message, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onRetry) { Text("Retry") }
                }
            }
            is ContestsUiState.Success -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = uiState.currentFilter == ContestFilter.ALL,
                                onClick = { onFilterSelected(ContestFilter.ALL) },
                                label = { Text("All") }
                            )
                        }
                        item {
                            FilterChip(
                                selected = uiState.currentFilter == ContestFilter.UPCOMING,
                                onClick = { onFilterSelected(ContestFilter.UPCOMING) },
                                label = { Text("Upcoming") }
                            )
                        }
                        item {
                            FilterChip(
                                selected = uiState.currentFilter == ContestFilter.RUNNING,
                                onClick = { onFilterSelected(ContestFilter.RUNNING) },
                                label = { Text("Running") }
                            )
                        }
                    }
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.filteredContests, key = { it.id }) { contest ->
                            ContestCard(
                                contest = contest,
                                onClick = { onContestClick(contest.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ContestCard(
    contest: Contest,
    onClick: () -> Unit
) {
    val formattedDate = remember(contest.startTimeSeconds) {
        val instant = Instant.ofEpochSecond(contest.startTimeSeconds)
        val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy • hh:mm a")
            .withZone(ZoneId.systemDefault())
        formatter.format(instant)
    }
    val phaseColor = if(contest.phase == ContestPhase.RUNNING) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = contest.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = contest.phase.name,
                    style = MaterialTheme.typography.labelLarge,
                    color = phaseColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}