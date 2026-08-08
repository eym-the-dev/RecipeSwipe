package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FilterBar
import com.example.ui.components.RecipeCard
import com.example.ui.components.RecipeDetailModal
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.LikeGreen
import com.example.ui.theme.PassRed
import com.example.ui.viewmodel.RecipeViewModel

@Composable
fun SwipeScreen(
    viewModel: RecipeViewModel,
    modifier: Modifier = Modifier
) {
    val displayedDeck by viewModel.displayedDeck.collectAsState()
    val filterState by viewModel.filterState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedDetailRecipe by viewModel.selectedRecipeForDetail.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = FlameOrange,
                    shape = CircleShape,
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Restaurant,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "RecipeSwipe",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 22.sp,
                            color = FlameOrange
                        )
                    )
                    Text(
                        text = "Ne Pişirsem? Kaydır ve Keşfet!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }

            IconButton(
                onClick = { viewModel.loadDeck() },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .testTag("reload_deck_header_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Yenile",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Filter Bar (Categories, Cuisines & Preferences)
        FilterBar(
            filterState = filterState,
            onCategorySelected = { viewModel.setCategoryFilter(it) },
            onCuisineSelected = { viewModel.setCuisineFilter(it) },
            onDifficultySelected = { viewModel.setDifficultyFilter(it) },
            onPrepTimeSelected = { viewModel.setMaxPrepTimeFilter(it) },
            onToggleNutFree = { viewModel.toggleNutFreeFilter() },
            onToggleDairyFree = { viewModel.toggleDairyFreeFilter() },
            onToggleVegetarian = { viewModel.toggleVegetarianFilter() },
            onToggleVegan = { viewModel.toggleVeganFilter() },
            onToggleGlutenFree = { viewModel.toggleGlutenFreeFilter() },
            onResetFilters = { viewModel.resetFilters() }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Main Card Stack Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = FlameOrange)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Nefis Tarifler Yükleniyor...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else if (displayedDeck.isEmpty()) {
                // Empty Deck State
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = CircleShape,
                        modifier = Modifier.size(80.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("🍳", fontSize = 40.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Kart Kalmadı!",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Seçilen filtrelere uygun yeni tarif kalmadı veya hepsini incelediniz.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { viewModel.resetFilters(); viewModel.loadDeck() },
                        colors = ButtonDefaults.buttonColors(containerColor = FlameOrange),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.testTag("reset_filters_button")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Filtreleri Sıfırla ve Yenile", fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                // Render Stack (Top card on front, 2nd card preview underneath)
                val topIndex = displayedDeck.size - 1
                
                // Second card preview
                if (topIndex > 0) {
                    val nextRecipe = displayedDeck[topIndex - 1]
                    RecipeCard(
                        recipe = nextRecipe,
                        isTopCard = false,
                        onSwipeRight = {},
                        onSwipeLeft = {},
                        onInfoClick = {},
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }

                // Top Draggable Card
                val topRecipe = displayedDeck[topIndex]
                RecipeCard(
                    recipe = topRecipe,
                    isTopCard = true,
                    onSwipeRight = { viewModel.onSwipeRight(topRecipe) },
                    onSwipeLeft = { viewModel.onSwipeLeft(topRecipe) },
                    onInfoClick = { viewModel.openDetailModal(topRecipe) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        val haptic = LocalHapticFeedback.current
        // Sleek Floating Control Action Buttons (Dislike, Like, Bookmark/Info)
        if (displayedDeck.isNotEmpty()) {
            val topRecipe = displayedDeck.last()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp, top = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // DISLIKE BUTTON (PASS) - White with Sleek Berry Border
                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.onSwipeLeft(topRecipe)
                    },
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape)
                        .testTag("action_dislike_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Pas Geç",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(24.dp))

                // LIKE BUTTON (SAVE) - Sleek Berry Filled Hero Button
                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.onSwipeRight(topRecipe)
                    },
                    modifier = Modifier
                        .size(76.dp)
                        .shadow(12.dp, CircleShape, ambientColor = MaterialTheme.colorScheme.primary, spotColor = MaterialTheme.colorScheme.primary)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .testTag("action_like_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Beğen & Kaydet",
                        tint = Color.White,
                        modifier = Modifier.size(34.dp)
                    )
                }

                Spacer(modifier = Modifier.width(24.dp))

                // INFO BUTTON - White with Muted Icon
                IconButton(
                    onClick = { viewModel.openDetailModal(topRecipe) },
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape)
                        .testTag("action_info_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Detay",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }

    // Modal Detail Sheet
    selectedDetailRecipe?.let { recipe ->
        RecipeDetailModal(
            recipe = recipe,
            onDismiss = { viewModel.closeDetailModal() },
            onSaveToggle = {
                viewModel.onSwipeRight(it)
                viewModel.closeDetailModal()
            }
        )
    }
}
