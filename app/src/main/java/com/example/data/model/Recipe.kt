package com.example.data.model

enum class DifficultyLevel(val label: String, val badgeText: String) {
    EASY("Kolay", "🟢 Kolay"),
    MEDIUM("Orta", "🟡 Orta"),
    HARD("Zor", "🔴 Zor")
}

data class IngredientItem(
    val name: String,
    val measure: String,
    var isChecked: Boolean = false
)

data class Recipe(
    val id: String,
    val title: String,
    val imageUrl: String,
    val category: String,
    val area: String,
    val instructions: String,
    val ingredients: List<IngredientItem>,
    val prepTimeMinutes: Int = 20,
    val calories: Int = 350,
    val difficulty: DifficultyLevel = DifficultyLevel.EASY,
    val isNutFree: Boolean = true,
    val isDairyFree: Boolean = true,
    val isVegetarian: Boolean = false,
    val isVegan: Boolean = false,
    val isGlutenFree: Boolean = false,
    val youtubeUrl: String? = null,
    val isLiked: Boolean = false
)

