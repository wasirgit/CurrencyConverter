package com.wasir.droid.currencyexchange.data.model

import com.google.gson.annotations.SerializedName
import com.wasir.droid.currencyexchange.data.database.entity.CurrencyRateEntity

data class CurrencyRateResponse(
    @SerializedName("success") var success: Boolean? = null,
    @SerializedName("base") var base: String? = null,
    @SerializedName("date") var date: String? = null,
    @SerializedName("rates") var rates: Map<String, Double>? = null
) {
    fun toCurrencyRateEntity(): CurrencyRateEntity? {
        base?.let { _base ->
            rates?.let { _rates ->
                return CurrencyRateEntity(base = _base, rates = _rates)
            }
        }

        return null
    }
}