package com.adhibuchori.domain.payment.fulfillment

import com.adhibuchori.domain.Resource

interface IFulfillmentRepository {
    suspend fun fulfillTransaction(fulfillmentParameter: FulfillmentParameter): Resource<FulfillmentModel>
}