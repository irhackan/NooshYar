package com.nooshyar.app.data.local.dao

import androidx.room.*
import com.nooshyar.app.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = 1 LIMIT 1")
    fun observeUser(): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = 1 LIMIT 1")
    suspend fun getUser(): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity)

    @Query("DELETE FROM users")
    suspend fun deleteAll()
}

@Dao
interface DrinkDao {
    @Query("SELECT * FROM drinks ORDER BY nameFa ASC")
    fun observeAll(): Flow<List<DrinkEntity>>

    @Query("SELECT * FROM drinks ORDER BY nameFa ASC")
    suspend fun getAll(): List<DrinkEntity>

    @Query("SELECT * FROM drinks WHERE id = :id")
    suspend fun getById(id: Long): DrinkEntity?

    @Query("SELECT * FROM drinks WHERE nameFa LIKE '%' || :query || '%' OR nameEn LIKE '%' || :query || '%'")
    suspend fun search(query: String): List<DrinkEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(drinks: List<DrinkEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(drink: DrinkEntity): Long

    @Query("DELETE FROM drinks WHERE isCustom = 1")
    suspend fun deleteCustom()
}

@Dao
interface ConsumptionDao {
    @Query("SELECT * FROM consumptions WHERE dateTime BETWEEN :start AND :end ORDER BY dateTime DESC")
    fun observeBetween(start: Long, end: Long): Flow<List<ConsumptionEntity>>

    @Query("SELECT * FROM consumptions WHERE dateTime BETWEEN :start AND :end ORDER BY dateTime DESC")
    suspend fun getBetween(start: Long, end: Long): List<ConsumptionEntity>

    @Query("SELECT * FROM consumptions ORDER BY dateTime DESC LIMIT :limit")
    suspend fun getRecent(limit: Int): List<ConsumptionEntity>

    @Query("SELECT drinkId, COUNT(*) as cnt FROM consumptions GROUP BY drinkId ORDER BY cnt DESC LIMIT :limit")
    suspend fun getPopularDrinkIds(limit: Int): List<PopularDrink>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(consumption: ConsumptionEntity): Long

    @Update
    suspend fun update(consumption: ConsumptionEntity)

    @Delete
    suspend fun delete(consumption: ConsumptionEntity)

    @Query("SELECT * FROM consumptions ORDER BY dateTime DESC")
    suspend fun getAll(): List<ConsumptionEntity>

    @Query("DELETE FROM consumptions")
    suspend fun deleteAll()
}

data class PopularDrink(val drinkId: Long, val cnt: Int)

@Dao
interface SuggestionDao {
    @Insert
    suspend fun insert(suggestion: SuggestionEntity): Long

    @Query("UPDATE suggestions SET userResponse = :response, acceptedAt = :acceptedAt WHERE id = :id")
    suspend fun updateResponse(id: Long, response: String, acceptedAt: Long?)

    @Query("SELECT * FROM suggestions WHERE userResponse IS NOT NULL")
    suspend fun getResponded(): List<SuggestionEntity>

    @Query("SELECT COUNT(*) FROM suggestions WHERE userResponse = 'ACCEPTED'")
    suspend fun countAccepted(): Int

    @Query("DELETE FROM suggestions")
    suspend fun deleteAll()
}

@Dao
interface NotificationSettingsDao {
    @Query("SELECT * FROM notification_settings")
    fun observeAll(): Flow<List<NotificationSettingsEntity>>

    @Query("SELECT * FROM notification_settings WHERE notificationType = :type")
    suspend fun get(type: String): NotificationSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(settings: NotificationSettingsEntity)

    @Query("DELETE FROM notification_settings")
    suspend fun deleteAll()
}
