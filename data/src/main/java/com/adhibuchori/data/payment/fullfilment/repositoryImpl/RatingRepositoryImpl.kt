package com.adhibuchori.data.payment.fullfilment.repositoryImpl

import com.adhibuchori.data.payment.fullfilment.source.TransactionApiService
import com.adhibuchori.data.utils.toRatingRequest
import com.adhibuchori.domain.Resource
import com.adhibuchori.domain.payment.rating.IRatingRepository
import com.adhibuchori.domain.payment.rating.RatingParameter
import java.io.IOException

class RatingRepositoryImpl(
    private val apiService: TransactionApiService,
) : IRatingRepository {
    override suspend fun submitRating(ratingParameter: RatingParameter): Resource<Unit> {
        return try {
            val request = ratingParameter.toRatingRequest()
            val response = apiService.submitRating(request)
            if (response.code == 200) {
                Resource.Success(Unit)
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