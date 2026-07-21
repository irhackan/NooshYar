package com.nooshyar.app.domain.engine

import com.nooshyar.app.core.util.JalaliDate
import com.nooshyar.app.domain.model.*
import java.util.Calendar
import kotlin.math.max
import kotlin.math.min

/**
 * موتور پیشنهاد مبتنی بر قواعد + وزن یادگیری رفتاری
 */
class RecommendationEngine {

    fun getSuggestions(
        drinks: List<Drink>,
        profile: UserProfile,
        todayLogs: List<ConsumptionLog>,
        context: SuggestionContext,
        behaviorWeights: List<DrinkBehaviorWeight> = emptyList(),
        nowMillis: Long = System.currentTimeMillis(),
        limit: Int = 3
    ): List<DrinkSuggestion> {
        val weightMap = behaviorWeights.associateBy { it.drinkId }
        val activeCaffeine = CaffeineCalculator.calculateActiveCaffeine(
            todayLogs, nowMillis, profile.caffeineHalfLifeHours
        )
        val consumedCaffeine = todayLogs.sumOf { it.caffeine }
        val waterVolume = todayLogs.filter { it.drinkName.contains("آب") || it.drinkName == "آب" }
            .sumOf { it.volume }
        val hoursToSleep = context.hoursUntilSleep
            ?: JalaliDate.minutesUntilSleep(nowMillis, profile.sleepTimeMinutes)
        val hourOfDay = Calendar.getInstance().apply { timeInMillis = nowMillis }
            .get(Calendar.HOUR_OF_DAY)
        val lastDrinkHoursAgo = todayLogs.maxByOrNull { it.dateTimeMillis }?.let {
            (nowMillis - it.dateTimeMillis) / (1000f * 60 * 60)
        } ?: 24f

        return drinks
            .filter { it.id !in profile.dislikedDrinkIds }
            .map { drink ->
                scoreDrink(
                    drink, profile, context, hourOfDay, hoursToSleep,
                    activeCaffeine, consumedCaffeine, waterVolume,
                    lastDrinkHoursAgo, todayLogs, weightMap[drink.id]
                )
            }
            .sortedByDescending { it.score }
            .take(limit)
    }

    fun getTopSuggestion(
        drinks: List<Drink>,
        profile: UserProfile,
        todayLogs: List<ConsumptionLog>,
        behaviorWeights: List<DrinkBehaviorWeight> = emptyList(),
        nowMillis: Long = System.currentTimeMillis()
    ): DrinkSuggestion? {
        return getSuggestions(
            drinks, profile, todayLogs, SuggestionContext(), behaviorWeights, nowMillis, 1
        ).firstOrNull()
    }

