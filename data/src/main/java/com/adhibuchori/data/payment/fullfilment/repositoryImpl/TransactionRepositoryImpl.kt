package com.adhibuchori.data.payment.fullfilment.repositoryImpl

import com.adhibuchori.data.payment.fullfilment.source.TransactionApiService
import com.adhibuchori.data.utils.toTransactionModel
import com.adhibuchori.domain.Resource
import com.adhibuchori.domain.payment.transaction.ITransactionRepository
import com.adhibuchori.domain.payment.transaction.TransactionModel
import retrofit2.HttpException
import java.io.IOException

class TransactionRepositoryImpl(
    private val apiService: TransactionApiService,
) : ITransactionRepository {
    override suspend fun getTransactions(): Resource<List<TransactionModel>> {
        return try {
            val response = apiService.getTransaction()
            val transactionData = response.data?.map { it.toTransactionModel() } ?: emptyList()
            Resource.Success(transactionData)
        } catch (e: HttpException) {
            Resource.HttpError(e.code(), e.message())
        } catch (e: IOException) {
            Resource.NetworkError
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }
}