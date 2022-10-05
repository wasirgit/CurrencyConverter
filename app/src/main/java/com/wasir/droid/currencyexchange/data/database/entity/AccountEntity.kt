package com.wasir.droid.currencyexchange.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.wasir.droid.currencyexchange.data.model.Account

@Entity(tableName = "account")
data class AccountEntity(
    @PrimaryKey
    @ColumnInfo(name = "currencyCode")
    var currencyCode: String,
    @ColumnInfo(name = "balance")
    var balance: Double
) {
    fun toAccount(): Account {
        return Account(
            currencyCode = currencyCode,
            balance = balance
        )
    }
}
