package com.adhibuchori.data.payment.fullfilment.repositoryImpl

import com.adhibuchori.data.payment.fullfilment.source.TransactionApiService
import com.adhibuchori.data.utils.toFulfillmentModel
import com.adhibuchori.data.utils.toFulfillmentRequest
import com.adhibuchori.domain.Resource
import com.adhibuchori.domain.payment.fulfillment.FulfillmentModel
import com.adhibuchori.domain.payment.fulfillment.FulfillmentParameter
import com.adhibuchori.domain.payment.fulfillment.IFulfillmentRepository
import java.io.IOException

class FulfillmentRepositoryImpl(
    private val apiService: TransactionApiService,
) : IFulfillmentRepository {
    override suspend fun fulfillTransaction(fulfillmentParameter: FulfillmentParameter): Resource<FulfillmentModel> {
        return try {
            val request = fulfillmentParameter.toFulfillmentRequest()
            val response = apiService.fulfillTransaction(request)
            val fulfillmentItemData = response.data.toFulfillmentModel()

            if (response.code == 200) {
                Resource.Success(fulfillmentItemData)
            } else {
                Resource.HttpError(response.code ?: -1, response.message)
            }
        } catch (e: IOException) {
            Resource.NetworkError
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage)
        }
    }
}