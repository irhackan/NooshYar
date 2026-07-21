package com.nooshyar.app.data.repository

import com.nooshyar.app.core.util.JalaliDate
import com.nooshyar.app.data.local.dao.*
import com.nooshyar.app.data.local.entity.NotificationSettingsEntity
import com.nooshyar.app.data.mapper.Mappers.toDomain
import com.nooshyar.app.data.mapper.Mappers.toEntity
import com.nooshyar.app.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao
) {
    fun observeProfile(): Flow<UserProfile?> = userDao.observeUser().map { it?.toDomain() }

    suspend fun getProfile(): UserProfile? = userDao.getUser()?.toDomain()

    suspend fun saveProfile(profile: UserProfile) {
        userDao.insert(profile.toEntity())
    }

    suspend fun deleteAll() = userDao.deleteAll()
}

@Singleton
class DrinkRepository @Inject constructor(
    private val drinkDao: DrinkDao
) {
    fun observeAll(): Flow<List<Drink>> = drinkDao.observeAll().map { list -> list.map { it.toDomain() } }

    suspend fun getAll(): List<Drink> = drinkDao.getAll().map { it.toDomain() }

    suspend fun getById(id: Long): Drink? = drinkDao.getById(id)?.toDomain()

    suspend fun search(query: String): List<Drink> =
        if (query.isBlank()) getAll() else drinkDao.search(query).map { it.toDomain() }

    suspend fun addCustom(drink: Drink): Long = drinkDao.insert(drink.copy(isCustom = true).toEntity())
}

@Singleton
class ConsumptionRepository @Inject constructor(
    private val consumptionDao: ConsumptionDao
) {
    fun observeToday(): Flow<List<ConsumptionLog>> {
        val start = JalaliDate.startOfDay()
        val end = JalaliDate.endOfDay()
        return consumptionDao.observeBetween(start, end).map { list -> list.map { it.toDomain() } }
    }

    suspend fun getToday(): List<ConsumptionLog> {
        val start = JalaliDate.startOfDay()
        val end = JalaliDate.endOfDay()
        return consumptionDao.getBetween(start, end).map { it.toDomain() }
    }

    suspend fun getBetween(start: Long, end: Long): List<ConsumptionLog> =
        consumptionDao.getBetween(start, end).map { it.toDomain() }

    suspend fun getRecent(limit: Int = 10): List<ConsumptionLog> =
        consumptionDao.getRecent(limit).map { it.toDomain() }

    suspend fun getAll(): List<ConsumptionLog> =
        consumptionDao.getAll().map { it.toDomain() }

    suspend fun getPopularDrinkIds(limit: Int = 8): List<Long> =
        consumptionDao.getPopularDrinkIds(limit).map { it.drinkId }

    suspend fun log(consumption: ConsumptionLog): Long =
        consumptionDao.insert(consumption.toEntity())

    suspend fun update(consumption: ConsumptionLog) =
        consumptionDao.update(consumption.toEntity())

    suspend fun delete(consumption: ConsumptionLog) =
        consumptionDao.delete(consumption.toEntity())

    suspend fun deleteAll() = consumptionDao.deleteAll()
}

@Singleton
class SuggestionRepository @Inject constructor(
    private val suggestionDao: SuggestionDao
) {
    suspend fun saveSuggestion(
        userId: Long,
        drinkId: Long,
        score: Int,
        reason: String
    ): Long = suggestionDao.insert(
        com.nooshyar.app.data.local.entity.SuggestionEntity(
            userId = userId,
            dateTime = System.currentTimeMillis(),
            suggestedDrinkId = drinkId,
            score = score,
            reason = reason,
            userResponse = null,
            acceptedAt = null
        )
    )

    suspend fun recordResponse(id: Long, response: SuggestionResponse) {
        suggestionDao.updateResponse(
            id, response.name,
            if (response == SuggestionResponse.ACCEPTED) System.currentTimeMillis() else null
        )
    }

    suspend fun getBehaviorWeights(): List<DrinkBehaviorWeight> {
        val responded = suggestionDao.getResponded()
        return responded.groupBy { it.suggestedDrinkId }.map { (drinkId, list) ->
            DrinkBehaviorWeight(
                drinkId = drinkId,
                accepted = list.count { it.userResponse == SuggestionResponse.ACCEPTED.name },
                rejected = list.count {
                    it.userResponse == SuggestionResponse.REJECTED.name ||
                        it.userResponse == SuggestionResponse.ALTERNATIVE.name
                }
            )
        }
    }

    suspend fun countAccepted(): Int = suggestionDao.countAccepted()

    suspend fun deleteAll() = suggestionDao.deleteAll()
}

