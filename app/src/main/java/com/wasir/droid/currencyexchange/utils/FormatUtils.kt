package com.wasir.droid.currencyexchange.utils

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class FormatUtils {
    private var df: DecimalFormat

    init {
        val nf = NumberFormat.getCurrencyInstance(Locale.ENGLISH)
        df = nf as DecimalFormat
        val symbols = df.decimalFormatSymbols
        symbols.currencySymbol = ""
        df.decimalFormatSymbols = symbols
    }

    fun formatAmountWithSign(amount: Double): String {
        return if (amount >= 0) "+" + df.format(amount)
            .trim { it <= ' ' } else "-" + df.format(-amount).trim { it <= ' ' }
    }

    fun formatAmountWithOutSign(amount: Double): String {
        return df.format(amount)
    }
}