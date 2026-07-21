package com.nooshyar.app.domain.model

enum class ActivityLevel { LOW, MEDIUM, HIGH }
enum class PressureLevel { LOW, MEDIUM, HIGH }
enum class CaffeineSensitivity { LOW, MEDIUM, HIGH }
enum class TemperatureType { HOT, COLD, ANY }
enum class ThemeMode { LIGHT, DARK, SYSTEM }

enum class UserGoal(val key: String) {
    INCREASE_WATER("water"),
    CONTROL_CAFFEINE("caffeine"),
    IMPROVE_SLEEP("sleep"),
    REDUCE_SUGAR("sugar"),
    INCREASE_FOCUS("focus"),
    REDUCE_ENERGY("energy"),
    TRACK_HABITS("habits"),
    BALANCE("balance")
}

enum class ConsumptionMood(val key: String) {
    THIRSTY("thirsty"),
    TIRED("tired"),
    LOW_ENERGY("low_energy"),
    ANXIOUS("anxious"),
    FOCUS("focus"),
    CALM("calm"),
    HAPPY("happy"),
    BORED("bored"),
    AFTER_SPORT("after_sport"),
    WITH_FOOD("with_food"),
    NONE("none")
}

enum class UserFeeling(val key: String) {
    TIRED("tired"),
    THIRSTY("thirsty"),
    FOCUS("focus"),
    ANXIOUS("anxious"),
    HOT("hot"),
    COLD("cold"),
    REST("rest"),
    TASTY("tasty")
}

enum class UserActivity(val key: String) {
    WORK("work"),
    STUDY("study"),
    MEETING("meeting"),
    DRIVING("driving"),
    EXERCISE("exercise"),
    REST("rest"),
    EATING("eating")
}

enum class SuggestionResponse {
    ACCEPTED, REJECTED, ALTERNATIVE, SAVED
}

data class UserProfile(
    val id: Long = 1L,
    val name: String = "",
    val age: Int = 25,
    val weight: Float = 70f,
    val height: Float? = null,
    val wakeTimeMinutes: Int = 7 * 60,
    val sleepTimeMinutes: Int = 23 * 60 + 30,
    val workStartMinutes: Int = 9 * 60,
    val workEndMinutes: Int = 17 * 60,
    val activityLevel: ActivityLevel = ActivityLevel.MEDIUM,
    val workPressure: PressureLevel = PressureLevel.MEDIUM,
    val caffeineSensitivity: CaffeineSensitivity = CaffeineSensitivity.MEDIUM,
    val caffeineDailyLimit: Int = 400,
    val waterDailyGoal: Int = 2450,
    val caffeineHalfLifeHours: Float = 5f,
    val selectedGoals: Set<UserGoal> = emptySet(),
    val likedDrinkIds: Set<Long> = emptySet(),
    val dislikedDrinkIds: Set<Long> = emptySet()
)

data class Drink(
    val id: Long = 0,
    val nameFa: String,
    val nameEn: String,
    val category: String,
    val defaultVolume: Int,
    val caffeinePerServing: Int,
    val sugarPerServing: Float,
    val caloriesPerServing: Int,
    val temperatureType: TemperatureType,
    val isCaffeinated: Boolean,
    val icon: String,
    val description: String,
    val suitableMorning: Boolean = true,
    val suitableNoon: Boolean = true,
    val suitableEvening: Boolean = true,
    val suitableNight: Boolean = false,
    val isCustom: Boolean = false
)

data class ConsumptionLog(
    val id: Long = 0,
    val userId: Long = 1L,
    val drinkId: Long,
    val drinkName: String = "",
    val dateTimeMillis: Long,
    val volume: Int,
    val servings: Int = 1,
    val caffeine: Int,
    val sugar: Float,
    val calories: Int,
    val mood: ConsumptionMood? = null,
    val activity: UserActivity? = null,
    val note: String? = null,
    val wasSuggested: Boolean = false,
    val suggestionId: Long? = null
)

data class DrinkSuggestion(
    val drink: Drink,
    val score: Int,
    val reasons: List<String>,
    val suggestedVolume: Int,
    val approxCaffeine: Int
)

data class SuggestionContext(
    val feelings: Set<UserFeeling> = emptySet(),
    val activity: UserActivity? = null,
    val temperaturePref: TemperatureType = TemperatureType.ANY,
    val hoursUntilSleep: Float? = null
)

data class DailyStats(
    val totalDrinks: Int = 0,
    val totalVolume: Int = 0,
    val waterVolume: Int = 0,
    val totalCaffeine: Int = 0,
    val totalSugar: Float = 0f,
    val activeCaffeine: Int = 0,
    val firstDrinkTime: Long? = null,
    val lastDrinkTime: Long? = null,
    val mostConsumedDrink: String? = null
)

data class WeeklyStats(
    val avgWater: Int = 0,
    val avgCaffeine: Int = 0,
    val avgDrinkCount: Int = 0,
    val popularDrink: String? = null,
    val maxCaffeineDay: String? = null,
    val minWaterDay: String? = null,
    val waterChangePercent: Int = 0,
    val caffeineChangePercent: Int = 0,
    val dailyWater: List<Int> = emptyList(),
    val dailyCaffeine: List<Int> = emptyList()
)

data class CaffeineStatus(
    val consumedToday: Int,
    val activeEstimate: Int,
    val lastConsumptionTime: Long?,
    val sleepTimeMinutes: Int,
    val recommendation: String
)

enum class DayStatus {
    NO_DATA, BALANCED, LOW_WATER, HIGH_CAFFEINE, COMPLETE
}

data class CalendarDay(
    val dayOfMonth: Int,
    val millis: Long,
    val status: DayStatus,
    val waterMl: Int = 0,
    val caffeineMg: Int = 0,
    val drinkCount: Int = 0
)

data class MonthlyStats(
    val totalDrinks: Int = 0,
    val totalCoffeeCups: Int = 0,
    val totalTeaCups: Int = 0,
    val totalDecafDrinks: Int = 0,
    val totalWaterMl: Int = 0,
    val avgDailyCaffeine: Int = 0,
    val daysOverLimit: Int = 0,
    val bestDayLabel: String? = null,
    val attentionDayLabel: String? = null,
    val waterChangePercent: Int = 0,
    val caffeineChangePercent: Int = 0,
    val habitTip: String = "",
    val dailyWater: List<Int> = emptyList()
)

data class YearlyStats(
    val totalDrinks: Int = 0,
    val totalCoffeeCups: Int = 0,
    val totalTeaCups: Int = 0,
    val totalWaterMl: Int = 0,
    val avgDailyCaffeine: Int = 0,
    val popularDrink: String? = null,
    val popularHour: String? = null,
    val busiestWeekday: String? = null,
    val busiestMonth: String? = null,
    val loggedDays: Int = 0,
    val acceptedSuggestions: Int = 0
)

/** وزن رفتاری برای هر نوشیدنی بر اساس پذیرش/رد پیشنهادها */
data class DrinkBehaviorWeight(
    val drinkId: Long,
    val accepted: Int = 0,
    val rejected: Int = 0
) {
    fun scoreBonus(): Int {
        val total = accepted + rejected
        if (total < 2) return 0
        val rate = accepted.toFloat() / total
        return when {
            rate >= 0.7f -> 15
            rate >= 0.5f -> 8
            rate <= 0.2f -> -15
            rate <= 0.4f -> -8
            else -> 0
        }
    }
}
