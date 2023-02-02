package com.wasir.droid.currencyexchange.domain.usecase.accounts

import com.wasir.droid.currencyexchange.common.Resource
import com.wasir.droid.currencyexchange.data.model.Account
import com.wasir.droid.currencyexchange.domain.repository.AccountRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAccountUseCase @Inject constructor(private val accountRepo: AccountRepo) {
    suspend operator fun invoke(): Flow<Resource<List<Account>>> {
        return accountRepo.getAccount()

    }
}