package com.nooshyar.app.data.repository

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.nooshyar.app.data.local.dao.ConsumptionDao
import com.nooshyar.app.data.local.dao.UserDao
import com.nooshyar.app.data.mapper.Mappers.toDomain
import com.nooshyar.app.data.mapper.Mappers.toEntity
import com.nooshyar.app.domain.model.ConsumptionLog
import com.nooshyar.app.domain.model.UserProfile
import javax.inject.Inject
import javax.inject.Singleton

data class BackupPayload(
    @SerializedName("version") val version: Int = 1,
    @SerializedName("exportedAt") val exportedAt: Long = System.currentTimeMillis(),
    @SerializedName("app") val app: String = "NooshYar",
    @SerializedName("developer") val developer: String = "کاظم دهناد",
    @SerializedName("profile") val profile: UserProfile?,
    @SerializedName("consumptions") val consumptions: List<ConsumptionLog>
)

@Singleton
class BackupRepository @Inject constructor(
    private val userDao: UserDao,
    private val consumptionDao: ConsumptionDao,
    private val gson: Gson
) {
    suspend fun exportJson(): String {
        val profile = userDao.getUser()?.toDomain()
        val logs = consumptionDao.getAll().map { it.toDomain() }
        return gson.toJson(
            BackupPayload(
                profile = profile,
                consumptions = logs
            )
        )
    }

    suspend fun importJson(json: String): Result<Int> = runCatching {
        val payload = gson.fromJson(json, BackupPayload::class.java)
            ?: error("فایل پشتیبان نامعتبر است")
        payload.profile?.let { userDao.insert(it.toEntity()) }
        var count = 0
        payload.consumptions.forEach { log ->
            consumptionDao.insert(log.copy(id = 0).toEntity())
            count++
        }
        count
    }
}
