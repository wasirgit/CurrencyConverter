package com.wasir.droid.currencyexchange.data.repository

import com.wasir.droid.currencyexchange.common.FormatUtils
import com.wasir.droid.currencyexchange.common.Resource
import com.wasir.droid.currencyexchange.data.database.dao.RoomDatabaseDao
import com.wasir.droid.currencyexchange.data.database.entity.ConfigEntity
import com.wasir.droid.currencyexchange.data.database.entity.CurrencyRateEntity
import com.wasir.droid.currencyexchange.domain.repository.ExchangeRateRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ExchangeRateRepoImpl @Inject constructor(
    private val dao: RoomDatabaseDao,
    private val formatUtils: FormatUtils,
) :
    ExchangeRateRepo {
    private val TAG = "ExchangeRateRepoImpl"


    override suspend fun calculateReceiverAmount(
        sellAmount: Double,
        base: String,
        symbols: String
    ): Flow<Resource<Double>> =
        flow {
            emit(Resource.Loading())
            try {
                val currencyRate = dao.getCurrencyRate()
                if (base != currencyRate.base && !currencyRate.rates.containsKey(base)) {
                    emit(Resource.Error("Please provide valid sell currency"))
                } else if (symbols != currencyRate.base && !currencyRate.rates.containsKey(symbols)) {
                    emit(Resource.Error("Please provide valid receiver currency"))
                } else {
                    val exchangeRate = getExchangeRate(base, symbols, currencyRate)
                    emit(Resource.Success(sellAmount * exchangeRate))
                }

            } catch (e: Exception) {
                emit(Resource.Error(e.localizedMessage))
            }
        }

    override suspend fun convertCurrency(
        sellAmount: Double,
        sellCurrency: String,
        receiveCurrency: String
    ): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        if (sellAmount <= 0) {
            emit(Resource.Error("Amount must be greater then 0"))
        } else {
            try {
                val config = dao.getConfig()
                val currencyRate = dao.getCurrencyRate()
                val exchangeRate = getExchangeRate(sellCurrency, receiveCurrency, currencyRate)
                val sellAccounts = dao.getAccountByCurrency(sellCurrency)
                val receiveAccount = dao.getAccountByCurrency(receiveCurrency)
                val convertedAmount = sellAmount * exchangeRate

                val isConversionFree: Boolean = isConversionFree(sellAmount, config);

                var sellerCommission: Double = 0.toDouble()
                var receiverCommission: Double = 0.toDouble()
                if (!isConversionFree) {
                    sellerCommission = getCommission(sellAmount, config)
                    receiverCommission = getCommission(convertedAmount, config)
                }
                val accBalanceAfterSell =
                    sellAccounts.balance - (sellAmount + sellerCommission)
                val accBalanceAfterReceive =
                    receiveAccount.balance + (convertedAmount - receiverCommission)
                if (accBalanceAfterSell < 0) {
                    emit(Resource.Error("Balance can't fall below zero"))
                } else {
                    dao.updateAccBalanceByCurrencyCode(sellCurrency, accBalanceAfterSell)
                    dao.updateAccBalanceByCurrencyCode(receiveCurrency, accBalanceAfterReceive)
                    val message =
                        generateSuccessMessage(
                            sellAmount,
                            sellCurrency,
                            convertedAmount,
                            receiveCurrency,
                            isConversionFree,
                            config
                        )
                    config.total_convert = config.total_convert + 1
                    dao.updateConfig(config)
                    emit(Resource.Success(message))
                }

            } catch (e: Exception) {
                emit(Resource.Error(e.localizedMessage))
            }
        }
    }

    private fun getCommission(amount: Double, config: ConfigEntity): Double {
        return (config.commission * amount) / 100
    }

    private fun generateSuccessMessage(
        sellAmount: Double,
        base: String,
        convertedAmount: Double,
        symbol: String,
        isConversionFree: Boolean,
        config: ConfigEntity
    ): String {
        val convertMessage =
            "You have converted ${formatUtils.formatAmountWithOutSign(sellAmount)} $base to ${
                formatUtils.formatAmountWithOutSign(
                    convertedAmount
                )
            } $symbol"

        if (!isConversionFree) {
            return convertMessage + "  Commission Fee - ${
                formatUtils.formatAmountWithOutSign(
                    config.commission
                )
            } %"
        }
        return convertMessage
    }

    private fun getExchangeRate(
        base: String,
        symbols: String,
        currencyExchangeRate: CurrencyRateEntity
    ): Double {
        val rateMap = currencyExchangeRate.rates
        val baseCurrency = currencyExchangeRate.base

        if (baseCurrency.equals(base, ignoreCase = true)) {
            rateMap[symbols]?.let {
                return it
            } ?: 0.00.toDouble()
        } else {
            rateMap[base]?.let { valueBase ->
                rateMap[symbols]?.let { valueSymbols ->
                    return (1 / valueBase) * valueSymbols
                }
            }
        }

        return 0.toDouble()
    }

    private fun isConversionFree(
        sellAmount: Double,
        config: ConfigEntity
    ): Boolean {
        if (config.every_nth_conversion_free > 0 && ((config.total_convert + 1) % config.every_nth_conversion_free) == 0) {
            return true
        } else if (config.total_convert < config.total_free_conversion) {
            return !(config.max_free_amount > 0 && sellAmount > config.max_free_amount)
        } else {
            return false
        }
    }
}