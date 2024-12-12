data class DashboardResponse(
    val status: String,
    val message: String,
    val data: NutritionData
)

data class NutritionData(
    val nutritionHistory: NutritionHistory
)

data class NutritionHistory(
    val totalCalories: Double,
    val totalProtein: Double,
    val totalCarbs: Double,
    val totalFat: Double,
    val totalFiber: Double,
    val targetCalories: Double,
    val targetProtein: Double,
    val targetCarbs: Double,
    val targetFat: Double,
    val targetFiber: Double,
    val remainingCalories: Double,
    val remainingProtein: Double,
    val remainingCarbs: Double,
    val remainingFat: Double,
    val remainingFiber: Double
)

