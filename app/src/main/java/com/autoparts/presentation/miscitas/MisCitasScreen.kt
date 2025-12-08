package com.autoparts.presentation.miscitas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.autoparts.presentation.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisCitasScreen(
    navController: NavController,
    viewModel: MisCitasViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showFilterMenu by remember { mutableStateOf(false) }

    LaunchedEffect(state.successMessage) {
        state.successMessage?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is MisCitasUiEffect.NavigateToLogin -> {
                    navController.navigate(
                        Screen.Login.createRoute("Inicie sesión para ver sus citas")
                    ) {
                        popUpTo(Screen.MisCitas.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
                is MisCitasUiEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is MisCitasUiEffect.CitaCancelada -> {
                    snackbarHostState.showSnackbar("Cita cancelada exitosamente")
                }
                is MisCitasUiEffect.NavigateToDetalle -> {
                }
            }
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let { error ->
            if (!error.contains("401", ignoreCase = true) &&
                !error.contains("unauthorized", ignoreCase = true) &&
                !error.contains("sesión", ignoreCase = true) &&
                !error.contains("login", ignoreCase = true)) {
                snackbarHostState.showSnackbar(error)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Citas") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { showFilterMenu = true }) {
                        Icon(Icons.Default.FilterList, "Filtrar")
                    }
                    DropdownMenu(
                        expanded = showFilterMenu,
                        onDismissRequest = { }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Todas") },
                            onClick = {
                                viewModel.handleIntent(MisCitasIntent.FiltrarPorEstado(null))
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Confirmadas") },
                            onClick = {
                                viewModel.handleIntent(MisCitasIntent.FiltrarPorEstado(true))
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Pendientes") },
                            onClick = {
                                viewModel.handleIntent(MisCitasIntent.FiltrarPorEstado(false))
                            }
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                state.error != null && state.citas.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Inicia sesión para ver tus citas",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Button(onClick = {
                            navController.navigate(Screen.Login.createRoute()) {
                                popUpTo(Screen.Home.route)
                            }
                        }) {
                            Icon(Icons.Default.Login, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Iniciar Sesión")
                        }
                    }
                }
                state.citasFiltradas.isEmpty() -> {
                    Text(
                        text = "No tienes citas registradas",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.citasFiltradas) { cita ->
                            CitaCard(
                                cita = cita,
                                isProcessing = state.isProcessing,
                                onCancelar = {
                                    viewModel.handleIntent(MisCitasIntent.CancelarCita(cita.citaId))
                                },
                                onClick = {
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CitaCard(
    cita: CitaUI,
    isProcessing: Boolean,
    onCancelar: () -> Unit,
    onClick: () -> Unit
) {
    var showCancelDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = cita.servicioSolicitado,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    color = Color(cita.estadoColor),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = cita.estadoTexto,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }
            }

            Text(
                text = "Cliente: ${cita.clienteNombre}",
                style = MaterialTheme.typography.bodyMedium
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.CalendarToday,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = cita.fechaFormateada,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.AccessTime,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = cita.horaFormateada,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
            Text(
                text = "Código: ${cita.codigoConfirmacion}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            if (!cita.confirmada) {
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedButton(
                    onClick = { showCancelDialog = true },
                    enabled = !isProcessing,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        Icons.Default.Cancel,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cancelar Cita")
                }
            } else {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Esta cita ya fue confirmada por el administrador",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Cancelar Cita") },
            text = {
                Text("¿Estás seguro que deseas cancelar esta cita? Esta acción no se puede deshacer.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onCancelar()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Cancelar Cita")
                }
            },
            dismissButton = {
                TextButton(onClick = { }) {
                    Text("Mantener")
                }
            }
        )
    }
}