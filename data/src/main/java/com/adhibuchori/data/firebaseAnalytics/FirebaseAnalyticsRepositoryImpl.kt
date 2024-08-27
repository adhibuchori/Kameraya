package com.adhibuchori.data.firebaseAnalytics

import android.os.Bundle
import com.adhibuchori.domain.firebaseAnalytics.IFirebaseAnalyticsRepository
import com.google.firebase.analytics.FirebaseAnalytics

class FirebaseAnalyticsRepositoryImpl(
    private val analytics: FirebaseAnalytics
) : IFirebaseAnalyticsRepository {
    override fun logEvent(eventName: String, bundle: Bundle) {
       analytics.logEvent(eventName, bundle)
    }
}