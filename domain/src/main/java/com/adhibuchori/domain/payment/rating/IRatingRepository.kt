package com.adhibuchori.domain.payment.rating

import com.adhibuchori.domain.Resource

interface IRatingRepository {
    suspend fun submitRating(ratingParameter: RatingParameter): Resource<Unit>
}