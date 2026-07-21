package com.nooshyar.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.nooshyar.app.notification.NotificationScheduler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class NooshYarApplication : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        NotificationScheduler.schedule(this)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
