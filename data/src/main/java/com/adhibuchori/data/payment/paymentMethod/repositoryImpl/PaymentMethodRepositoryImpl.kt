package com.adhibuchori.data.payment.paymentMethod.repositoryImpl

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.adhibuchori.data.R
import com.adhibuchori.data.payment.paymentMethod.response.PaymentMethodResponse
import com.adhibuchori.data.utils.toPaymentSectionList
import com.adhibuchori.domain.Resource
import com.adhibuchori.domain.payment.paymentMethod.IPaymentMethodRepository
import com.adhibuchori.domain.payment.paymentMethod.PaymentMethodModel
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.gson.Gson

class PaymentMethodRepositoryImpl(private val firebaseRemoteConfig: FirebaseRemoteConfig) :
    IPaymentMethodRepository {

    private val listPayment = MutableLiveData<Resource<List<PaymentMethodModel>>>()

    override fun checkPaymentConfigUpdate(): LiveData<Resource<List<PaymentMethodModel>>> {
        firebaseRemoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                if (configUpdate.updatedKeys.contains("payment_list")) {
                    getListPaymentMethods()
                }
            }

            override fun onError(error: FirebaseRemoteConfigException) {}
        })
        return listPayment
    }

    override fun getListPaymentMethods() {
        firebaseRemoteConfig.run {
            setDefaultsAsync(R.xml.remote_config_defaults)
            fetchAndActivate()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val paymentListJson = firebaseRemoteConfig.getString("payment_list")
                        val paymentResponse =
                            Gson().fromJson(paymentListJson, PaymentMethodResponse::class.java)

                        listPayment.postValue(
                            Resource.Success(
                                paymentResponse.data?.toPaymentSectionList().orEmpty()
                            )
                        )
                    }
                }
        }
    }
}