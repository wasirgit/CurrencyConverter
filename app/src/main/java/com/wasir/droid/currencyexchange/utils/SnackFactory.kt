package com.wasir.droid.currencyexchange.utils

import android.graphics.Color
import android.view.View
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.wasir.droid.currencyexchange.R

object SnackFactory {
    fun showError(attach: View?, message: String) {
        attach?.let {
            val snack = Snackbar.make(attach, message, Snackbar.LENGTH_LONG)
            val view = snack.view
            view.setBackgroundResource(R.color.red)
            val tv = view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
            tv.setTextColor(Color.WHITE)
            tv.maxLines = 3
            snack.show()
        }

    }

    fun showMessage(attach: View?, message: String) {
        attach?.let {
            val snack = Snackbar.make(attach, message, Snackbar.LENGTH_LONG)
            val view = snack.view
            view.setBackgroundResource(R.color.colorAccent)
            val tv = view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
            tv.setTextColor(Color.WHITE)
            tv.maxLines = 3
            snack.show()
        }

    }
}