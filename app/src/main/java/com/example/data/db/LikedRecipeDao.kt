package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LikedRecipeDao {
    @Query("SELECT * FROM liked_recipes ORDER BY likedAt DESC")
    fun getAllLikedRecipes(): Flow<List<LikedRecipeEntity>>

    @Query("SELECT * FROM liked_recipes WHERE title LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' ORDER BY likedAt DESC")
    fun searchLikedRecipes(query: String): Flow<List<LikedRecipeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLikedRecipe(recipe: LikedRecipeEntity)

    @Query("DELETE FROM liked_recipes WHERE id = :id")
    suspend fun deleteLikedRecipeById(id: String)

    @Query("DELETE FROM liked_recipes")
    suspend fun deleteAllLikedRecipes()

    @Query("SELECT EXISTS(SELECT 1 FROM liked_recipes WHERE id = :id)")
    suspend fun isRecipeLiked(id: String): Boolean
}
