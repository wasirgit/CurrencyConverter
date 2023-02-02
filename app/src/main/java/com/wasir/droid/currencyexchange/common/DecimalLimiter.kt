package com.wasir.droid.currencyexchange.common

import android.text.InputFilter
import android.text.Spanned
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import java.util.regex.Pattern

class DecimalLimiter :InputFilter{
    var mPattern: Pattern? = null
    private var df: DecimalFormat
    init {
        val nf = NumberFormat.getCurrencyInstance(Locale.ENGLISH)
        df = (nf as DecimalFormat)
        val symbols = df.decimalFormatSymbols
        symbols.currencySymbol = ""
        df.decimalFormatSymbols = symbols
        mPattern = if (df.maximumFractionDigits == 0) {
            Pattern.compile("\\d*") // no decimal point
        } else {
            Pattern.compile("\\d*((\\" +  df.decimalFormatSymbols.decimalSeparator + "\\d{0," + df.maximumFractionDigits + "}?)?)||(\\" + df.decimalFormatSymbols.decimalSeparator + ")?")
        }
    }

    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        val sb: StringBuilder = StringBuilder(dest)
        sb.insert(dstart, source, start, end)

        val matcher = mPattern!!.matcher(sb.toString())
        return if (!matcher.matches()) {
            ""
        } else null
    }
}