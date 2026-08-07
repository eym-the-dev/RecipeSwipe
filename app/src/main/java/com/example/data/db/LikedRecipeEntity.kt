package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "liked_recipes")
data class LikedRecipeEntity(
    @PrimaryKey val id: String,
    val title: String,
    val imageUrl: String,
    val category: String,
    val area: String,
    val instructions: String,
    val ingredientsJson: String, // Pipe-separated or json string
    val prepTimeMinutes: Int,
    val calories: Int,
    val difficultyName: String = "EASY",
    val isNutFree: Boolean = true,
    val isDairyFree: Boolean = true,
    val isVegetarian: Boolean = false,
    val isVegan: Boolean = false,
    val isGlutenFree: Boolean = false,
    val youtubeUrl: String?,
    val likedAt: Long = System.currentTimeMillis()
)
