package com.wasir.droid.currencyexchange

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.wasir.droid.currencyexchange.data.scheduler.AppConfigSync
import com.wasir.droid.currencyexchange.domain.repository.ConfigurationRepo
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), Application.ActivityLifecycleCallbacks {
    private val TAG = "AppClass"

    @Inject
    lateinit var configurationRepo: ConfigurationRepo

    @Inject
    lateinit var appConfigSync: AppConfigSync

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(this)
        appConfigSync.loadConfiguration()
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {

    }

    override fun onActivityStarted(p0: Activity) {

    }

    override fun onActivityResumed(p0: Activity) {
        appConfigSync.startSync()
    }

    override fun onActivityPaused(p0: Activity) {
        appConfigSync.stopSync()
    }

    override fun onActivityStopped(p0: Activity) {
        appConfigSync.stopSync()
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {

    }

    override fun onActivityDestroyed(p0: Activity) {

    }
}