package com.nooshyar.app.data.mapper

import com.nooshyar.app.data.local.entity.*
import com.nooshyar.app.domain.model.*

object Mappers {

    fun UserEntity.toDomain(): UserProfile = UserProfile(
        id = id,
        name = name,
        age = age,
        weight = weight,
        height = height,
        wakeTimeMinutes = wakeTimeMinutes,
        sleepTimeMinutes = sleepTimeMinutes,
        workStartMinutes = workStartMinutes,
        workEndMinutes = workEndMinutes,
        activityLevel = ActivityLevel.valueOf(activityLevel),
        workPressure = PressureLevel.valueOf(workPressure),
        caffeineSensitivity = CaffeineSensitivity.valueOf(caffeineSensitivity),
        caffeineDailyLimit = caffeineDailyLimit,
        waterDailyGoal = waterDailyGoal,
        caffeineHalfLifeHours = caffeineHalfLifeHours,
        selectedGoals = selectedGoals.split(",").filter { it.isNotBlank() }.map { UserGoal.valueOf(it) }.toSet(),
        likedDrinkIds = likedDrinkIds.split(",").filter { it.isNotBlank() }.map { it.toLong() }.toSet(),
        dislikedDrinkIds = dislikedDrinkIds.split(",").filter { it.isNotBlank() }.map { it.toLong() }.toSet()
    )

    fun UserProfile.toEntity(now: Long = System.currentTimeMillis()): UserEntity = UserEntity(
        id = id,
        name = name,
        age = age,
        weight = weight,
        height = height,
        wakeTimeMinutes = wakeTimeMinutes,
        sleepTimeMinutes = sleepTimeMinutes,
        workStartMinutes = workStartMinutes,
        workEndMinutes = workEndMinutes,
        activityLevel = activityLevel.name,
        workPressure = workPressure.name,
        caffeineSensitivity = caffeineSensitivity.name,
        caffeineDailyLimit = caffeineDailyLimit,
        waterDailyGoal = waterDailyGoal,
        caffeineHalfLifeHours = caffeineHalfLifeHours,
        selectedGoals = selectedGoals.joinToString(",") { it.name },
        likedDrinkIds = likedDrinkIds.joinToString(","),
        dislikedDrinkIds = dislikedDrinkIds.joinToString(","),
        createdAt = now,
        updatedAt = now
    )

    fun DrinkEntity.toDomain(): Drink = Drink(
        id = id,
        nameFa = nameFa,
        nameEn = nameEn,
        category = category,
        defaultVolume = defaultVolume,
        caffeinePerServing = caffeinePerServing,
        sugarPerServing = sugarPerServing,
        caloriesPerServing = caloriesPerServing,
        temperatureType = TemperatureType.valueOf(temperatureType),
        isCaffeinated = isCaffeinated,
        icon = icon,
        description = description,
        suitableMorning = suitableMorning,
        suitableNoon = suitableNoon,
        suitableEvening = suitableEvening,
        suitableNight = suitableNight,
        isCustom = isCustom
    )

    fun Drink.toEntity(now: Long = System.currentTimeMillis()): DrinkEntity = DrinkEntity(
        id = id,
        nameFa = nameFa,
        nameEn = nameEn,
        category = category,
        defaultVolume = defaultVolume,
        caffeinePerServing = caffeinePerServing,
        sugarPerServing = sugarPerServing,
        caloriesPerServing = caloriesPerServing,
        temperatureType = temperatureType.name,
        isCaffeinated = isCaffeinated,
        icon = icon,
        description = description,
        suitableMorning = suitableMorning,
        suitableNoon = suitableNoon,
        suitableEvening = suitableEvening,
        suitableNight = suitableNight,
        isCustom = isCustom,
        createdAt = now
    )

    fun ConsumptionEntity.toDomain(): ConsumptionLog = ConsumptionLog(
        id = id,
        userId = userId,
        drinkId = drinkId,
        drinkName = drinkName,
        dateTimeMillis = dateTime,
        volume = volume,
        servings = servings,
        caffeine = caffeine,
        sugar = sugar,
        calories = calories,
        mood = mood?.let { ConsumptionMood.entries.find { m -> m.key == it } },
        activity = activity?.let { UserActivity.entries.find { a -> a.key == it } },
        note = note,
        wasSuggested = wasSuggested,
        suggestionId = suggestionId
    )

    fun ConsumptionLog.toEntity(): ConsumptionEntity = ConsumptionEntity(
        id = id,
        userId = userId,
        drinkId = drinkId,
        drinkName = drinkName,
        dateTime = dateTimeMillis,
        volume = volume,
        servings = servings,
        caffeine = caffeine,
        sugar = sugar,
        calories = calories,
        mood = mood?.key,
        activity = activity?.key,
        note = note,
        wasSuggested = wasSuggested,
        suggestionId = suggestionId
    )
}
