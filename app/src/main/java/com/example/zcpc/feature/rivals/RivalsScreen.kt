package com.example.zcpc.feature.rivals


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.zcpc.domain.model.UserProfile
import com.example.zcpc.feature.profile.components.getRankColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.ui.platform.LocalContext
import com.example.zcpc.core.util.openCustomTab

@Composable
fun RivalsRoute(
    modifier: Modifier = Modifier,
    viewModel: RivalsViewModel = hiltViewModel(),
    onNavigateToNotifications: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    RivalsScreen(
        uiState = uiState,
        modifier = modifier,
        onAddRival = { viewModel.addRival(it) },
        onRemoveRival = { viewModel.removeRival(it) },
        onNavigateToNotifications = onNavigateToNotifications,
        onClearError = { viewModel.clearError() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RivalsScreen(
    uiState: RivalsUiState,
    modifier: Modifier = Modifier,
    onAddRival: (String) -> Unit,
    onRemoveRival: (UserProfile) -> Unit,
    onNavigateToNotifications: () -> Unit,
    onClearError: () -> Unit
) {
    var searchInput by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedRivalHandle by remember { mutableStateOf<String?>(null) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text("Rivals & Friends")
                },
                actions = {
                    IconButton(onClick = onNavigateToNotifications) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when (uiState) {
                is RivalsUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is RivalsUiState.Success -> {
                    LaunchedEffect(uiState.errorMessage) {
                        uiState.errorMessage?.let {
                            snackbarHostState.showSnackbar(it)
                            onClearError()
                        }
                    }

                    if (selectedRivalHandle != null) {
                        val failedProblems = uiState.rivalsFailedProblems[selectedRivalHandle] ?: emptyList()
                        RivalFailedProblemsDialog(
                            handle = selectedRivalHandle!!,
                            problems = failedProblems,
                            onDismiss = { selectedRivalHandle = null }
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        // Search & Add Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = searchInput,
                                onValueChange = { searchInput = it },
                                label = { Text("Codeforces Handle") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            Button(
                                onClick = {
                                    onAddRival(searchInput)
                                    searchInput = ""
                                },
                                enabled = searchInput.isNotBlank() && !uiState.isSearching,
                                modifier = Modifier.height(56.dp)
                            ) {
                                if (uiState.isSearching) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                } else {
                                    Icon(Icons.Default.Add, contentDescription = "Add Rival")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (uiState.rivals.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No rivals added yet. Search a handle above to track!",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(uiState.rivals, key = { it.handle }) { rival ->
                                    val failedCount = uiState.rivalsFailedProblems[rival.handle]?.size ?: 0
                                    RivalCard(
                                        rival = rival,
                                        failedCount = failedCount,
                                        onDelete = { onRemoveRival(rival) },
                                        onShowFailed = { selectedRivalHandle = rival.handle }
                                    )
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
private fun RivalCard(
    rival: UserProfile,
    failedCount: Int,
    onDelete: () -> Unit,
    onShowFailed: () -> Unit
) {
    val rankColor = getRankColor(rival.rank)
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val url = "https://codeforces.com/profile/${rival.handle}"
                openCustomTab(context, url)
            }
        ,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = rival.avatarUrl,
                contentDescription = "${rival.handle}'s Avatar",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = rival.handle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = rankColor
                )
                Text(
                    text = "${rival.rank} • Rating: ${rival.currentRating}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (failedCount > 0) {
                    Text(
                        text = "$failedCount unsolved problems",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.clickable { onShowFailed() }
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Remove Rival",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun RivalFailedProblemsDialog(
    handle: String,
    problems: List<com.example.zcpc.domain.model.Submission>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "$handle's Unsolved Problems",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            if (problems.isEmpty()) {
                Text("No unsolved problems found.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(problems) { problem ->
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable {
                                if (problem.contestId != null) {
                                    val url = "https://codeforces.com/problemset/problem/${problem.contestId}/${problem.index}"
                                    openCustomTab(context, url)
                                }
                            },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = problem.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${problem.contestId}${problem.index} - ${problem.verdict}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
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
