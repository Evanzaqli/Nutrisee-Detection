data class DashboardResponse(
    val status: String,
    val message: String,
    val data: Data
) {
    data class Data(
        val dailyNeeds: DailyNeeds
    ) {
        data class DailyNeeds(
            val calories: Int,
            val protein: Int,
            val carbohydrates: Int,
            val fat: Int,
            val fiber: Int
        )
    }
}
