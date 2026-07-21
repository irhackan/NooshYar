package com.nooshyar.app.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.nooshyar.app.R
import com.nooshyar.app.data.repository.ConsumptionRepository
import com.nooshyar.app.data.repository.NotificationRepository
import com.nooshyar.app.data.repository.UserRepository
import com.nooshyar.app.domain.engine.CaffeineCalculator
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

object NotificationChannels {
    const val WATER = "water_reminder"
    const val CAFFEINE = "caffeine_alert"
    const val DAILY = "daily_summary"

    fun create(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(NotificationManager::class.java)
            listOf(
                Triple(WATER, R.string.notif_channel_water, NotificationManager.IMPORTANCE_DEFAULT),
                Triple(CAFFEINE, R.string.notif_channel_caffeine, NotificationManager.IMPORTANCE_HIGH),
                Triple(DAILY, R.string.notif_channel_daily, NotificationManager.IMPORTANCE_LOW)
            ).forEach { (id, nameRes, importance) ->
                manager.createNotificationChannel(
                    NotificationChannel(id, context.getString(nameRes), importance)
                )
            }
        }
    }
}

object NotificationScheduler {
    fun schedule(context: Context) {
        NotificationChannels.create(context)
        val request = PeriodicWorkRequestBuilder<NooshYarNotificationWorker>(4, TimeUnit.HOURS)
            .setConstraints(Constraints.Builder().build())
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "nooshyar_notifications",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}

@HiltWorker
class NooshYarNotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val consumptionRepo: ConsumptionRepository,
    private val userRepo: UserRepository,
    private val notificationRepo: NotificationRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val profile = userRepo.getProfile() ?: return Result.success()
        val todayLogs = consumptionRepo.getToday()
        val now = System.currentTimeMillis()

        val waterSettings = notificationRepo.get("water")
        if (waterSettings?.enabled == true) {
            val lastDrink = todayLogs.maxByOrNull { it.dateTimeMillis }
            val hoursSince = lastDrink?.let { (now - it.dateTimeMillis) / (1000f * 60 * 60) } ?: 24f
            if (hoursSince >= 3) {
                showNotification(NotificationChannels.WATER, applicationContext.getString(R.string.notif_water_title), applicationContext.getString(R.string.notif_water_body))
            }
        }

        val caffeineSettings = notificationRepo.get("caffeine")
        if (caffeineSettings?.enabled == true) {
            val active = CaffeineCalculator.calculateActiveCaffeine(todayLogs, now, profile.caffeineHalfLifeHours)
            val hoursToSleep = com.nooshyar.app.core.util.JalaliDate.minutesUntilSleep(now, profile.sleepTimeMinutes)
            if (hoursToSleep < 5 && active > 50) {
                showNotification(NotificationChannels.CAFFEINE, applicationContext.getString(R.string.notif_caffeine_title), applicationContext.getString(R.string.notif_caffeine_body))
            }
        }

        return Result.success()
    }

    private fun showNotification(channelId: String, title: String, body: String) {
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        NotificationManagerCompat.from(applicationContext).notify(channelId.hashCode(), notification)
    }
}
