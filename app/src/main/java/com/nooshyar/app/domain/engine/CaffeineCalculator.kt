package com.nooshyar.app.domain.engine

import com.nooshyar.app.domain.model.ConsumptionLog
import com.nooshyar.app.domain.model.UserProfile
import kotlin.math.pow

object CaffeineCalculator {

    /**
     * تخمین کافئین فعال با مدل نیمه‌عمر نمایی.
     * این مقدار تقریبی است و در افراد مختلف متفاوت است.
     */
    fun calculateActiveCaffeine(
        logs: List<ConsumptionLog>,
        nowMillis: Long = System.currentTimeMillis(),
        halfLifeHours: Float = 5f
    ): Int {
        if (halfLifeHours <= 0f) return 0
        return logs.sumOf { log ->
            val hoursElapsed = (nowMillis - log.dateTimeMillis) / (1000.0 * 60 * 60)
            if (hoursElapsed < 0) 0.0
            else log.caffeine * 0.5.pow(hoursElapsed / halfLifeHours)
        }.toInt()
    }

    fun buildStatus(
        logs: List<ConsumptionLog>,
        profile: UserProfile,
        nowMillis: Long = System.currentTimeMillis()
    ): com.nooshyar.app.domain.model.CaffeineStatus {
        val todayLogs = logs.filter { it.dateTimeMillis >= com.nooshyar.app.core.util.JalaliDate.startOfDay(nowMillis) }
        val consumed = todayLogs.sumOf { it.caffeine }
        val active = calculateActiveCaffeine(todayLogs, nowMillis, profile.caffeineHalfLifeHours)
        val lastTime = todayLogs.maxByOrNull { it.dateTimeMillis }?.dateTimeMillis
        val hoursToSleep = com.nooshyar.app.core.util.JalaliDate.minutesUntilSleep(nowMillis, profile.sleepTimeMinutes)

        val recommendation = when {
            hoursToSleep < 5f && active > 50 -> "بهتر است نوشیدنی بعدی بدون کافئین باشد."
            consumed >= profile.caffeineDailyLimit -> "به حد روزانه کافئین نزدیک شده‌اید."
            active > profile.caffeineDailyLimit / 2 -> "کافئین فعال تخمینی هنوز بالاست."
            else -> "مصرف کافئین در محدوده مناسب است."
        }

        return com.nooshyar.app.domain.model.CaffeineStatus(
            consumedToday = consumed,
            activeEstimate = active,
            lastConsumptionTime = lastTime,
            sleepTimeMinutes = profile.sleepTimeMinutes,
            recommendation = recommendation
        )
    }

    fun calculateForDrink(caffeinePerServing: Int, servings: Int): Int =
        caffeinePerServing * servings
}
