package com.Halza.Master.presentation.service

import com.Halza.Master.BuildConfig
import com.Halza.Master.presentation.utils.AppConstatnt
import okhttp3.Interceptor
import okhttp3.Response
//Header Interceptor to add subscription Key to request Header for every call
class HeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response = chain.run {
        proceed(
            request()
                .newBuilder()
                .addHeader(AppConstatnt.SUBSCRIPTION_KEY, BuildConfig.SUBSCRIPTION_KEY_VALUE)
                .build()
        )
    }
}