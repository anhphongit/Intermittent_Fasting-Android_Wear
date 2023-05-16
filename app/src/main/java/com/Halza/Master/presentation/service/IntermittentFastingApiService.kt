package com.Halza.Master.presentation.service

import com.Halza.Master.BuildConfig
import com.Halza.Master.presentation.model.*
import com.Halza.Master.presentation.utils.AppConstatnt
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

//intermittent fasting service for  all calls
interface IntermittentFastingApiService {
    /*Get Current Cycle For User*/
    @GET(AppConstatnt.CURRENT_FASTING_CYCLE)
    suspend fun GetCurrentFastingPlan(@Query(AppConstatnt.DEVICE_ID) deviceId: String): Response<FastingData>
    /*Get Previouse Cycle For User*/
    @GET(AppConstatnt.PREVIOUSE_FASTING_CYCLE)
    suspend fun GetPreviousFastingPlan(@Query(AppConstatnt.DEVICE_ID) deviceId: String): Response<PreviousFastingDataResponse>
    /*Start Fasting*/
    @POST(AppConstatnt.START_FASTING)
    suspend fun StartFasting(
        @Body startFasting: StartFastingRequestBody,
        @Query(AppConstatnt.DEVICE_ID) id: String
    ): Response<Void>

    /*EnD Fasting*/
    @POST(AppConstatnt.END_FASTING)
    suspend fun StopFasting(
        @Body EndFasting: EndFastingRequestBody,
        @Query(AppConstatnt.DEVICE_ID) id: String
    ): Response<Void>

    /*Get History For User For Chart*/
    @GET(AppConstatnt.FASTING_HISTORY)
    suspend fun getFastingCycleHistory(
        @Query(AppConstatnt.PERIOD) period: String,
        @Query(AppConstatnt.DEVICE_ID) deviceId: String
    ): Response<List<FastingData>>

    /*Get Next Fasting Time*/
    @GET(AppConstatnt.NEXT_FASTING)
    suspend fun getNextFastingData(@Query(AppConstatnt.DEVICE_ID) deviceId: String): Response<NextDataResponse>

    @PUT(AppConstatnt.UPDATE_TIME_FASTING)
    suspend fun updateTimeData(
        @Body updateTimeDataRequest: UpdateTimeDataRequest,
        @Query(AppConstatnt.DEVICE_ID) deviceId: String,
    ): Response<Void>

    //Setup the Retrofit Singlton Class baseurl and retrofit service httpclient
    companion object {
        var apiService: IntermittentFastingApiService? = null
        fun getInstance(): IntermittentFastingApiService {
            if (apiService == null) {
                //Build HttpCLient  For Retrofit Service
                val client: OkHttpClient =
                    OkHttpClient.Builder().addInterceptor(HeaderInterceptor())
                        .connectTimeout(100, TimeUnit.SECONDS)
                        .readTimeout(100, TimeUnit.SECONDS).build()
                /*Build Retrofit Service with Base URL*/
                apiService = Retrofit.Builder()
                    .baseUrl(BuildConfig.BASE_URL).client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(IntermittentFastingApiService::class.java)
            }
            return apiService!!
        }
    }

}