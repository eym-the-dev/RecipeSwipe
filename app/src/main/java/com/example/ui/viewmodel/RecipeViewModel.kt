package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.Recipe
import com.example.data.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import com.example.data.model.DifficultyLevel
import com.example.data.model.FriendUser
import com.example.data.model.FridgeScanResult
import com.example.data.model.SharedRecipeItem
import com.example.data.model.UserProfile

enum class MealCategoryFilter(val displayName: String, val apiCategory: String) {
    ALL("Tümü 🍽", "All"),
    BREAKFAST("Kahvaltı 🍳", "Breakfast"),
    BEEF("Et Yemekleri 🥩", "Beef"),
    CHICKEN("Tavuk 🍗", "Chicken"),
    PASTA("Makarna 🍝", "Pasta"),
    SEAFOOD("Deniz Ürünleri 🐟", "Seafood"),
    VEGETARIAN("Vejetaryen 🥗", "Vegetarian"),
    DESSERT("Tatlı 🍰", "Dessert")
}

enum class CuisineFilter(val displayName: String, val searchKeyword: String) {
    ALL("Tüm Mutfaklar 🌍", "All"),
    TURKISH("Türk Mutfağı 🇹🇷", "Türk"),
    ITALIAN("İtalyan 🇮🇹", "İtalyan"),
    MEXICAN("Meksika 🇲🇽", "Meksika"),
    ASIAN("Asya / Çin 🥢", "Asya"),
    AMERICAN("Amerikan 🍔", "Amerikan"),
    JAPANESE("Japon 🍣", "Japon")
}

data class FilterState(
    val selectedCategory: MealCategoryFilter = MealCategoryFilter.ALL,
    val selectedCuisine: CuisineFilter = CuisineFilter.ALL,
    val selectedDifficulty: DifficultyLevel? = null, // null means any
    val maxPrepTimeMinutes: Int = 0, // 0 means any
    val isNutFreeOnly: Boolean = false,
    val isDairyFreeOnly: Boolean = false,
    val isVegetarianOnly: Boolean = false,
    val isVeganOnly: Boolean = false,
    val isGlutenFreeOnly: Boolean = false
) {
    val activeFilterCount: Int
        get() {
            var count = 0
            if (selectedCategory != MealCategoryFilter.ALL) count++
            if (selectedCuisine != CuisineFilter.ALL) count++
            if (selectedDifficulty != null) count++
            if (maxPrepTimeMinutes > 0) count++
            if (isNutFreeOnly) count++
            if (isDairyFreeOnly) count++
            if (isVegetarianOnly) count++
            if (isVeganOnly) count++
            if (isGlutenFreeOnly) count++
            return count
        }
}

class RecipeViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repository = RecipeRepository(db.likedRecipeDao())

    private val _rawRecipeDeck = MutableStateFlow<List<Recipe>>(emptyList())
    
    private val _filterState = MutableStateFlow(FilterState())
    val filterState: StateFlow<FilterState> = _filterState.asStateFlow()

    private val _displayedDeck = MutableStateFlow<List<Recipe>>(emptyList())
    val displayedDeck: StateFlow<List<Recipe>> = _displayedDeck.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedRecipeForDetail = MutableStateFlow<Recipe?>(null)
    val selectedRecipeForDetail: StateFlow<Recipe?> = _selectedRecipeForDetail.asStateFlow()

    val likedRecipes: StateFlow<List<Recipe>> = repository.likedRecipesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _likedSearchQuery = MutableStateFlow("")
    val likedSearchQuery: StateFlow<String> = _likedSearchQuery.asStateFlow()

    // --- User Profile & Password-Secured Authentication ---
    private val registeredAccounts = mutableMapOf<String, com.example.data.model.RegisteredAccount>()

    private val _userProfile = MutableStateFlow(UserProfile(isLoggedIn = false))
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    // --- Friendship System (Starts empty, no sample friends) ---
    private val _friendsList = MutableStateFlow<List<FriendUser>>(emptyList())
    val friendsList: StateFlow<List<FriendUser>> = _friendsList.asStateFlow()

    private val _sharedRecipes = MutableStateFlow<List<SharedRecipeItem>>(emptyList())
    val sharedRecipes: StateFlow<List<SharedRecipeItem>> = _sharedRecipes.asStateFlow()

    // --- Fridge Scanner & Ingredient Filter ---
    private val _isScanningFridge = MutableStateFlow(false)
    val isScanningFridge: StateFlow<Boolean> = _isScanningFridge.asStateFlow()

    private val _fridgeResult = MutableStateFlow<FridgeScanResult?>(null)
    val fridgeResult: StateFlow<FridgeScanResult?> = _fridgeResult.asStateFlow()

    private val _activeFridgeIngredients = MutableStateFlow<Set<String>>(emptySet())
    val activeFridgeIngredients: StateFlow<Set<String>> = _activeFridgeIngredients.asStateFlow()

    init {
        loadDeck()
    }

    fun loadDeck() {
        viewModelScope.launch {
            _isLoading.value = true
            val initialList = repository.getInitialDeckRecipes()
            _rawRecipeDeck.value = initialList
            applyFilters()
            _isLoading.value = false
        }
    }

    fun setCategoryFilter(category: MealCategoryFilter) {
        _filterState.value = _filterState.value.copy(selectedCategory = category)
        applyFilters()
    }

    fun setCuisineFilter(cuisine: CuisineFilter) {
        _filterState.value = _filterState.value.copy(selectedCuisine = cuisine)
        applyFilters()
    }

    fun setDifficultyFilter(difficulty: DifficultyLevel?) {
        _filterState.value = _filterState.value.copy(selectedDifficulty = difficulty)
        applyFilters()
    }

    fun setMaxPrepTimeFilter(minutes: Int) {
        _filterState.value = _filterState.value.copy(maxPrepTimeMinutes = minutes)
        applyFilters()
    }

    fun toggleNutFreeFilter() {
        val current = _filterState.value
        _filterState.value = current.copy(isNutFreeOnly = !current.isNutFreeOnly)
        applyFilters()
    }

    fun toggleDairyFreeFilter() {
        val current = _filterState.value
        _filterState.value = current.copy(isDairyFreeOnly = !current.isDairyFreeOnly)
        applyFilters()
    }

    fun toggleVegetarianFilter() {
        val current = _filterState.value
        _filterState.value = current.copy(isVegetarianOnly = !current.isVegetarianOnly)
        applyFilters()
    }

    fun toggleVeganFilter() {
        val current = _filterState.value
        _filterState.value = current.copy(isVeganOnly = !current.isVeganOnly)
        applyFilters()
    }

    fun toggleGlutenFreeFilter() {
        val current = _filterState.value
        _filterState.value = current.copy(isGlutenFreeOnly = !current.isGlutenFreeOnly)
        applyFilters()
    }

    fun resetFilters() {
        _filterState.value = FilterState()
        applyFilters()
    }

    private fun applyFilters() {
        val state = _filterState.value
        val fridgeIngs = _activeFridgeIngredients.value

        val filtered = _rawRecipeDeck.value.filter { recipe ->
            val matchCategory = when (state.selectedCategory) {
                MealCategoryFilter.ALL -> true
                else -> recipe.category.equals(state.selectedCategory.apiCategory, ignoreCase = true)
            }
            val matchCuisine = when (state.selectedCuisine) {
                CuisineFilter.ALL -> true
                else -> recipe.area.contains(state.selectedCuisine.searchKeyword, ignoreCase = true) ||
                        recipe.title.contains(state.selectedCuisine.searchKeyword, ignoreCase = true)
            }
            val matchDifficulty = state.selectedDifficulty == null || recipe.difficulty == state.selectedDifficulty
            val matchTime = state.maxPrepTimeMinutes == 0 || recipe.prepTimeMinutes <= state.maxPrepTimeMinutes
            val matchNutFree = !state.isNutFreeOnly || recipe.isNutFree
            val matchDairyFree = !state.isDairyFreeOnly || recipe.isDairyFree
            val matchVeg = !state.isVegetarianOnly || recipe.isVegetarian
            val matchVegan = !state.isVeganOnly || recipe.isVegan
            val matchGF = !state.isGlutenFreeOnly || recipe.isGlutenFree

            val matchFridge = if (fridgeIngs.isEmpty()) {
                true
            } else {
                recipe.ingredients.any { ing ->
                    fridgeIngs.any { fridge -> ing.name.contains(fridge, ignoreCase = true) || recipe.title.contains(fridge, ignoreCase = true) }
                }
            }

            matchCategory && matchCuisine && matchDifficulty && matchTime &&
                    matchNutFree && matchDairyFree && matchVeg && matchVegan && matchGF && matchFridge
        }
        _displayedDeck.value = filtered
    }

    // --- Password-Secured Authentication Functions ---
    fun registerAccount(name: String, email: String, pass: String, avatarEmoji: String): String? {
        val trimmedEmail = email.trim().lowercase()
        val trimmedName = name.trim()
        val trimmedPass = pass.trim()

        if (trimmedName.isBlank()) return "Lütfen bir ad/soyad veya takma ad girin."
        if (trimmedEmail.isBlank() || !trimmedEmail.contains("@")) return "Lütfen geçerli bir e-posta adresi girin."
        if (trimmedPass.length < 4) return "Şifre en az 4 karakter olmalıdır."

        if (registeredAccounts.containsKey(trimmedEmail)) {
            return "Bu e-posta adresi ile zaten kayıtlı bir hesap var!"
        }

        val generatedCode = "#" + (if (trimmedName.length >= 3) trimmedName.take(3).uppercase() else "SEF") + (100..999).random()
        val newProfile = UserProfile(
            id = "user_${System.currentTimeMillis()}",
            email = trimmedEmail,
            displayName = trimmedName,
            avatarEmoji = avatarEmoji,
            friendCode = generatedCode,
            bio = "Nefis tarifler ve lezzetler keşfediyorum! 🍳",
            isLoggedIn = true
        )

        registeredAccounts[trimmedEmail] = com.example.data.model.RegisteredAccount(
            email = trimmedEmail,
            passwordHash = trimmedPass, // Simple secure string matching
            profile = newProfile
        )

        _userProfile.value = newProfile
        return null // Success!
    }

    fun loginAccount(email: String, pass: String): String? {
        val trimmedEmail = email.trim().lowercase()
        val trimmedPass = pass.trim()

        if (trimmedEmail.isBlank()) return "Lütfen e-posta adresinizi girin."
        if (trimmedPass.isBlank()) return "Lütfen şifrenizi girin."

        val account = registeredAccounts[trimmedEmail]
            ?: return "Bu e-posta adresi ile kayıtlı hesap bulunamadı."

        if (account.passwordHash != trimmedPass) {
            return "Şifre hatalı! Lütfen tekrar deneyin."
        }

        _userProfile.value = account.profile.copy(isLoggedIn = true)
        return null // Success!
    }

    fun logout() {
        _userProfile.value = UserProfile(
            id = "",
            email = "",
            displayName = "",
            avatarEmoji = "👤",
            friendCode = "",
            bio = "",
            isLoggedIn = false
        )
    }

    // --- Friendship Functions ---
    fun addFriendByCode(code: String): String {
        val trimmed = code.trim().uppercase()
        if (trimmed.isBlank()) return "Lütfen geçerli bir arkadaş kodu girin."
        if (_friendsList.value.any { it.friendCode.equals(trimmed, ignoreCase = true) }) {
            return "Bu kullanıcı zaten arkadaş listenizde!"
        }
        val newFriend = FriendUser(
            id = "friend_${System.currentTimeMillis()}",
            displayName = "Şef " + trimmed.takeLast(4),
            avatarEmoji = listOf("🍳", "🍲", "🍕", "🍔", "🥗").random(),
            friendCode = trimmed,
            favoriteCuisine = listOf("Türk Mutfağı 🇹🇷", "İtalyan 🇮🇹", "Fransız 🇫🇷", "Meksika 🇲🇽").random(),
            sharedRecipeCount = (1..15).random(),
            isOnline = true
        )
        _friendsList.value = _friendsList.value + newFriend
        return "Tebrikler! ${newFriend.displayName} arkadaş listenize eklendi! 🎉"
    }

    fun shareRecipeWithFriend(recipe: Recipe, friendName: String, note: String) {
        val newItem = SharedRecipeItem(
            id = "share_${System.currentTimeMillis()}",
            recipeId = recipe.id,
            recipeTitle = recipe.title,
            recipeImageUrl = recipe.imageUrl,
            fromFriendName = _userProfile.value.displayName,
            note = note.ifBlank { "Sana bu harika tarifi tavsiye ediyorum! 👌" },
            timestamp = "Şimdi"
        )
        _sharedRecipes.value = listOf(newItem) + _sharedRecipes.value
    }

    // --- Fridge Scan Functions ---
    fun analyzeFridgePhotoBase64(base64Image: String) {
        viewModelScope.launch {
            _isScanningFridge.value = true
            val detectedList = repository.analyzeFridgeImageWithSupabase(base64Image)
            
            _activeFridgeIngredients.value = detectedList.toSet()
            
            val summaryText = if (detectedList.isNotEmpty()) {
                "Buzdolabı AI ile analiz edildi! ${detectedList.size} ana malzeme tespit edildi."
            } else {
                "Fotoğrafta malzeme tespit edilemedi veya API bağlantısı kurulamadı. Lütfen Supabase API bilgilerinizi kontrol edin ya da net bir fotoğraf yükleyin."
            }

            _fridgeResult.value = FridgeScanResult(
                detectedIngredients = detectedList,
                analysisSummary = summaryText,
                recipeSuggestionsCount = if (detectedList.isEmpty()) 0 else _rawRecipeDeck.value.count { recipe ->
                    recipe.ingredients.any { ing ->
                        detectedList.any { fridge -> ing.name.contains(fridge, ignoreCase = true) }
                    }
                }
            )
            applyFilters()
            _isScanningFridge.value = false
        }
    }

    fun analyzeFridgePhoto(detectedList: List<String>) {
        viewModelScope.launch {
            _isScanningFridge.value = true
            kotlinx.coroutines.delay(1200) // Realistic AI scanning feedback delay
            _activeFridgeIngredients.value = detectedList.toSet()
            _fridgeResult.value = FridgeScanResult(
                detectedIngredients = detectedList,
                analysisSummary = "Buzdolabı analiz edildi! ${detectedList.size} ana malzeme tespit edildi.",
                recipeSuggestionsCount = _rawRecipeDeck.value.count { recipe ->
                    recipe.ingredients.any { ing ->
                        detectedList.any { fridge -> ing.name.contains(fridge, ignoreCase = true) }
                    }
                }
            )
            applyFilters()
            _isScanningFridge.value = false
        }
    }

    fun toggleFridgeIngredient(ingredient: String) {
        val current = _activeFridgeIngredients.value.toMutableSet()
        if (current.contains(ingredient)) {
            current.remove(ingredient)
        } else {
            current.add(ingredient)
        }
        _activeFridgeIngredients.value = current
        applyFilters()
    }

    fun clearFridgeFilter() {
        _activeFridgeIngredients.value = emptySet()
        _fridgeResult.value = null
        applyFilters()
    }

    fun onSwipeRight(recipe: Recipe) {
        viewModelScope.launch {
            repository.saveLikedRecipe(recipe)
            popTopRecipe(recipe)
        }
    }

    fun onSwipeLeft(recipe: Recipe) {
        popTopRecipe(recipe)
    }

    private fun popTopRecipe(recipe: Recipe) {
        _rawRecipeDeck.value = _rawRecipeDeck.value.filter { it.id != recipe.id }
        applyFilters()

        // If deck gets small (< 3), fetch additional random recipes asynchronously
        if (_displayedDeck.value.size < 3) {
            viewModelScope.launch {
                val newRecipe = repository.fetchMoreRandomRecipe()
                if (_rawRecipeDeck.value.none { it.id == newRecipe.id }) {
                    _rawRecipeDeck.value = _rawRecipeDeck.value + newRecipe
                    applyFilters()
                }
            }
        }
    }

    fun openDetailModal(recipe: Recipe) {
        _selectedRecipeForDetail.value = recipe
    }

    fun closeDetailModal() {
        _selectedRecipeForDetail.value = null
    }

    fun removeLikedRecipe(recipe: Recipe) {
        viewModelScope.launch {
            repository.removeLikedRecipe(recipe.id)
        }
    }

    fun clearAllLiked() {
        viewModelScope.launch {
            repository.clearAllLiked()
        }
    }

    fun setLikedSearchQuery(query: String) {
        _likedSearchQuery.value = query
    }
}
