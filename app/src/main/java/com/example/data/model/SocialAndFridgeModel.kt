package com.example.data.model

data class UserProfile(
    val id: String = "",
    val email: String = "",
    val displayName: String = "",
    val avatarEmoji: String = "👨‍🍳",
    val friendCode: String = "",
    val bio: String = "",
    val isLoggedIn: Boolean = false
)

data class RegisteredAccount(
    val email: String,
    val passwordHash: String,
    val profile: UserProfile
)

data class FriendUser(
    val id: String,
    val displayName: String,
    val avatarEmoji: String,
    val friendCode: String,
    val favoriteCuisine: String,
    val sharedRecipeCount: Int,
    val isOnline: Boolean = true
)

data class SharedRecipeItem(
    val id: String,
    val recipeId: String,
    val recipeTitle: String,
    val recipeImageUrl: String,
    val fromFriendName: String,
    val note: String,
    val timestamp: String = "Bugün"
)

data class FridgeScanResult(
    val detectedIngredients: List<String>,
    val analysisSummary: String,
    val recipeSuggestionsCount: Int
)
