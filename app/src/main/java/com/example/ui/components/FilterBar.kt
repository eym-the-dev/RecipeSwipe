package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DifficultyLevel
import com.example.ui.viewmodel.CuisineFilter
import com.example.ui.viewmodel.FilterState
import com.example.ui.viewmodel.MealCategoryFilter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterBar(
    filterState: FilterState,
    onCategorySelected: (MealCategoryFilter) -> Unit,
    onCuisineSelected: (CuisineFilter) -> Unit,
    onDifficultySelected: (DifficultyLevel?) -> Unit,
    onPrepTimeSelected: (Int) -> Unit,
    onToggleNutFree: () -> Unit,
    onToggleDairyFree: () -> Unit,
    onToggleVegetarian: () -> Unit,
    onToggleVegan: () -> Unit,
    onToggleGlutenFree: () -> Unit,
    onResetFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showFilterSheet by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        // Quick Access Filter Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Detailed Filter Modal Trigger Button
            Surface(
                onClick = { showFilterSheet = true },
                shape = CircleShape,
                color = if (filterState.activeFilterCount > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                contentColor = if (filterState.activeFilterCount > 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.testTag("open_filter_sheet_btn")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filtreler",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Filtrele",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    if (filterState.activeFilterCount > 0) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${filterState.activeFilterCount}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Cuisine Quick Chips Horizontal Scroll
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CuisineFilter.entries.forEach { cuisine ->
                    val isSelected = filterState.selectedCuisine == cuisine
                    FilterChip(
                        selected = isSelected,
                        onClick = { onCuisineSelected(cuisine) },
                        label = {
                            Text(
                                text = cuisine.displayName,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSecondary,
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = Color.Transparent,
                            selectedBorderColor = Color.Transparent
                        ),
                        shape = CircleShape,
                        modifier = Modifier.testTag("cuisine_chip_${cuisine.name.lowercase()}")
                    )
                }
            }
        }

        // Active Quick Toggles Row (Nut-free, Difficulty, Prep Time)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Difficulty Chips
            DifficultyLevel.entries.forEach { diff ->
                val isSelected = filterState.selectedDifficulty == diff
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        onDifficultySelected(if (isSelected) null else diff)
                    },
                    label = { Text(diff.badgeText, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        labelColor = MaterialTheme.colorScheme.onSecondary,
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = Color.Transparent,
                        selectedBorderColor = Color.Transparent
                    ),
                    shape = CircleShape,
                    modifier = Modifier.testTag("diff_chip_${diff.name.lowercase()}")
                )
            }

            // Nut Free Toggle
            FilterChip(
                selected = filterState.isNutFreeOnly,
                onClick = onToggleNutFree,
                label = { Text("🥜 Kuruyemişsiz", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = MaterialTheme.colorScheme.onSecondary,
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = Color.White
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = filterState.isNutFreeOnly,
                    borderColor = Color.Transparent,
                    selectedBorderColor = Color.Transparent
                ),
                shape = CircleShape,
                modifier = Modifier.testTag("nut_free_toggle")
            )

            // Dairy Free Toggle
            FilterChip(
                selected = filterState.isDairyFreeOnly,
                onClick = onToggleDairyFree,
                label = { Text("🥛 Süt Ürünlerisiz", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = MaterialTheme.colorScheme.onSecondary,
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = Color.White
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = filterState.isDairyFreeOnly,
                    borderColor = Color.Transparent,
                    selectedBorderColor = Color.Transparent
                ),
                shape = CircleShape,
                modifier = Modifier.testTag("dairy_free_toggle")
            )

            if (filterState.activeFilterCount > 0) {
                Surface(
                    onClick = onResetFilters,
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Sıfırla", modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Temizle", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Modal Bottom Sheet for Comprehensive Filters
    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 32.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Detaylı Tarif Filtreleri",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { showFilterSheet = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Kapat")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 1: Mutfak Türü (Cuisine)
                Text(
                    text = "🌍 Mutfak Türü",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CuisineFilter.entries.forEach { cuisine ->
                        val isSelected = filterState.selectedCuisine == cuisine
                        FilterChip(
                            selected = isSelected,
                            onClick = { onCuisineSelected(cuisine) },
                            label = { Text(cuisine.displayName, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            ),
                            shape = CircleShape
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Section 2: Zorluk Seviyesi (Difficulty)
                Text(
                    text = "⚖️ Zorluk Seviyesi",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = filterState.selectedDifficulty == null,
                        onClick = { onDifficultySelected(null) },
                        label = { Text("Tümü", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White
                        ),
                        shape = CircleShape
                    )
                    DifficultyLevel.entries.forEach { diff ->
                        val isSelected = filterState.selectedDifficulty == diff
                        FilterChip(
                            selected = isSelected,
                            onClick = { onDifficultySelected(if (isSelected) null else diff) },
                            label = { Text(diff.badgeText, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            ),
                            shape = CircleShape
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Section 3: Alerjenler & Diyet Özellikleri
                Text(
                    text = "🛡️ Alerjenler & Beslenme Tipi",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = filterState.isNutFreeOnly,
                        onClick = onToggleNutFree,
                        label = { Text("🥜 Kuruyemişsiz (Nut-Free)", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White
                        ),
                        shape = CircleShape
                    )
                    FilterChip(
                        selected = filterState.isDairyFreeOnly,
                        onClick = onToggleDairyFree,
                        label = { Text("🥛 Süt Ürünlerisiz (Lactose-Free)", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White
                        ),
                        shape = CircleShape
                    )
                    FilterChip(
                        selected = filterState.isVegetarianOnly,
                        onClick = onToggleVegetarian,
                        label = { Text("🌱 Vejetaryen", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White
                        ),
                        shape = CircleShape
                    )
                    FilterChip(
                        selected = filterState.isVeganOnly,
                        onClick = onToggleVegan,
                        label = { Text("🌿 Vegan", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White
                        ),
                        shape = CircleShape
                    )
                    FilterChip(
                        selected = filterState.isGlutenFreeOnly,
                        onClick = onToggleGlutenFree,
                        label = { Text("🌾 Glutensiz", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White
                        ),
                        shape = CircleShape
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Section 4: Max Hazırlama Süresi
                Text(
                    text = "⏱ Max Hazırlama Süresi",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val timeOptions = listOf(0 to "Tümü", 15 to "≤ 15 dk", 30 to "≤ 30 dk", 45 to "≤ 45 dk")
                    timeOptions.forEach { (minutes, label) ->
                        val isSelected = filterState.maxPrepTimeMinutes == minutes
                        FilterChip(
                            selected = isSelected,
                            onClick = { onPrepTimeSelected(minutes) },
                            label = { Text(label, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            ),
                            shape = CircleShape
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Bottom Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            onResetFilters()
                        },
                        modifier = Modifier.weight(1f),
                        shape = CircleShape
                    ) {
                        Text("Sıfırla")
                    }

                    Button(
                        onClick = { showFilterSheet = false },
                        modifier = Modifier.weight(2f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = CircleShape
                    ) {
                        Text("Sonuçları Göster", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

