package com.adhibuchori.domain.payment.rating.usecase

import com.adhibuchori.domain.Resource
import com.adhibuchori.domain.payment.rating.IRatingRepository
import com.adhibuchori.domain.payment.rating.RatingParameter

class SubmitRatingUseCase(
    private val ratingRepository: IRatingRepository
) {
    suspend operator fun invoke(ratingParameter: RatingParameter): Resource<Unit> {
        return ratingRepository.submitRating(ratingParameter)
    }
}