package com.Halza.Master.presentation.service

import android.content.Context
import android.content.SharedPreferences
import com.Halza.Master.presentation.model.CurrentCycleFastingData
import com.Halza.Master.presentation.model.EndFastingRequestBody
import com.Halza.Master.presentation.model.NextDataResponse
import com.Halza.Master.presentation.model.PrevouisFastingData
import com.Halza.Master.presentation.model.StartFastingRequestBody
import com.Halza.Master.presentation.model.UpdateTimeDataRequest
import com.Halza.Master.presentation.utils.AppKey
import retrofit2.Response

class IntermittentFastingRepository(private val mContext: Context) {
    private val prefService = SharedPreferencesService(mContext)
    private val apiService = IntermittentFastingApiService.getInstance()

    suspend fun getCurrentFastingPlan(nodeId: String): Response<CurrentCycleFastingData> {
        return apiService.GetCurrentFastingPlan(nodeId)
    }

    suspend fun getPreviousFasting(nodeId: String): Response<PrevouisFastingData> {
        return apiService.GetPreviousFastingPlan(nodeId)
    }

    suspend fun getNextFastingData(nodeId: String): Response<NextDataResponse> {
        return apiService.getNextFastingData(nodeId)
    }

    suspend fun updateTimeData(body: UpdateTimeDataRequest, nodeId: String): Response<Void> {
        return apiService.updateTimeData(body, nodeId)
    }

    suspend fun startFasting(body: StartFastingRequestBody, nodeId: String): Response<Void> {
        return apiService.StartFasting(body, nodeId)
    }

    suspend fun stopFasting(body: EndFastingRequestBody, nodeId: String): Response<Void> {
        return apiService.StopFasting(body, nodeId)
    }

    suspend fun getFastingCycleHistory(
        period: String,
        nodeId: String
    ): List<CurrentCycleFastingData> {
        return apiService.getFastingCycleHistory(period, nodeId)
    }


}