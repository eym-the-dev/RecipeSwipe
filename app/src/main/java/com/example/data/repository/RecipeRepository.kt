package com.example.data.repository

import com.example.data.api.MealApiClient
import com.example.data.api.SupabaseApiClient
import com.example.data.db.LikedRecipeDao
import com.example.data.db.LikedRecipeEntity
import com.example.data.model.DifficultyLevel
import com.example.data.model.IngredientItem
import com.example.data.model.Recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class RecipeRepository(
    private val likedRecipeDao: LikedRecipeDao
) {
    val likedRecipesFlow: Flow<List<Recipe>> = likedRecipeDao.getAllLikedRecipes().map { entities ->
        entities.map { it.toRecipe() }
    }

    fun searchLikedRecipes(query: String): Flow<List<Recipe>> {
        return likedRecipeDao.searchLikedRecipes(query).map { entities ->
            entities.map { it.toRecipe() }
        }
    }

    suspend fun getInitialDeckRecipes(): List<Recipe> = withContext(Dispatchers.IO) {
        val fetchedList = mutableListOf<Recipe>()
        
        // Try fetching recipes from Supabase Edge Function first
        val supabaseMeals = try {
            SupabaseApiClient.fetchMealsFromSupabase()
        } catch (e: Exception) {
            emptyList()
        }

        if (supabaseMeals.isNotEmpty()) {
            fetchedList.addAll(supabaseMeals)
        } else {
            // Fallback: fetch random meals from API
            repeat(3) {
                val apiMeal = MealApiClient.fetchRandomMealFromApi()
                if (apiMeal != null) {
                    fetchedList.add(apiMeal)
                }
            }
        }

        // Combine with fallback curated recipes
        val fallbacks = MealApiClient.getFallbackRecipes()
        val combined = (fetchedList + fallbacks).distinctBy { it.id }
        
        // Check liked status in DB
        combined.map { recipe ->
            val isLiked = likedRecipeDao.isRecipeLiked(recipe.id)
            recipe.copy(isLiked = isLiked)
        }
    }

    suspend fun fetchMoreRandomRecipe(): Recipe = withContext(Dispatchers.IO) {
        val supabaseMeals = try {
            SupabaseApiClient.fetchMealsFromSupabase()
        } catch (e: Exception) {
            emptyList()
        }

        val recipe = supabaseMeals.randomOrNull()
            ?: MealApiClient.fetchRandomMealFromApi()
            ?: MealApiClient.getFallbackRecipes().random()

        val isLiked = likedRecipeDao.isRecipeLiked(recipe.id)
        recipe.copy(isLiked = isLiked)
    }

    suspend fun analyzeFridgeImageWithSupabase(base64Image: String): List<String> = withContext(Dispatchers.IO) {
        SupabaseApiClient.analyzeFridgePhoto(base64Image)
    }

    suspend fun saveLikedRecipe(recipe: Recipe) = withContext(Dispatchers.IO) {
        val ingredientsJsonStr = recipe.ingredients.joinToString("||") { "${it.name}::${it.measure}" }
        val entity = LikedRecipeEntity(
            id = recipe.id,
            title = recipe.title,
            imageUrl = recipe.imageUrl,
            category = recipe.category,
            area = recipe.area,
            instructions = recipe.instructions,
            ingredientsJson = ingredientsJsonStr,
            prepTimeMinutes = recipe.prepTimeMinutes,
            calories = recipe.calories,
            difficultyName = recipe.difficulty.name,
            isNutFree = recipe.isNutFree,
            isDairyFree = recipe.isDairyFree,
            isVegetarian = recipe.isVegetarian,
            isVegan = recipe.isVegan,
            isGlutenFree = recipe.isGlutenFree,
            youtubeUrl = recipe.youtubeUrl
        )
        likedRecipeDao.insertLikedRecipe(entity)
    }

    suspend fun removeLikedRecipe(recipeId: String) = withContext(Dispatchers.IO) {
        likedRecipeDao.deleteLikedRecipeById(recipeId)
    }

    suspend fun clearAllLiked() = withContext(Dispatchers.IO) {
        likedRecipeDao.deleteAllLikedRecipes()
    }

    private fun LikedRecipeEntity.toRecipe(): Recipe {
        val ingredientsList = ingredientsJson.split("||").mapNotNull { itemStr ->
            val parts = itemStr.split("::")
            if (parts.size >= 2) {
                IngredientItem(name = parts[0], measure = parts[1])
            } else if (itemStr.isNotBlank()) {
                IngredientItem(name = itemStr, measure = "İsteğe Göre")
            } else null
        }

        val diffLevel = try {
            DifficultyLevel.valueOf(difficultyName)
        } catch (e: Exception) {
            DifficultyLevel.EASY
        }

        return Recipe(
            id = id,
            title = title,
            imageUrl = imageUrl,
            category = category,
            area = area,
            instructions = instructions,
            ingredients = if (ingredientsList.isNotEmpty()) ingredientsList else listOf(IngredientItem("Taze Malzemeler", "İsteğe Göre")),
            prepTimeMinutes = prepTimeMinutes,
            calories = calories,
            difficulty = diffLevel,
            isNutFree = isNutFree,
            isDairyFree = isDairyFree,
            isVegetarian = isVegetarian,
            isVegan = isVegan,
            isGlutenFree = isGlutenFree,
            youtubeUrl = youtubeUrl,
            isLiked = true
        )
    }
}
