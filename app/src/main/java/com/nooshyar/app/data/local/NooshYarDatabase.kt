package com.nooshyar.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nooshyar.app.data.local.dao.*
import com.nooshyar.app.data.local.entity.*

@Database(
    entities = [
        UserEntity::class,
        DrinkEntity::class,
        ConsumptionEntity::class,
        SuggestionEntity::class,
        NotificationSettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class NooshYarDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun drinkDao(): DrinkDao
    abstract fun consumptionDao(): ConsumptionDao
    abstract fun suggestionDao(): SuggestionDao
    abstract fun notificationSettingsDao(): NotificationSettingsDao
}
