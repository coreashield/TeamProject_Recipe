data class RecipeDetails(
    val aggregateLikes: Int,
    val analyzedInstructions: List<AnalyzedInstruction>,
    val approved: Int,
    val cheap: Boolean,
    val cookingMinutes: Int?,
    val creditsText: String,
    val cuisines: List<String>,
    val dairyFree: Boolean,
    val diets: List<String>,
    val dishTypes: List<String>,
    val extendedIngredients: List<ExtendedIngredient>,
    val gaps: String,
    val glutenFree: Boolean,
    val healthScore: Int,
    val id: Int,
    val image: String,
    val imageType: String,
    val instructions: String,
    val lowFodmap: Boolean,
    val occasions: List<String>,
    val openLicense: Int,
    val originalId: Int?,
    val preparationMinutes: Int?,
    val pricePerServing: Double,
    val readyInMinutes: Int,
    val servings: Int,
    val sourceName: String,
    val sourceUrl: String,
    val spoonacularScore: Double,
    val spoonacularSourceUrl: String,
    val summary: String,
    val sustainable: Boolean,
    val title: String,
    val vegan: Boolean,
    val vegetarian: Boolean,
    val veryHealthy: Boolean,
    val veryPopular: Boolean,
    val weightWatcherSmartPoints: Int,
    val winePairing: WinePairing
)

data class WinePairing(
    val pairedWines: List<String>,
    val pairingText: String,
    val productMatches: List<ProductMatch>
)

data class ProductMatch(
    val id: Int,
    val title: String,
    val description: String,
    val price: String,
    val imageUrl: String,
    val averageRating: Double,
    val ratingCount: Int,
    val score: Double,
    val link: String
)

data class AnalyzedInstruction(
    val name: String,
    val steps: List<Step>
)

data class Step(
    val equipment: List<Equipment>,
    val ingredients: List<Ingredient>,
    val number: Int,
    val step: String,
    val length: Length?
)

data class Equipment(
    val id: Int,
    val image: String,
    val localizedName: String,
    val name: String
)

data class Ingredient(
    val id: Int,
    val image: String,
    val localizedName: String,
    val name: String
)

data class Length(
    val number: Int,
    val unit: String
)

data class ExtendedIngredient(
    val aisle: String,
    val amount: Double,
    val consistency: String,
    val id: Int,
    val image: String,
    val measures: Measures,
    val meta: List<String>,
    val name: String,
    val nameClean: String,
    val original: String,
    val originalName: String,
    val unit: String
)

data class Measures(
    val metric: Metric,
    val us: Us
)

data class Metric(
    val amount: Double,
    val unitLong: String,
    val unitShort: String
)

data class Us(
    val amount: Double,
    val unitLong: String,
    val unitShort: String
)

val allIngredients = listOf(
    "kimchi",
    "pork",
    "beef",
    "chicken",
    "sausage",
    "Fish Cakes",
    "Radish",
    "bean curd",
    "onion",
    "Spring onion",
    "garlic",
    "potato",
    "pepper",
    "egg",
    "Carrot",
    "milk",
    "Sliced Cheese",
    "Frozen dumplings",
    "Ramen",
    "bean sprouts",
    "Canned tuna",
    "cucumber",
    "enoki mushroom",
    "Matsutake mushroom",
    "Shiitake mushrooms",
    "bacon",
    "spam",
    "tomato",
    "Cherry Tomatoes",
    "Bean",
    "Tomato",
    "Cheese",
    "Basil",
    "Garlic",
    "Onion"
)
