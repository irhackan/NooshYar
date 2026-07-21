package com.nooshyar.app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.google.gson.Gson
import com.nooshyar.app.data.local.DrinkSeedData
import com.nooshyar.app.data.local.NooshYarDatabase
import com.nooshyar.app.data.local.dao.*
import com.nooshyar.app.data.repository.NotificationRepository
import com.nooshyar.app.domain.engine.RecommendationEngine
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "nooshyar_prefs")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): NooshYarDatabase {
        val db = Room.databaseBuilder(context, NooshYarDatabase::class.java, "nooshyar.db").build()
        CoroutineScope(Dispatchers.IO).launch {
            val drinkDao = db.drinkDao()
            if (drinkDao.getAll().isEmpty()) {
                drinkDao.insertAll(DrinkSeedData.defaultDrinks())
            }
            val notifDao = db.notificationSettingsDao()
            if (notifDao.get("water") == null) {
                NotificationRepository(notifDao).initDefaults()
            }
        }
        return db
    }

    @Provides fun provideUserDao(db: NooshYarDatabase): UserDao = db.userDao()
    @Provides fun provideDrinkDao(db: NooshYarDatabase): DrinkDao = db.drinkDao()
    @Provides fun provideConsumptionDao(db: NooshYarDatabase): ConsumptionDao = db.consumptionDao()
    @Provides fun provideSuggestionDao(db: NooshYarDatabase): SuggestionDao = db.suggestionDao()
    @Provides fun provideNotificationDao(db: NooshYarDatabase): NotificationSettingsDao = db.notificationSettingsDao()

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> = context.dataStore

    @Provides
    @Singleton
    fun provideRecommendationEngine(): RecommendationEngine = RecommendationEngine()
}
