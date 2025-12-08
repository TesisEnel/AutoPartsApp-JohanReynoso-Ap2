package com.autoparts.presentation.admin.citas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.autoparts.domain.model.Cita
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCitasScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit = {},
    viewModel: AdminCitasViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is AdminCitasUiEffect.NavigateToLogin -> onNavigateToLogin()
                is AdminCitasUiEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.onEvent(AdminCitasUiEvent.ClearError)
        }
    }

    LaunchedEffect(state.successMessage) {
        state.successMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.onEvent(AdminCitasUiEvent.ClearSuccess)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Gestión de Citas")
                        Text(
                            text = "Panel de Administrador",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                state.citas.isEmpty() -> {
                    EmptyCitasContent(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    CitasAdminContent(
                        state = state,
                        isProcessing = state.isProcessing,
                        onConfirmar = { citaId ->
                            viewModel.onEvent(AdminCitasUiEvent.ConfirmarCita(citaId))
                        },
                        onRechazar = { citaId ->
                            viewModel.onEvent(AdminCitasUiEvent.RechazarCita(citaId))
                        },
                        onFiltroChanged = { filtro ->
                            viewModel.onEvent(AdminCitasUiEvent.OnFiltroEstadoChanged(filtro))
                        },
                        onSearchChanged = { query ->
                            viewModel.onEvent(AdminCitasUiEvent.OnSearchQueryChanged(query))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CitasAdminContent(
    state: AdminCitasUiState,
    isProcessing: Boolean,
    onConfirmar: (Int) -> Unit,
    onRechazar: (Int) -> Unit,
    onFiltroChanged: (FiltroEstadoCita) -> Unit,
    onSearchChanged: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Barra de búsqueda
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = onSearchChanged,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { Text("Buscar por cliente o servicio...") },
            leadingIcon = {
                Icon(Icons.Default.Search, "Buscar")
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        PrimaryScrollableTabRow(
            selectedTabIndex = state.filtroEstado.ordinal,
            modifier = Modifier.fillMaxWidth(),
            edgePadding = 16.dp
        ) {
            FiltroEstadoCita.entries.forEach { filtro ->
                Tab(
                    selected = state.filtroEstado == filtro,
                    onClick = { onFiltroChanged(filtro) },
                    text = {
                        Text(
                            text = when (filtro) {
                                FiltroEstadoCita.TODAS -> "Todas"
                                FiltroEstadoCita.APROBADAS -> "Aprobadas"
                                FiltroEstadoCita.PENDIENTES -> "Pendientes"
                                FiltroEstadoCita.DENEGADAS -> "Denegadas"
                            },
                            color = if (state.filtroEstado == filtro)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    }
                )
            }
        }

        HorizontalDivider()

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Citas Encontradas",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "${state.citasFiltradas.size}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            items(state.citasFiltradas, key = { it.citaId }) { cita ->
                CitaAdminCard(
                    cita = cita,
                    isProcessing = isProcessing,
                    onConfirmar = { onConfirmar(cita.citaId) },
                    onRechazar = { onRechazar(cita.citaId) }
                )
            }
        }
    }
}

@Composable
private fun CitaAdminCard(
    cita: Cita,
    isProcessing: Boolean,
    onConfirmar: () -> Unit,
    onRechazar: () -> Unit
) {
    var showConfirmDialog by remember { mutableStateOf(false) }
    var actionType by remember { mutableStateOf<String?>(null) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                cita.confirmada -> MaterialTheme.colorScheme.secondaryContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        border = if (!cita.confirmada) {
            androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
        } else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Event,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Cita #${cita.citaId}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    color = if (cita.confirmada) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.tertiary
                    },
                    shape = MaterialTheme.shapes.small,
                    tonalElevation = 2.dp
                ) {
                    Text(
                        text = if (cita.confirmada) "Confirmada" else "Pendiente",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = if (cita.confirmada) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onTertiary
                        }
                    )
                }
            }

            HorizontalDivider()

            InfoRow(
                icon = Icons.Default.Person,
                label = "Cliente",
                value = cita.clienteNombre
            )

            InfoRow(
                icon = Icons.Default.Build,
                label = "Servicio",
                value = cita.servicioSolicitado
            )

            InfoRow(
                icon = Icons.Default.CalendarToday,
                label = "Fecha",
                value = formatDate(cita.fechaCita)
            )

            if (cita.codigoConfirmacion.isNotEmpty()) {
                InfoRow(
                    icon = Icons.Default.Key,
                    label = "Código",
                    value = cita.codigoConfirmacion
                )
            }

            if (!cita.confirmada) {
                HorizontalDivider()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            actionType = "rechazar"
                            showConfirmDialog = true
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !isProcessing,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Rechazar")
                    }

                    Button(
                        onClick = {
                            actionType = "confirmar"
                            showConfirmDialog = true
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !isProcessing
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Confirmar")
                    }
                }
            }
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = {
                Text(
                    if (actionType == "confirmar") "Confirmar Cita" else "Rechazar Cita"
                )
            },
            text = {
                Text(
                    "¿Estás seguro de que deseas ${if (actionType == "confirmar") "confirmar" else "rechazar"} " +
                    "la cita de ${cita.clienteNombre}?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (actionType == "confirmar") onConfirmar() else onRechazar()
                        showConfirmDialog = false
                    },
                    colors = if (actionType == "rechazar")
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ) else ButtonDefaults.buttonColors()
                ) {
                    Text(if (actionType == "confirmar") "Confirmar" else "Rechazar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun EmptyCitasContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CalendarToday,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No hay citas registradas",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Las citas de los clientes aparecerán aquí",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

private fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.forLanguageTag("es-DO"))
        val date = inputFormat.parse(dateString)
        outputFormat.format(date ?: Date())
    } catch (_: Exception) {
        dateString
    }
}