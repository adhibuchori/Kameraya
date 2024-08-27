package com.adhibuchori.domain.firebaseAnalytics

import android.os.Bundle

interface IFirebaseAnalyticsRepository {
    fun logEvent(eventName: String, bundle: Bundle)
}