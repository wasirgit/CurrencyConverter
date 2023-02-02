package com.wasir.droid.currencyexchange.data.scheduler

import android.os.Handler
import android.os.Looper
import com.wasir.droid.currencyexchange.data.database.entity.ConfigEntity
import com.wasir.droid.currencyexchange.data.database.entity.CurrencyRateEntity
import com.wasir.droid.currencyexchange.domain.repository.ConfigurationRepo
import kotlinx.coroutines.*
import javax.inject.Inject

class AppConfigSync @Inject constructor(private val configurationRepo: ConfigurationRepo) {
    private val TAG = "CurrencyRateSyncSchedul"
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var rateSyncJob: Job? = null
    private var loadConfigJob: Job? = null
    val latestRateSyncHandler = Handler(Looper.getMainLooper())
    private var config: ConfigEntity? = null
    private var currencyRateEntity: CurrencyRateEntity? = null
    var defaultSyncTimeInSec: Long = 5

    fun startSync() {
        latestRateSyncHandler.post(rateSyncRunnable)
    }

    fun stopSync() {
        rateSyncJob?.cancel()
        latestRateSyncHandler.removeCallbacks(rateSyncRunnable)
    }

    fun updateConfig(configEntity: ConfigEntity) {
        this.config = configEntity
    }

    fun getConfig(): ConfigEntity? = config
    fun getCurrencyExchangeRate() = currencyRateEntity

    private val rateSyncRunnable = object : Runnable {
        override fun run() {
            makeRequestAndSyncDb()
            val time: Long
            if (config == null || config!!.syncTime < defaultSyncTimeInSec) {
                time = defaultSyncTimeInSec * 1000
            } else {
                time = config!!.syncTime.toLong() * 1000
            }
            latestRateSyncHandler.postDelayed(this, time)
        }
    }

    fun loadConfiguration() {
        loadConfigJob?.cancel()
        loadConfigJob = applicationScope
            .launch {
                configurationRepo.loadConfiguration()
                    .collect {
                        it.data?.let {
                            config = it
                        }
                    }
            }
    }

    private fun makeRequestAndSyncDb() {
        rateSyncJob?.cancel()
        rateSyncJob = applicationScope
            .launch {
                configurationRepo.getRateByBaseSymbol("EUR")
                    .collect {
                        currencyRateEntity = it.data
                    }
            }
    }
}