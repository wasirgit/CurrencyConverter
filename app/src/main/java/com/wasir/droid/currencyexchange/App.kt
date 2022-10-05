package com.wasir.droid.currencyexchange

import android.app.Application
import com.wasir.droid.currencyexchange.data.database.entity.ConfigEntity
import com.wasir.droid.currencyexchange.domain.repository.ConfigurationRepo
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {
    private val TAG = "AppClass"
    private lateinit var config: ConfigEntity
    private lateinit var job: Job

    @Inject
    lateinit var configurationRepo: ConfigurationRepo
    override fun onCreate() {
        super.onCreate()
    }

    fun loadConfiguration() {
        CoroutineScope(SupervisorJob())
            .launch {
                configurationRepo.loadConfiguration().collect {
                    it.data?.let {
                        config = it
                    }
                }
            }
    }

    fun getConfiguration(): ConfigEntity {
        return config
    }
}