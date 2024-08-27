package com.adhibuchori.domain.payment.fulfillment.usecase

import com.adhibuchori.domain.Resource
import com.adhibuchori.domain.payment.fulfillment.FulfillmentModel
import com.adhibuchori.domain.payment.fulfillment.FulfillmentParameter
import com.adhibuchori.domain.payment.fulfillment.IFulfillmentRepository

class FulfillTransactionUseCase(
    private val fulfillmentRepository: IFulfillmentRepository
) {
    suspend operator fun invoke(fulfillmentParameter: FulfillmentParameter): Resource<FulfillmentModel> {
        return fulfillmentRepository.fulfillTransaction(fulfillmentParameter)
    }
}