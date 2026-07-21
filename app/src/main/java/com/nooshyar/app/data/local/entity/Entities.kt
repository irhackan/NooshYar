package com.nooshyar.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: Long = 1L,
    val name: String,
    val age: Int,
    val weight: Float,
    val height: Float?,
    val wakeTimeMinutes: Int,
    val sleepTimeMinutes: Int,
    val workStartMinutes: Int,
    val workEndMinutes: Int,
    val activityLevel: String,
    val workPressure: String,
    val caffeineSensitivity: String,
    val caffeineDailyLimit: Int,
    val waterDailyGoal: Int,
    val caffeineHalfLifeHours: Float,
    val selectedGoals: String,
    val likedDrinkIds: String,
    val dislikedDrinkIds: String,
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(tableName = "drinks")
data class DrinkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nameFa: String,
    val nameEn: String,
    val category: String,
    val defaultVolume: Int,
    val caffeinePerServing: Int,
    val sugarPerServing: Float,
    val caloriesPerServing: Int,
    val temperatureType: String,
    val isCaffeinated: Boolean,
    val icon: String,
    val description: String,
    val suitableMorning: Boolean,
    val suitableNoon: Boolean,
    val suitableEvening: Boolean,
    val suitableNight: Boolean,
    val isCustom: Boolean,
    val createdAt: Long
)

@Entity(tableName = "consumptions")
data class ConsumptionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val drinkId: Long,
    val drinkName: String,
    val dateTime: Long,
    val volume: Int,
    val servings: Int,
    val caffeine: Int,
    val sugar: Float,
    val calories: Int,
    val mood: String?,
    val activity: String?,
    val note: String?,
    val wasSuggested: Boolean,
    val suggestionId: Long?
)

@Entity(tableName = "suggestions")
data class SuggestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val dateTime: Long,
    val suggestedDrinkId: Long,
    val score: Int,
    val reason: String,
    val userResponse: String?,
    val acceptedAt: Long?
)

@Entity(tableName = "notification_settings")
data class NotificationSettingsEntity(
    @PrimaryKey val notificationType: String,
    val enabled: Boolean,
    val startTimeMinutes: Int,
    val endTimeMinutes: Int,
    val dailyLimit: Int,
    val silenceStartMinutes: Int?,
    val silenceEndMinutes: Int?
)
