package com.example.data.api

import com.example.data.model.DifficultyLevel
import com.example.data.model.IngredientItem
import com.example.data.model.Recipe
import com.example.data.model.toRecipe
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object MealApiClient {
    private const val BASE_URL = "https://www.themealdb.com/api/json/v1/1/"

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    val apiService: MealApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(MealApiService::class.java)
    }

    suspend fun fetchRandomMealFromApi(): Recipe? {
        return try {
            val response = apiService.getRandomMeal()
            if (response.isSuccessful) {
                val mealDto = response.body()?.meals?.firstOrNull()
                mealDto?.toRecipe()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun fetchMealDetailsFromApi(id: String): Recipe? {
        return try {
            val response = apiService.getMealById(id)
            if (response.isSuccessful) {
                val mealDto = response.body()?.meals?.firstOrNull()
                mealDto?.toRecipe()
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getFallbackRecipes(): List<Recipe> {
        return listOf(
            Recipe(
                id = "52772",
                title = "Teriyaki Somon Bowl",
                imageUrl = "https://www.themealdb.com/images/media/meals/g046vh1509964005.jpg",
                category = "Seafood",
                area = "Japon",
                instructions = "1. Somon filetolarını soya sosu, bal ve susam yağı marinesi ile marine edin.\n2. Tavada her iki tarafını 4'er dakika mühürleyin.\n3. Haşlanmış pirinç üzerine avokado, edamame ve somonu yerleştirip teriyaki sos gezdirin.",
                ingredients = listOf(
                    IngredientItem("Somon Fileto", "200g"),
                    IngredientItem("Soya Sosu", "2 yemek kaşığı"),
                    IngredientItem("Jasmin Pirinç", "1 su bardağı"),
                    IngredientItem("Avokado", "1/2 adet"),
                    IngredientItem("Susam & Çörekotu", "1 tatlı kaşığı")
                ),
                prepTimeMinutes = 20,
                calories = 480,
                difficulty = DifficultyLevel.EASY,
                isNutFree = true,
                isDairyFree = true,
                isVegetarian = false,
                isVegan = false,
                isGlutenFree = true,
                youtubeUrl = "https://www.youtube.com/watch?v=4sp46c3q_A0"
            ),
            Recipe(
                id = "52771",
                title = "Geleneksel Mercimek Çorbası",
                imageUrl = "https://www.themealdb.com/images/media/meals/58o10a1564916566.jpg",
                category = "Breakfast",
                area = "Türk",
                instructions = "1. Kuru soğan ve havuçları zeytinyağında soteleyin.\n2. Yıkanmış kırmızı mercimeği ekleyip karıştırın.\n3. Sıcak su veya et suyu ekleyip mercimekler yumuşayana kadar pişirin.\n4. El blenderı ile pürüzsüz kıvama getirin. Üzerine tereyağlı pul biber eritin.",
                ingredients = listOf(
                    IngredientItem("Kırmızı Mercimek", "1.5 su bardağı"),
                    IngredientItem("Kuru Soğan", "1 adet"),
                    IngredientItem("Havuç", "1 adet"),
                    IngredientItem("Zeytinyağı & Pul Biber", "2 yemek kaşığı"),
                    IngredientItem("Limon", "Servis için")
                ),
                prepTimeMinutes = 25,
                calories = 240,
                difficulty = DifficultyLevel.EASY,
                isNutFree = true,
                isDairyFree = true,
                isVegetarian = true,
                isVegan = true,
                isGlutenFree = true
            ),
            Recipe(
                id = "52844",
                title = "Karnıyarık (Kıymalı Patlıcan)",
                imageUrl = "https://www.themealdb.com/images/media/meals/424j2f1681283626.jpg",
                category = "Beef",
                area = "Türk",
                instructions = "1. Patlıcanları alacalı soyup zeytinyağında kızartın veya fırınlayın.\n2. Kıymayı soğan, sarımsak, domates ve biberle kavurun.\n3. Patlıcanların ortasını yararak hazırladığınız harçla doldurun.\n4. Salçalı sos ekleyip 200 derece fırında 25 dakika pişirin.",
                ingredients = listOf(
                    IngredientItem("Kemer Patlıcan", "4 adet"),
                    IngredientItem("Orta Yağlı Kıurma", "300g"),
                    IngredientItem("Domates & Biber", "2'şer adet"),
                    IngredientItem("Sarımsak & Soğan", "3 diş / 1 adet"),
                    IngredientItem("Domates Salçası", "1 yemek kaşığı")
                ),
                prepTimeMinutes = 45,
                calories = 380,
                difficulty = DifficultyLevel.MEDIUM,
                isNutFree = true,
                isDairyFree = true,
                isVegetarian = false,
                isVegan = false,
                isGlutenFree = true
            ),
            Recipe(
                id = "52959",
                title = "Kremalı Mantarlı Fesleğenli Pasta",
                imageUrl = "https://www.themealdb.com/images/media/meals/wvpsxx1468256321.jpg",
                category = "Pasta",
                area = "İtalyan",
                instructions = "1. Makarnayı bol tuzlu suda al dente haşlayın.\n2. İnce dilimlenmiş kültür mantarlarını zeytinyağı ve sarımsakta soteleyin.\n3. Sıvı krema ve taze fesleğen ekleyip kıvam aldırın.\n4. Makarnayı sosla birleştirip parmesan rendesi serpin.",
                ingredients = listOf(
                    IngredientItem("Penne / Rigatoni Makarna", "350g"),
                    IngredientItem("Kültür Mantarı", "250g"),
                    IngredientItem("Sıvı Krema", "200ml"),
                    IngredientItem("Taze Fesleğen & Sarımsak", "1 demet"),
                    IngredientItem("Parmesan Peyniri", "50g")
                ),
                prepTimeMinutes = 15,
                calories = 520,
                difficulty = DifficultyLevel.EASY,
                isNutFree = true,
                isDairyFree = false,
                isVegetarian = true,
                isVegan = false,
                isGlutenFree = false
            ),
            Recipe(
                id = "52855",
                title = "Meksika Usulü Tavuklu Taco",
                imageUrl = "https://www.themealdb.com/images/media/meals/uvuyxu1503067369.jpg",
                category = "Chicken",
                area = "Meksika",
                instructions = "1. Tavuk göğsünü kimyon, paprika ve sarımsak tozu ile marine edip tavada pişirin.\n2. Mısır tortillalarını tavada ısıtın.\n3. İçerisine tavuk dilimleri, guacamole, Meksika fasulyesi ve pikante salsa ekleyin.",
                ingredients = listOf(
                    IngredientItem("Tavuk Göğsü", "300g"),
                    IngredientItem("Mısır Tortilla", "6 adet"),
                    IngredientItem("Meksika Fasulyesi", "1/2 kutu"),
                    IngredientItem("Avokado Guacamole", "3 yemek kaşığı"),
                    IngredientItem("Meksika Baharat Karışımı", "1 tatlı kaşığı")
                ),
                prepTimeMinutes = 25,
                calories = 430,
                difficulty = DifficultyLevel.MEDIUM,
                isNutFree = true,
                isDairyFree = true,
                isVegetarian = false,
                isVegan = false,
                isGlutenFree = true
            ),
            Recipe(
                id = "52893",
                title = "Izgara Tavuklu Sezar Wrap",
                imageUrl = "https://www.themealdb.com/images/media/meals/1548772880.jpg",
                category = "Chicken",
                area = "Amerikan",
                instructions = "1. Tavuk göğsünü baharatlayıp döküm tavada ızgaralayın.\n2. Lavaş içerisine kıvırcık marul, ızgara tavuk dilimleri ve kruton ekleyin.\n3. Sezar sos gezdirip sıkıca sarın ve tost makinesinde ısıtın.",
                ingredients = listOf(
                    IngredientItem("Tavuk Göğsü", "250g"),
                    IngredientItem("Tam Buğday Lavaş", "2 adet"),
                    IngredientItem("Sezar Sos", "2 yemek kaşığı"),
                    IngredientItem("Romaine Marul", "4 yaprak"),
                    IngredientItem("Rendelenmiş Kaşar", "30g")
                ),
                prepTimeMinutes = 20,
                calories = 410,
                difficulty = DifficultyLevel.EASY,
                isNutFree = true,
                isDairyFree = false,
                isVegetarian = false,
                isVegan = false,
                isGlutenFree = false
            ),
            Recipe(
                id = "52857",
                title = "Çin Usulü Sebzeli Noodle",
                imageUrl = "https://www.themealdb.com/images/media/meals/1529446352.jpg",
                category = "Pasta",
                area = "Asya",
                instructions = "1. Noodle'ları 3 dakika sıcak suda haşlayın.\n2. Wok tavada susam yağında jülyen doğranmış havuç, kabak, renkli biberler ve brokoli soteleyin.\n3. Soya sosu ve zencefil ekleyip noodle ile harmanlayın. Üzerine kavrulmuş fıstık serperek servis edin.",
                ingredients = listOf(
                    IngredientItem("Yumurta / Pirinç Noodle", "200g"),
                    IngredientItem("Havuç & Renkli Biber", "2 adet"),
                    IngredientItem("Soya Sosu & Zencefil", "2 yemek kaşığı"),
                    IngredientItem("Susam Yağı", "1 yemek kaşığı"),
                    IngredientItem("Kavrulmuş Fıstık", "2 yemek kaşığı")
                ),
                prepTimeMinutes = 18,
                calories = 360,
                difficulty = DifficultyLevel.EASY,
                isNutFree = false, // Contains peanuts
                isDairyFree = true,
                isVegetarian = true,
                isVegan = true,
                isGlutenFree = false
            ),
            Recipe(
                id = "52768",
                title = "Meyveli Acai & Yoğurt Bowl",
                imageUrl = "https://www.themealdb.com/images/media/meals/adxcvx1682248998.jpg",
                category = "Dessert",
                area = "Brezilya",
                instructions = "1. Süzme yoğurt, donmuş çilek ve balı blenderdan geçirin.\n2. Kaseye aktarıp üzerine granola, dilimlenmiş muz ve yaban mersini dizin.\n3. Chia tohumu ve fıstık ezmesi gezdirerek servis edin.",
                ingredients = listOf(
                    IngredientItem("Süzme Yoğurt", "200g"),
                    IngredientItem("Donmuş Çilek & Muz", "1 su bardağı"),
                    IngredientItem("Ev Yapımı Granola", "3 yemek kaşığı"),
                    IngredientItem("Chia Tohumu", "1 tatlı kaşığı"),
                    IngredientItem("Fıstık Ezmesi", "1 yemek kaşığı")
                ),
                prepTimeMinutes = 10,
                calories = 290,
                difficulty = DifficultyLevel.EASY,
                isNutFree = false, // Contains peanut butter
                isDairyFree = false, // Contains yogurt
                isVegetarian = true,
                isVegan = false,
                isGlutenFree = true
            )
        )
    }
}
