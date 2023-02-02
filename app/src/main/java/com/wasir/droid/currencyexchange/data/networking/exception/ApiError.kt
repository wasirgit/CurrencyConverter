package com.wasir.droid.currencyexchange.data.networking.exception

import android.content.Context
import com.wasir.droid.currencyexchange.R
import com.wasir.droid.currencyexchange.common.ConnectionUtil.getNoConnectionMessage
import com.wasir.droid.currencyexchange.common.ConnectionUtil.isNetworkAvailable
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

class ApiError(
    private val context: Context,
    private val statusCode: Int = 0,
    private val message: String? = null,
    private val errorDescription: String? = null,
    private val errorCodeKey: String? = null
)  {

    fun auditError(e: Exception): String {
        return auditNetworkFailure(e)
    }

    private fun auditNetworkFailure(e: Exception): String {
        if (!isNetworkAvailable(context)) return getNoConnectionMessage(context)
        else if (e is SocketException) return getNoConnectionMessage(context)
        else if (e is SocketTimeoutException) return getNoConnectionMessage(context)
        else if (e is SSLException) return getNoConnectionMessage(context)
        else if (e is UnknownHostException) return context.getString(R.string.connection_time_out)
        else {
            return context.getString(R.string.something_went_wrong)
        }
    }
}