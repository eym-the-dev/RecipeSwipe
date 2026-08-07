package com.example.data.api

import com.example.data.model.MealListDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MealApiService {
    @GET("random.php")
    suspend fun getRandomMeal(): Response<MealListDto>

    @GET("filter.php")
    suspend fun getMealsByCategory(
        @Query("c") category: String
    ): Response<MealListDto>

    @GET("lookup.php")
    suspend fun getMealById(
        @Query("i") id: String
    ): Response<MealListDto>
}
