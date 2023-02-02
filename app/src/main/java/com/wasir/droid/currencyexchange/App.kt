package com.wasir.droid.currencyexchange

import android.app.Activity
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.wasir.droid.currencyexchange.data.scheduler.AppConfigSync
import com.wasir.droid.currencyexchange.common.ConnectionUtil
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), Application.ActivityLifecycleCallbacks {
    private val TAG = "AppClass"
    private var isNetworkConnected = false

    @Inject
    lateinit var appConfigSync: AppConfigSync
    private var connectivityManager: ConnectivityManager? = null
    override fun onCreate() {
        super.onCreate()
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        registerActivityLifecycleCallbacks(this)
        appConfigSync.loadConfiguration()
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {

    }

    override fun onActivityStarted(p0: Activity) {

    }

    override fun onActivityResumed(p0: Activity) {
        registerNetworkMonitor()
        if (isNetworkConnected) {
            appConfigSync.startSync()
        }

    }

    override fun onActivityPaused(p0: Activity) {
        appConfigSync.stopSync()
        unregisterNetworkMonitor()
    }

    override fun onActivityStopped(p0: Activity) {
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {

    }

    override fun onActivityDestroyed(p0: Activity) {

    }

    private fun registerNetworkMonitor() {
        connectivityManager?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                registerAPI24AndAbove(it)
            } else {
                registerAPI24Below(it)
            }
        }
    }

    private fun unregisterNetworkMonitor() {
        connectivityManager?.unregisterNetworkCallback(registerDefaultNetworkCallback)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun registerAPI24AndAbove(connectivityManager: ConnectivityManager) {
        connectivityManager.registerDefaultNetworkCallback(registerDefaultNetworkCallback)
    }

    private fun registerAPI24Below(connectivityManager: ConnectivityManager) {
        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, registerDefaultNetworkCallback)
    }

    private val registerDefaultNetworkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            if(ConnectionUtil.isNetworkAvailable(this@App)){
                isNetworkConnected = true
                appConfigSync.startSync()
            }
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            isNetworkConnected = false
            appConfigSync.stopSync()
        }
    }
}