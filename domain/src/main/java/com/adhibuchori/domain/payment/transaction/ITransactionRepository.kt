package com.adhibuchori.domain.payment.transaction

import com.adhibuchori.domain.Resource

interface ITransactionRepository {
    suspend fun getTransactions(): Resource<List<TransactionModel>>
}