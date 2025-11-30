package com.autoparts.presentation.categorias

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.autoparts.dominio.model.Producto
import com.autoparts.presentation.Inicio.InicioViewModel
import com.autoparts.presentation.components.ProductImage
import com.autoparts.presentation.navigation.Screen
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriasScreen(
    navController: NavController,
    viewModel: InicioViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var selectedCategory by remember { mutableStateOf<String?>("Uso General") } // Iniciar con Uso General
    var minPrice by remember { mutableIntStateOf(100) } // Precio mínimo inicial 100
    var maxPrice by remember { mutableIntStateOf(1000000) }
    var showFilterDialog by remember { mutableStateOf(false) }

    val categorias = listOf(
        "Uso General",
        "Autos o Vehículos Ligeros",
        "Motocicletas",
        "Vehículos Pesados"
    )

    val filteredProducts = state.listProductos.filter { producto ->
        val matchesCategory = selectedCategory?.let { category ->
            producto.categoria.equals(category, ignoreCase = true)
        } ?: true

        val matchesPrice = producto.productoMonto in minPrice..maxPrice

        matchesCategory && matchesPrice
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Categorías") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { showFilterDialog = true }) {
                        Badge(
                            containerColor = if (minPrice > 100 || maxPrice < 1000000)
                                MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Icon(Icons.Default.FilterList, "Filtrar por precio")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            CategoryFilterRow(
                categorias = categorias,
                selectedCategory = selectedCategory,
                onCategorySelected = { category ->
                    selectedCategory = if (selectedCategory == category) null else category
                }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filteredProducts.size} productos",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                if (selectedCategory != null || minPrice > 100 || maxPrice < 1000000) {
                    TextButton(onClick = {
                        selectedCategory = "Uso General"
                        minPrice = 100
                        maxPrice = 1000000
                    }) {
                        Text("Limpiar filtros")
                    }
                }
            }

            if (filteredProducts.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = "Sin resultados",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "No se encontraron productos",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Intenta con otros filtros",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredProducts) { producto ->
                        ProductoFilteredCard(
                            producto = producto,
                            onProductoClick = {
                                navController.navigate(
                                    Screen.ProductoDetalle.createRoute(producto.productoId ?: 0)
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    if (showFilterDialog) {
        PriceFilterDialog(
            minPrice = minPrice,
            maxPrice = maxPrice,
            onMinPriceChange = { minPrice = it },
            onMaxPriceChange = { maxPrice = it },
            onDismiss = { showFilterDialog = false },
            onApply = { showFilterDialog = false }
        )
    }
}

@Composable
fun CategoryFilterRow(
    categorias: List<String>,
    selectedCategory: String?,
    onCategorySelected: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categorias) { categoria ->
            CategoryChip(
                categoria = categoria,
                isSelected = categoria == selectedCategory,
                onClick = { onCategorySelected(categoria) }
            )
        }
    }
}

@Composable
fun CategoryChip(
    categoria: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = when (categoria) {
                        "Uso General" -> Icons.Default.Build
                        "Autos o Vehículos Ligeros" -> Icons.Default.DirectionsCar
                        "Motocicletas" -> Icons.Default.TwoWheeler
                        "Vehículos Pesados" -> Icons.Default.LocalShipping
                        else -> Icons.Default.Category
                    },
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = categoria,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1
                )
            }
        },
        leadingIcon = if (isSelected) {
            {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Seleccionado",
                    modifier = Modifier.size(18.dp)
                )
            }
        } else null
    )
}

@Composable
fun ProductoFilteredCard(
    producto: Producto,
    onProductoClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onProductoClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProductImage(
                imageUrl = producto.productoImagenUrl,
                contentDescription = producto.productoNombre,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop,
                placeholderSize = 48.dp
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = producto.productoNombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = producto.categoria,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "$${String.format(Locale.US, "%,d", producto.productoMonto)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Inventory,
                        contentDescription = "Stock",
                        modifier = Modifier.size(16.dp),
                        tint = if (producto.productoCantidad > 10)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = "${producto.productoCantidad} disponibles",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun PriceFilterDialog(
    minPrice: Int,
    maxPrice: Int,
    onMinPriceChange: (Int) -> Unit,
    onMaxPriceChange: (Int) -> Unit,
    onDismiss: () -> Unit,
    onApply: () -> Unit
) {
    var tempMinPrice by remember { mutableStateOf(minPrice.toFloat()) }
    var tempMaxPrice by remember { mutableStateOf(maxPrice.toFloat()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filtrar por Precio") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column {
                    Text(
                        text = "Precio mínimo: $${tempMinPrice.toInt()}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Slider(
                        value = tempMinPrice,
                        onValueChange = {
                            tempMinPrice = it.coerceAtLeast(100f)
                            if (tempMinPrice > tempMaxPrice) {
                                tempMaxPrice = tempMinPrice
                            }
                        },
                        valueRange = 100f..1000000f,
                        steps = 99,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                Column {
                    Text(
                        text = "Precio máximo: $${tempMaxPrice.toInt()}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Slider(
                        value = tempMaxPrice,
                        onValueChange = {
                            tempMaxPrice = it
                            if (tempMaxPrice < tempMinPrice) {
                                tempMinPrice = tempMaxPrice.coerceAtLeast(100f)
                            }
                        },
                        valueRange = 100f..1000000f,
                        steps = 99,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Rango seleccionado:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "$${tempMinPrice.toInt()} - $${tempMaxPrice.toInt()}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onMinPriceChange(tempMinPrice.toInt())
                onMaxPriceChange(tempMaxPrice.toInt())
                onApply()
            }) {
                Text("Aplicar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}