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
import androidx.compose.ui.platform.LocalContext
import com.example.zcpc.core.util.openCustomTab

@Composable
fun RivalsRoute(
    modifier: Modifier = Modifier,
    viewModel: RivalsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    RivalsScreen(
        uiState = uiState,
        modifier = modifier,
        onAddRival = { viewModel.addRival(it) },
        onRemoveRival = { viewModel.removeRival(it) },
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
    onClearError: () -> Unit
) {
    var searchInput by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text("Rivals & Friends") }) },
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
                                    RivalCard(
                                        rival = rival,
                                        onDelete = { onRemoveRival(rival) },

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
    onDelete: () -> Unit
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