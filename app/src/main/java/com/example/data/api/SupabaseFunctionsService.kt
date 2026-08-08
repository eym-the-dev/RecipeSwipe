package com.example.data.api

import com.example.data.model.Recipe
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

// --- Supabase Edge Functions DTO Models ---

data class AnalyzeFridgeRequest(
    @Json(name = "imageBase64") val imageBase64: String,
    @Json(name = "mimeType") val mimeType: String = "image/jpeg"
)

data class AnalyzeFridgeResponse(
    @Json(name = "ingredients") val ingredients: List<String> = emptyList(),
    @Json(name = "error") val error: String? = null
)

data class FetchMealsRequest(
    @Json(name = "category") val category: String? = null,
    @Json(name = "ingredient") val ingredient: String? = null,
    @Json(name = "query") val query: String? = null
)

data class FetchMealsResponse(
    @Json(name = "meals") val meals: List<Recipe> = emptyList(),
    @Json(name = "error") val error: String? = null
)

// --- Retrofit Service Interface ---

interface SupabaseFunctionsApi {
    @POST("functions/v1/analyze-fridge")
    suspend fun analyzeFridge(
        @Body request: AnalyzeFridgeRequest,
        @Header("apikey") apiKey: String = SupabaseApiClient.SUPABASE_ANON_KEY
    ): Response<AnalyzeFridgeResponse>

    @POST("functions/v1/fetch-meals")
    suspend fun fetchMeals(
        @Body request: FetchMealsRequest,
        @Header("apikey") apiKey: String = SupabaseApiClient.SUPABASE_ANON_KEY
    ): Response<FetchMealsResponse>
}

// --- Supabase Retrofit Singleton Client ---

object SupabaseApiClient {
    // Configurable Supabase Edge Functions base URL & Anon Key
    var SUPABASE_URL: String = "https://m6addckpyzsbbqlhqsij6l.supabase.co/"
    var SUPABASE_ANON_KEY: String = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.dummy_anon_key"

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    val apiService: SupabaseFunctionsApi by lazy {
        Retrofit.Builder()
            .baseUrl(if (SUPABASE_URL.endsWith("/")) SUPABASE_URL else "$SUPABASE_URL/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(SupabaseFunctionsApi::class.java)
    }

    suspend fun analyzeFridgePhoto(base64Image: String): List<String> {
        return try {
            val response = apiService.analyzeFridge(AnalyzeFridgeRequest(imageBase64 = base64Image))
            if (response.isSuccessful) {
                response.body()?.ingredients ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun fetchMealsFromSupabase(category: String? = null, ingredient: String? = null, query: String? = null): List<Recipe> {
        return try {
            val response = apiService.fetchMeals(FetchMealsRequest(category = category, ingredient = ingredient, query = query))
            if (response.isSuccessful) {
                response.body()?.meals ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
