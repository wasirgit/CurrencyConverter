package com.wasir.droid.currencyexchange.data.mockrepo

import com.wasir.droid.currencyexchange.common.MockData
import com.wasir.droid.currencyexchange.data.database.dao.RoomDatabaseDao
import com.wasir.droid.currencyexchange.data.database.entity.AccountEntity
import com.wasir.droid.currencyexchange.data.database.entity.ConfigEntity
import com.wasir.droid.currencyexchange.data.database.entity.CurrencyRateEntity

class MockDataRepo : RoomDatabaseDao {

    private var accountList = mutableListOf<AccountEntity>()
    private var currencyRateList: CurrencyRateEntity? = null
    private var config = ConfigEntity(
        id = 1,
        total_free_conversion = 2,
        total_convert = 0,
        max_free_amount = 200.00,
        every_nth_conversion_free = 4,
        commission = 0.7,
        syncTime = 5
    )

    fun cleanUp() {
        accountList.clear()
        currencyRateList = null
        config = ConfigEntity(
            id = 1,
            total_free_conversion = 2,
            total_convert = 0,
            max_free_amount = 200.00,
            every_nth_conversion_free = 4,
            commission = 0.7,
            syncTime = 5
        )
    }

    fun setAccountList(accountList: List<AccountEntity>) {
        this.accountList = accountList.toMutableList()
    }

    fun setCurrencyRateEntity(currencyRateEntity: CurrencyRateEntity) {
        currencyRateList = currencyRateEntity
    }

    fun setConfigEntity(configEntity: ConfigEntity) {
        config = configEntity
    }

    override suspend fun insertAccount(account: AccountEntity) {
        accountList.add(account)
    }

    override suspend fun insertCurrencyRate(currencyRateEntity: CurrencyRateEntity) {
        currencyRateList = currencyRateEntity
    }

    override suspend fun getCurrencyRate(): CurrencyRateEntity {
        return MockData.getCurrencyRateEntity()
    }

    override suspend fun getConfig(): ConfigEntity {
        return config
    }

    override suspend fun getAccountList(): List<AccountEntity> {
        return accountList
    }

    override suspend fun getCurrencyList(): List<String> {
        return accountList.map { it.currencyCode }
    }

    override suspend fun getAccountByCurrency(currencyCode: String): AccountEntity {
        for (account in accountList) {
            if (account.currencyCode == currencyCode) return account
        }
        return AccountEntity("", 0.0)
    }

    override suspend fun updateAccBalanceByCurrencyCode(
        currencyCode: String,
        updatedBalance: Double
    ) {
        for (account in accountList) {
            if (account.currencyCode == currencyCode) {
                account.balance = updatedBalance
            }
        }
    }

    override fun isCurrencyExists(currencyCode: String): Boolean {
        for (account in accountList) {
            if (account.currencyCode == currencyCode) {
                return true
            }
        }
        return false
    }

    override suspend fun updateCommission(updatedCommission: Double): Int {
        config.commission = updatedCommission
        return 1
    }

    override suspend fun updateSyncTime(updatedTimeInSec: Int): Int {
        config.syncTime = updatedTimeInSec
        return 1
    }

    override suspend fun updateFreeConversionPosition(freeConversionPosition: Int): Int {
        config.every_nth_conversion_free = freeConversionPosition
        return 1
    }

    override suspend fun updateMaxFreeAmount(maxFreeAmount: Double): Int {
        config.max_free_amount = maxFreeAmount
        return 1
    }

    override suspend fun updateNumberOfFreeConversion(totalNumber: Int): Int {
        config.total_free_conversion = totalNumber
        return 1
    }

    override suspend fun updateConfig(config: ConfigEntity) {
        this.config = config
    }

}