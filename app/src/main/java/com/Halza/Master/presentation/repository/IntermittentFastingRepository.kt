package com.Halza.Master.presentation.service

import android.content.Context
import com.Halza.Master.presentation.model.FastingData
import com.Halza.Master.presentation.model.EndFastingRequestBody
import com.Halza.Master.presentation.model.NextDataResponse
import com.Halza.Master.presentation.model.StartFastingRequestBody
import com.Halza.Master.presentation.model.UpdateTimeDataRequest
import com.Halza.Master.presentation.utils.CommonUtil
import retrofit2.Response
import java.lang.Exception

class IntermittentFastingRepository(private val mContext: Context) {
    private val prefService = SharedPreferencesService(mContext)
    private val apiService = IntermittentFastingApiService.getInstance()

    suspend fun getCurrentFastingPlan(nodeId: String): Response<FastingData> {
        return apiService.GetCurrentFastingPlan(nodeId);
    }

    suspend fun getPreviousFasting(nodeId: String): Response<FastingData> {
        val res = apiService.GetPreviousFastingPlan(nodeId)
        if (res.isSuccessful) {
            prefService.savePreviousFasting(res.body()?.prvsFasting)
        }
        return Response.success(res.body()?.prvsFasting)
    }

    suspend fun getNextFastingData(nodeId: String): Response<NextDataResponse> {
        val res = try {
            apiService.getNextFastingData(nodeId)
        } catch (e: Exception) {
            val showingFasting = prefService.getShowingFasting()
            val nextFastingStart =
                if (showingFasting.endFasting == "") CommonUtil.plusHourToDateTimeString(
                    showingFasting.startFasting!!,
                    (showingFasting.fastingHr + showingFasting.eatingHr).toLong()
                ) else CommonUtil.plusHourToDateTimeString(
                    showingFasting.endFasting, (showingFasting.eatingHr).toLong()
                )
            val nextFastingEnd = CommonUtil.plusHourToDateTimeString(
                nextFastingStart, showingFasting.fastingHr.toLong()
            )

            Response.success(
                NextDataResponse(
                    nextStartFasting = nextFastingStart, nextEndFasting = nextFastingEnd
                )
            )
        }


        if (res.isSuccessful) {
            prefService.saveNextFasting(res.body())
        }
        return res
    }

    suspend fun updateTimeData(body: UpdateTimeDataRequest, nodeId: String): Response<Void> {
        return try {
            apiService.updateTimeData(body, nodeId)
        } catch (e: Exception) {
            var updatedFasting = prefService.getShowingFasting().copy()
            if (body.newStartFasting != "") updatedFasting =
                updatedFasting.copy(startFasting = body.newStartFasting)
            if (body.newExEndFasting != "") updatedFasting =
                updatedFasting.copy(expectedEndFasting = body.newExEndFasting)


            saveOutOfDateFasting(updatedFasting)

            Response.success(null)
        }
    }

    suspend fun startFasting(
        body: StartFastingRequestBody,
        nodeId: String,
        tmpShowingFasting: FastingData
    ): Response<Void> {
        return try {
            apiService.StartFasting(body, nodeId)
        } catch (e: Exception) {
            val newFasting = generateNewFasting(body.startFasting, tmpShowingFasting)
            saveOutOfDateFasting(newFasting, tmpShowingFasting)

            Response.success(null)
        }
    }

    suspend fun stopFasting(body: EndFastingRequestBody, nodeId: String): Response<Void> {
        return try {
            apiService.StopFasting(body, nodeId)
        } catch (e: Exception) {
            val updatedFasting = prefService.getShowingFasting().copy(endFasting = body.endFasting)
            saveOutOfDateFasting(updatedFasting)

            Response.success(null)
        }
    }

    suspend fun getFastingCycleHistory(
        period: String, nodeId: String
    ): Response<List<FastingData>> {
        val res = apiService.getFastingCycleHistory(period, nodeId)
        if (res.isSuccessful) {
            prefService.saveHistoryFasting(res.body() ?: listOf())
        }
        return res
    }

    fun checkIfHasUpdateSinceTheLastTimeConnect(
        currentFasting: FastingData
    ): Boolean {
        val storedCurrentFastingData = prefService.getCurrentFasting();
        return currentFasting != storedCurrentFastingData
    }

    fun checkIfHasOutDateData(): Boolean {
        val outOfFastingData = prefService.getOutOfSyncFasting()
        return outOfFastingData.size > 0
    }

    // This will be called when device went to offline
    fun saveOutOfDateFasting(showingFasting: FastingData, previousFasting: FastingData? = null) {
        val outOfFastingData = prefService.getOutOfSyncFasting()
        outOfFastingData[showingFasting.fastingDate] = showingFasting
        prefService.saveOutOfSyncFasting(outOfFastingData)

        if (previousFasting != null) prefService.savePreviousFasting(previousFasting)
    }

    suspend fun syncOutOfDateFasting(
        nodeId: String
    ): FastingData? {
        val previousFasting = apiService.GetPreviousFastingPlan(nodeId).body()?.prvsFasting
        val outOfFastingData = prefService.getOutOfSyncFasting()
        var handlingPreviousFasting: FastingData? = previousFasting?.copy()

        if (handlingPreviousFasting == null) return null

        for ((date, fastingData) in outOfFastingData) {
            if (CommonUtil.compareDateTime(
                    fastingData.startFasting ?: date, handlingPreviousFasting!!.endFasting
                ) == 1
            ) {
                if (handlingPreviousFasting.id == previousFasting!!.id && fastingData.id != "") {
                    val updateTimeDataRes = apiService.updateTimeData(
                        UpdateTimeDataRequest(
                            newStartFasting = fastingData.startFasting ?: "",
                            newExEndFasting = fastingData.expectedEndFasting,
                            id = fastingData.id
                        ), nodeId
                    )
                } else {
                    val startFastingRes = apiService.StartFasting(
                        StartFastingRequestBody(
                            fastingDate = date, startFasting = fastingData.startFasting ?: ""
                        ), nodeId
                    )
                }

                val endFastingRes = apiService.StopFasting(
                    EndFastingRequestBody(endFasting = fastingData.endFasting), nodeId
                )
                handlingPreviousFasting = fastingData.copy()
            }
        }

        getFastingCycleHistory("1W", nodeId)
        prefService.clearOutOfSyncFastingData()
        return handlingPreviousFasting; // return last fasting for showing
    }

    fun generateNewFasting(startFasting: String, currentFasting: FastingData): FastingData {
        val startFastingDate = CommonUtil.parseDateTime(startFasting);

        if (startFastingDate != null) {
            val expectedEndFasting =
                CommonUtil.plusHourToDateTimeString(startFasting, currentFasting.fastingHr.toLong())

            return FastingData(
                fastingDate = startFasting,
                createdOn = startFasting,
                fastingHr = currentFasting.fastingHr,
                eatingHr = currentFasting.eatingHr,
                plannedCycleStarts = currentFasting.plannedCycleStarts,
                startFasting = startFasting,
                expectedEndFasting = expectedEndFasting
            )
        }

        return FastingData()
    }
}