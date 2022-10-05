package com.wasir.droid.currencyexchange.data.deserializer

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.wasir.droid.currencyexchange.data.model.CurrencyRateResponse
import java.lang.reflect.Type

class RateApiDeserializer : JsonDeserializer<CurrencyRateResponse> {
    private val TAG = "RateApiDeserializer"
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): CurrencyRateResponse {
        val jsonObject: JsonObject? = json?.asJsonObject
        val success: Boolean = jsonObject?.get("success")?.asBoolean ?: false
        val base: String = jsonObject?.get("base")?.asString ?: ""
        val date: String = jsonObject?.get("date")?.asString ?: ""
        jsonObject?.let {
            val parameters: HashMap<String, Double> = readDataFromMap(it)
            val response = CurrencyRateResponse()
            response.success = success
            response.base = base
            response.date = date
            response.rates = parameters
            return response
        }


        return CurrencyRateResponse()
    }

    private fun readDataFromMap(jsonObject: JsonObject?): HashMap<String, Double> {
        val parameters: HashMap<String, Double> = HashMap()
        jsonObject?.let {
            val paramsElement: JsonElement = it["rates"]

            val parametersObject = paramsElement.asJsonObject


            parametersObject.entrySet().forEach { entry ->
                parameters.put(entry.key, entry.value.asDouble)
            }
        }
        return parameters
    }
}