@Singleton
class NotificationRepository @Inject constructor(
    private val notificationSettingsDao: NotificationSettingsDao
) {
    fun observeSettings(): Flow<List<NotificationSettingsEntity>> =
        notificationSettingsDao.observeAll()

    suspend fun get(type: String): NotificationSettingsEntity? =
        notificationSettingsDao.get(type)

    suspend fun save(settings: NotificationSettingsEntity) =
        notificationSettingsDao.insert(settings)

    suspend fun initDefaults() {
        val defaults = listOf(
            NotificationSettingsEntity("water", true, 8 * 60, 22 * 60, 3, 23 * 60, 7 * 60),
            NotificationSettingsEntity("caffeine", true, 14 * 60, 23 * 60, 2, 23 * 60, 7 * 60),
            NotificationSettingsEntity("daily", true, 21 * 60, 22 * 60, 1, 23 * 60, 7 * 60)
        )
        defaults.forEach { notificationSettingsDao.insert(it) }
    }

    suspend fun deleteAll() = notificationSettingsDao.deleteAll()
}

@Singleton
class ReportRepository @Inject constructor(
    private val consumptionRepository: ConsumptionRepository
) {
    suspend fun getDailyStats(dayMillis: Long = System.currentTimeMillis()): DailyStats {
        val start = JalaliDate.startOfDay(dayMillis)
        val end = JalaliDate.endOfDay(dayMillis)
        val logs = consumptionRepository.getBetween(start, end)
        if (logs.isEmpty()) return DailyStats()

        val water = logs.filter { it.drinkName.contains("آب") || it.drinkName == "آب" }.sumOf { it.volume }
        val mostConsumed = logs.groupBy { it.drinkName }.maxByOrNull { it.value.size }?.key

        return DailyStats(
            totalDrinks = logs.size,
            totalVolume = logs.sumOf { it.volume },
            waterVolume = water,
            totalCaffeine = logs.sumOf { it.caffeine },
            totalSugar = logs.sumOf { it.sugar.toDouble() }.toFloat(),
            firstDrinkTime = logs.minByOrNull { it.dateTimeMillis }?.dateTimeMillis,
            lastDrinkTime = logs.maxByOrNull { it.dateTimeMillis }?.dateTimeMillis,
            mostConsumedDrink = mostConsumed
        )
    }

    suspend fun getWeeklyStats(): WeeklyStats {
        val weekStart = JalaliDate.startOfWeek()
        val now = System.currentTimeMillis()
        val dailyWater = mutableListOf<Int>()
        val dailyCaffeine = mutableListOf<Int>()

        for (i in 0 until 7) {
            val dayStart = weekStart + i * 24L * 60 * 60 * 1000
            if (dayStart > now) break
            val stats = getDailyStats(dayStart)
            dailyWater.add(stats.waterVolume)
            dailyCaffeine.add(stats.totalCaffeine)
        }

        val prevWeekWater = mutableListOf<Int>()
        val prevWeekCaffeine = mutableListOf<Int>()
        for (i in 0 until 7) {
            val dayStart = weekStart - (7 - i) * 24L * 60 * 60 * 1000
            val stats = getDailyStats(dayStart)
            prevWeekWater.add(stats.waterVolume)
            prevWeekCaffeine.add(stats.totalCaffeine)
        }

        val avgWater = if (dailyWater.isNotEmpty()) dailyWater.average().toInt() else 0
        val avgCaffeine = if (dailyCaffeine.isNotEmpty()) dailyCaffeine.average().toInt() else 0
        val prevAvgWater = if (prevWeekWater.isNotEmpty()) prevWeekWater.average().toInt() else 0
        val prevAvgCaffeine = if (prevWeekCaffeine.isNotEmpty()) prevWeekCaffeine.average().toInt() else 0

        val weekLogs = consumptionRepository.getBetween(weekStart, now)
        val popular = weekLogs.groupBy { it.drinkName }.maxByOrNull { it.value.size }?.key

        return WeeklyStats(
            avgWater = avgWater,
            avgCaffeine = avgCaffeine,
            avgDrinkCount = if (dailyWater.isNotEmpty()) weekLogs.size / dailyWater.size else 0,
            popularDrink = popular,
            waterChangePercent = if (prevAvgWater > 0) ((avgWater - prevAvgWater) * 100 / prevAvgWater) else 0,
            caffeineChangePercent = if (prevAvgCaffeine > 0) ((avgCaffeine - prevAvgCaffeine) * 100 / prevAvgCaffeine) else 0,
            dailyWater = dailyWater,
            dailyCaffeine = dailyCaffeine
        )
    }

    suspend fun getMonthlyStats(
        monthMillis: Long = System.currentTimeMillis(),
        caffeineLimit: Int = 400
    ): MonthlyStats {
        val start = JalaliDate.startOfMonth(monthMillis)
        val end = JalaliDate.endOfMonth(monthMillis)
        val logs = consumptionRepository.getBetween(start, end)
        val days = JalaliDate.daysInMonth(monthMillis)
        val dailyWater = mutableListOf<Int>()
        var daysOverLimit = 0
        var bestDay: Pair<String, Int>? = null
        var attentionDay: Pair<String, Int>? = null

        for (d in 1..days) {
            val dayMillis = JalaliDate.dayMillisInMonth(d, monthMillis)
            if (JalaliDate.startOfDay(dayMillis) > System.currentTimeMillis()) break
            val stats = getDailyStats(dayMillis)
            dailyWater.add(stats.waterVolume)
            if (stats.totalCaffeine > caffeineLimit) daysOverLimit++
            if (stats.totalDrinks > 0) {
                val label = JalaliDate.formatDayLabel(dayMillis)
                if (bestDay == null || stats.waterVolume > bestDay.second) {
                    bestDay = label to stats.waterVolume
                }
                if (attentionDay == null || stats.waterVolume < attentionDay.second) {
                    attentionDay = label to stats.waterVolume
                }
            }
        }

        val prevStart = Calendar.getInstance().apply {
            timeInMillis = start
            add(Calendar.MONTH, -1)
        }.timeInMillis
        val prevLogs = consumptionRepository.getBetween(
            JalaliDate.startOfMonth(prevStart),
            JalaliDate.endOfMonth(prevStart)
        )
        val water = logs.filter { it.drinkName.contains("آب") }.sumOf { it.volume }
        val prevWater = prevLogs.filter { it.drinkName.contains("آب") }.sumOf { it.volume }
        val caffeine = if (logs.isNotEmpty()) logs.sumOf { it.caffeine } / maxOf(1, dailyWater.size) else 0
        val prevCaffeine = if (prevLogs.isNotEmpty()) {
            prevLogs.sumOf { it.caffeine } / 30
        } else 0

        val tip = when {
            daysOverLimit > 5 -> "در چند روز از حد کافئین عبور کرده‌اید؛ عصرها دمنوش را امتحان کنید."
            water < caffeineLimit * 10 -> "مصرف آب ماهانه کمتر از انتظار است؛ یادآوری آب را فعال نگه دارید."
            else -> "الگوی ماهانه شما نسبتاً متعادل است. همین روال را ادامه دهید."
        }

        return MonthlyStats(
            totalDrinks = logs.size,
            totalCoffeeCups = logs.count { it.drinkName.contains("قهوه") || it.drinkName.contains("اسپرسو") || it.drinkName.contains("لاته") || it.drinkName.contains("کاپوچینو") },
            totalTeaCups = logs.count { it.drinkName.contains("چای") },
            totalDecafDrinks = logs.count { it.caffeine == 0 },
            totalWaterMl = water,
            avgDailyCaffeine = caffeine,
            daysOverLimit = daysOverLimit,
            bestDayLabel = bestDay?.first,
            attentionDayLabel = attentionDay?.first,
            waterChangePercent = if (prevWater > 0) ((water - prevWater) * 100 / prevWater) else 0,
            caffeineChangePercent = if (prevCaffeine > 0) ((caffeine - prevCaffeine) * 100 / prevCaffeine) else 0,
            habitTip = tip,
            dailyWater = dailyWater
        )
    }

    suspend fun getYearlyStats(acceptedSuggestions: Int = 0): YearlyStats {
        val start = JalaliDate.startOfYear()
        val now = System.currentTimeMillis()
        val logs = consumptionRepository.getBetween(start, now)
        if (logs.isEmpty()) return YearlyStats(acceptedSuggestions = acceptedSuggestions)

        val popular = logs.groupBy { it.drinkName }.maxByOrNull { it.value.size }?.key
        val popularHour = logs.groupBy {
            java.util.Calendar.getInstance().apply { timeInMillis = it.dateTimeMillis }
                .get(java.util.Calendar.HOUR_OF_DAY)
        }.maxByOrNull { it.value.size }?.key?.let { String.format("%02d:00", it) }

        val busiestWeekday = logs.groupBy { JalaliDate.weekdayName(it.dateTimeMillis) }
            .maxByOrNull { it.value.size }?.key

        val busiestMonth = logs.groupBy {
            val j = JalaliDate.fromMillis(it.dateTimeMillis)
            JalaliDate.monthName(j.month)
        }.maxByOrNull { it.value.size }?.key

        val loggedDays = logs.map { JalaliDate.startOfDay(it.dateTimeMillis) }.toSet().size
        val water = logs.filter { it.drinkName.contains("آب") }.sumOf { it.volume }

        return YearlyStats(
            totalDrinks = logs.size,
            totalCoffeeCups = logs.count {
                it.drinkName.contains("قهوه") || it.drinkName.contains("اسپرسو") ||
                    it.drinkName.contains("لاته") || it.drinkName.contains("کاپوچینو")
            },
            totalTeaCups = logs.count { it.drinkName.contains("چای") },
            totalWaterMl = water,
            avgDailyCaffeine = if (loggedDays > 0) logs.sumOf { it.caffeine } / loggedDays else 0,
            popularDrink = popular,
            popularHour = popularHour,
            busiestWeekday = busiestWeekday,
            busiestMonth = busiestMonth,
            loggedDays = loggedDays,
            acceptedSuggestions = acceptedSuggestions
        )
    }

    suspend fun getCalendarDays(
        monthMillis: Long = System.currentTimeMillis(),
        waterGoal: Int = 2450,
        caffeineLimit: Int = 400
    ): List<CalendarDay> {
        val days = JalaliDate.daysInMonth(monthMillis)
        val result = mutableListOf<CalendarDay>()
        val todayStart = JalaliDate.startOfDay()
        for (d in 1..days) {
            val dayMillis = JalaliDate.dayMillisInMonth(d, monthMillis)
            val dayStart = JalaliDate.startOfDay(dayMillis)
            if (dayStart > todayStart) {
                result.add(CalendarDay(d, dayMillis, DayStatus.NO_DATA))
                continue
            }
            val stats = getDailyStats(dayMillis)
            val status = when {
                stats.totalDrinks == 0 -> DayStatus.NO_DATA
                stats.totalCaffeine > caffeineLimit -> DayStatus.HIGH_CAFFEINE
                stats.waterVolume < waterGoal / 2 -> DayStatus.LOW_WATER
                stats.waterVolume >= waterGoal * 0.8 && stats.totalCaffeine <= caffeineLimit -> DayStatus.COMPLETE
                else -> DayStatus.BALANCED
            }
            result.add(
                CalendarDay(
                    dayOfMonth = d,
                    millis = dayMillis,
                    status = status,
                    waterMl = stats.waterVolume,
                    caffeineMg = stats.totalCaffeine,
                    drinkCount = stats.totalDrinks
                )
            )
        }
        return result
    }
}
