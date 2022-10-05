package com.wasir.droid.currencyexchange.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wasir.droid.currencyexchange.data.database.entity.AccountEntity
import com.wasir.droid.currencyexchange.data.database.entity.ConfigEntity
import com.wasir.droid.currencyexchange.data.database.entity.CurrencyRateEntity

@Dao
interface CurrencyExchangeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: AccountEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrencyRate(currencyRateEntity: CurrencyRateEntity)

    @Query("SELECT * FROM currency_rate")
    suspend fun getCurrencyEntity(): CurrencyRateEntity

    @Query("SELECT * FROM config")
    suspend fun getConfig(): ConfigEntity

    @Query("SELECT * FROM account")
    suspend fun getAccountList(): List<AccountEntity>

    @Query("SELECT currencyCode FROM account")
    suspend fun getCurrencyList(): List<String>

    @Query("SELECT * FROM account where currencyCode =:currencyCode ")
    suspend fun getAccountByCurrency(currencyCode: String): AccountEntity


    @Query("UPDATE account SET balance =:updatedBalance where currencyCode =:currencyCode ")
    suspend fun updateAccBalanceByCurrencyCode(currencyCode: String, updatedBalance: Double)

    @Query("SELECT EXISTS(SELECT * FROM account WHERE currencyCode = :currencyCode)")
    fun isCurrencyExists(currencyCode: String): Boolean

    @Query("UPDATE config SET commission =:updatedCommission")
    suspend fun updateCommission(updatedCommission: Double):Int

    @Query("UPDATE config SET syncTime =:updatedTimeInSec")
    suspend fun updateSyncTime(updatedTimeInSec: Int):Int
}