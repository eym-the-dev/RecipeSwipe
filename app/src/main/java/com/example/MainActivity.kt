package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Kitchen
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.FridgeScannerScreen
import com.example.ui.screens.LikedRecipesScreen
import com.example.ui.screens.ProfileAndFriendsScreen
import com.example.ui.screens.SwipeScreen
import com.example.ui.theme.RecipeSwipeTheme
import com.example.ui.viewmodel.RecipeViewModel

sealed class BottomNavItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val tag: String
) {
    object Keşfet : BottomNavItem("Keşfet", Icons.Filled.Restaurant, Icons.Outlined.Restaurant, "nav_discover")
    object Dolabım : BottomNavItem("Dolabım AI", Icons.Filled.Kitchen, Icons.Outlined.Kitchen, "nav_fridge")
    object Beğenilenler : BottomNavItem("Beğenilenler", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder, "nav_liked")
    object Profil : BottomNavItem("Profil & Sosyal", Icons.Filled.People, Icons.Outlined.People, "nav_profile")
}

class MainActivity : ComponentActivity() {

    private val viewModel: RecipeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            RecipeSwipeTheme {
                var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
                val likedList by viewModel.likedRecipes.collectAsState()

                val navItems = listOf(
                    BottomNavItem.Keşfet,
                    BottomNavItem.Dolabım,
                    BottomNavItem.Beğenilenler,
                    BottomNavItem.Profil
                )

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            tonalElevation = 0.dp
                        ) {
                            navItems.forEachIndexed { index, item ->
                                val isSelected = selectedTabIndex == index
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = { selectedTabIndex = index },
                                    label = {
                                        Text(
                                            text = item.title,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 11.sp
                                        )
                                    },
                                    icon = {
                                        if (item == BottomNavItem.Beğenilenler && likedList.isNotEmpty()) {
                                            BadgedBox(
                                                badge = {
                                                    Badge(containerColor = MaterialTheme.colorScheme.primary) {
                                                        Text("${likedList.size}")
                                                    }
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                                    contentDescription = item.title
                                                )
                                            }
                                        } else {
                                            Icon(
                                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                                contentDescription = item.title
                                            )
                                        }
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.primary,
                                        selectedTextColor = MaterialTheme.colorScheme.primary,
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                    ),
                                    modifier = Modifier.testTag(item.tag)
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (selectedTabIndex) {
                            0 -> SwipeScreen(viewModel = viewModel)
                            1 -> FridgeScannerScreen(
                                viewModel = viewModel,
                                onNavigateToDeck = { selectedTabIndex = 0 }
                            )
                            2 -> LikedRecipesScreen(viewModel = viewModel)
                            3 -> ProfileAndFriendsScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}