    private fun scoreDrink(
        drink: Drink,
        profile: UserProfile,
        context: SuggestionContext,
        hourOfDay: Int,
        hoursToSleep: Float,
        activeCaffeine: Int,
        consumedCaffeine: Int,
        waterVolume: Int,
        lastDrinkHoursAgo: Float,
        todayLogs: List<ConsumptionLog>,
        behavior: DrinkBehaviorWeight?
    ): DrinkSuggestion {
        var score = 50
        val reasons = mutableListOf<String>()

        val timeScore = when {
            hourOfDay in 6..11 && drink.suitableMorning -> 20
            hourOfDay in 12..16 && drink.suitableNoon -> 20
            hourOfDay in 17..20 && drink.suitableEvening -> 20
            hourOfDay >= 21 && drink.suitableNight -> 20
            else -> 5
        }
        score += timeScore - 10

        if (UserFeeling.THIRSTY in context.feelings && drink.category == "آب") {
            score += 25
            reasons.add("به دلیل احساس تشنگی، آب گزینه مناسبی است.")
        }
        if (UserFeeling.TIRED in context.feelings && drink.isCaffeinated && hoursToSleep > 6) {
            score += 15
            reasons.add("برای کاهش خستگی، نوشیدنی ملایم کافئین‌دار مناسب است.")
        }
        if (UserFeeling.FOCUS in context.feelings && drink.isCaffeinated && activeCaffeine < 150) {
            score += 18
            reasons.add("برای تمرکز، مقدار کنترل‌شده کافئین می‌تواند مفید باشد.")
        }
        if (UserFeeling.REST in context.feelings && !drink.isCaffeinated) {
            score += 20
            reasons.add("برای استراحت، نوشیدنی بدون کافئین بهتر است.")
        }
        if (UserFeeling.ANXIOUS in context.feelings && drink.isCaffeinated) {
            score -= 25
            reasons.add("در حالت اضطراب، کافئین ممکن است مناسب نباشد.")
        }

        if (hoursToSleep < 6 && drink.isCaffeinated) {
            score -= 30
            reasons.add("تا زمان خواب کمتر از ۶ ساعت مانده؛ کافئین توصیه نمی‌شود.")
        } else if (hoursToSleep < 6 && !drink.isCaffeinated) {
            score += 20
            reasons.add("نزدیک به زمان خواب، نوشیدنی بدون کافئین انتخاب بهتری است.")
        }
        if (activeCaffeine > 100 && drink.isCaffeinated) {
            score -= 15
            reasons.add("کافئین فعال تخمینی شما هنوز بالاست.")
        }

        if (waterVolume < profile.waterDailyGoal / 2 && drink.category.contains("آب")) {
            score += 15
            reasons.add("مصرف آب امروز کمتر از هدف است.")
        }
        if (lastDrinkHoursAgo > 2 && drink.category == "آب") {
            score += 12
            reasons.add("از آخرین نوشیدنی بیش از ۲ ساعت گذشته است.")
        }

        if (drink.id in profile.likedDrinkIds) {
            score += 12
            reasons.add("این نوشیدنی در لیست علاقه‌مندی‌های شماست.")
        }

        if (UserGoal.INCREASE_WATER in profile.selectedGoals && drink.category == "آب") score += 10
        if (UserGoal.CONTROL_CAFFEINE in profile.selectedGoals && !drink.isCaffeinated) score += 8
        if (UserGoal.IMPROVE_SLEEP in profile.selectedGoals && !drink.isCaffeinated && hoursToSleep < 8) score += 10

        when (context.temperaturePref) {
            TemperatureType.HOT -> if (drink.temperatureType == TemperatureType.HOT) score += 8
            TemperatureType.COLD -> if (drink.temperatureType == TemperatureType.COLD) score += 8
            TemperatureType.ANY -> {}
        }

        val sameCount = todayLogs.count { it.drinkId == drink.id }
        if (sameCount >= 3) {
            score -= 10
            reasons.add("این نوشیدنی امروز چند بار مصرف شده است.")
        }

        val approxCaffeine = drink.caffeinePerServing
        if (consumedCaffeine + approxCaffeine > profile.caffeineDailyLimit) {
            score -= 20
            reasons.add("مصرف این نوشیدنی از حد روزانه کافئین عبور می‌دهد.")
        }

        // یادگیری رفتاری از پاسخ‌های قبلی کاربر
        behavior?.let { w ->
            val bonus = w.scoreBonus()
            if (bonus != 0) {
                score += bonus
                if (bonus > 0) {
                    reasons.add("بر اساس پذیرش‌های قبلی شما، این گزینه مناسب‌تر است.")
                } else {
                    reasons.add("قبلاً این پیشنهاد کمتر مورد پذیرش شما بوده است.")
                }
            }
        }

        score = min(100, max(0, score))
        if (reasons.isEmpty()) {
            reasons.add("بر اساس زمان روز و عادت‌های شما انتخاب شده است.")
        }

        return DrinkSuggestion(
            drink = drink,
            score = score,
            reasons = reasons.take(3),
            suggestedVolume = drink.defaultVolume,
            approxCaffeine = approxCaffeine
        )
    }

    fun getDailyInsight(
        profile: UserProfile,
        todayLogs: List<ConsumptionLog>
    ): String {
        val water = todayLogs.filter { it.drinkName.contains("آب") || it.drinkName == "آب" }
            .sumOf { it.volume }
        val caffeine = todayLogs.sumOf { it.caffeine }
        return when {
            water < profile.waterDailyGoal / 3 -> "امروز مصرف آب شما کمتر از الگوی همیشگی است."
            caffeine > profile.caffeineDailyLimit * 0.8 -> "مصرف کافئین امروز به حد روزانه نزدیک است."
            todayLogs.isEmpty() -> "هنوز نوشیدنی‌ای ثبت نکرده‌اید. یک لیوان آب شروع خوبی است!"
            else -> "روز خوبی برای حفظ تعادل نوشیدنی‌هاست."
        }
    }
}
