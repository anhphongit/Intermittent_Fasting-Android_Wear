package com.Halza.Master.presentation.model

import com.Halza.Master.presentation.utils.CommonUtil
import com.google.gson.Gson

class FastingData constructor(
    id: String? = "",
    accountId: String? = "",//account Id
    createdOn: String? = "",
    fastingDate: String? = "",
    fastingHr: Int? = 0,
    eatingHr: Int? = 0,
    plannedCycleStarts: String? = "",
    startFasting: String? = "",
    endFasting: String? = "",
    calculatedFastingDuration: String? = "",
    calculatedEatingDuration: String? = "",
    code: String? = "",
    startEating: String? = "",
    endEating: String? = "",
    status: String? = "",
    expectedEndFasting: String? = "",//expecting end fasting as the fasting not ended until user click end
) {
    var id: String = if (CommonUtil.detectNullString(id)) "" else id!!
        get() = if (CommonUtil.detectNullString(field)) "" else field
        set(value) {
            field = if (CommonUtil.detectNullString(value)) "" else value
        }

    var accountId: String = if (CommonUtil.detectNullString(accountId)) "" else accountId!!
        get() = if (CommonUtil.detectNullString(field)) "" else field
        set(value) {
            field = if (CommonUtil.detectNullString(value)) "" else value
        }

    var createdOn: String = if (CommonUtil.detectNullString(createdOn)) "" else createdOn!!
        get() = if (CommonUtil.detectNullString(field)) "" else field
        set(value) {
            field = if (CommonUtil.detectNullString(value)) "" else value
        }

    var fastingDate: String = if (CommonUtil.detectNullString(fastingDate)) "" else fastingDate!!
        get() = if (CommonUtil.detectNullString(field)) "" else field
        set(value) {
            field = if (CommonUtil.detectNullString(value)) "" else value
        }

    var fastingHr: Int = fastingHr ?: 0
        get() = if (CommonUtil.detectNullString(field.toString())) 0 else field
        set(value) {
            field = value ?: 0
        }

    var eatingHr: Int = eatingHr ?: 0
        get() = if (CommonUtil.detectNullString(field.toString())) 0 else field
        set(value) {
            field = value ?: 0
        }

    var plannedCycleStarts: String =
        if (CommonUtil.detectNullString(plannedCycleStarts)) "" else plannedCycleStarts!!
        get() = if (CommonUtil.detectNullString(field)) "" else field
        set(value) {
            field = if (CommonUtil.detectNullString(value)) "" else value
        }

    var startFasting: String = if (CommonUtil.detectNullString(startFasting)) "" else startFasting!!
        get() = if (CommonUtil.detectNullString(field)) "" else field
        set(value) {
            field = if (CommonUtil.detectNullString(value)) "" else value
        }

    var endFasting: String = if (CommonUtil.detectNullString(endFasting)) "" else endFasting!!
        get() = if (CommonUtil.detectNullString(field)) "" else field
        set(value) {
            field = if (CommonUtil.detectNullString(value)) "" else value
        }

    var calculatedFastingDuration: String =
        if (CommonUtil.detectNullString(calculatedFastingDuration)) "" else calculatedFastingDuration!!
        get() = if (CommonUtil.detectNullString(field)) "" else field
        set(value) {
            field = if (CommonUtil.detectNullString(value)) "" else value
        }

    var calculatedEatingDuration: String =
        if (CommonUtil.detectNullString(calculatedEatingDuration)) "" else calculatedEatingDuration!!
        get() = if (CommonUtil.detectNullString(field)) "" else field
        set(value) {
            field = if (CommonUtil.detectNullString(value)) "" else value
        }

    var code: String = if (CommonUtil.detectNullString(code)) "" else code!!
        get() = if (CommonUtil.detectNullString(field)) "" else field
        set(value) {
            field = if (CommonUtil.detectNullString(value)) "" else value
        }

    var startEating: String = if (CommonUtil.detectNullString(startEating)) "" else startEating!!
        get() = if (CommonUtil.detectNullString(field)) "" else field
        set(value) {
            field = if (CommonUtil.detectNullString(value)) "" else value
        }

    var endEating: String = if (CommonUtil.detectNullString(endEating)) "" else endEating!!
        get() = if (CommonUtil.detectNullString(field)) "" else field
        set(value) {
            field = if (CommonUtil.detectNullString(value)) "" else value
        }

    var status: String = if (CommonUtil.detectNullString(status)) "" else status!!
        get() = if (CommonUtil.detectNullString(field)) "" else field
        set(value) {
            field = if (CommonUtil.detectNullString(value)) "" else value
        }

    var expectedEndFasting: String =
        if (CommonUtil.detectNullString(expectedEndFasting)) "" else expectedEndFasting!!
        get() = if (CommonUtil.detectNullString(field)) "" else field
        set(value) {
            field = if (CommonUtil.detectNullString(value)) "" else value
        }

    fun copy(
        id: String? = this.id,
        accountId: String? = this.accountId,
        createdOn: String? = this.createdOn,
        fastingDate: String? = this.fastingDate,
        fastingHr: Int? = this.fastingHr,
        eatingHr: Int? = this.eatingHr,
        plannedCycleStarts: String? = this.plannedCycleStarts,
        startFasting: String? = this.startFasting,
        endFasting: String? = this.endFasting,
        calculatedFastingDuration: String? = this.calculatedFastingDuration,
        calculatedEatingDuration: String? = this.calculatedEatingDuration,
        code: String? = this.code,
        startEating: String? = this.startEating,
        endEating: String? = this.endEating,
        status: String? = this.status,
        expectedEndFasting: String? = this.expectedEndFasting,
    ): FastingData {
        return FastingData(
            id,
            accountId,
            createdOn,
            fastingDate,
            fastingHr,
            eatingHr,
            plannedCycleStarts,
            startFasting,
            endFasting,
            calculatedFastingDuration,
            calculatedEatingDuration,
            code,
            startEating,
            endEating,
            status,
            expectedEndFasting,
        )
    }


    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + accountId.hashCode()
        result = 31 * result + createdOn.hashCode()
        result = 31 * result + fastingDate.hashCode()
        result = 31 * result + fastingHr
        result = 31 * result + eatingHr
        result = 31 * result + plannedCycleStarts.hashCode()
        result = 31 * result + startFasting.hashCode()
        result = 31 * result + endFasting.hashCode()
        result = 31 * result + calculatedFastingDuration.hashCode()
        result = 31 * result + calculatedEatingDuration.hashCode()
        result = 31 * result + code.hashCode()
        result = 31 * result + startEating.hashCode()
        result = 31 * result + endEating.hashCode()
        result = 31 * result + status.hashCode()
        result = 31 * result + expectedEndFasting.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if(other == null) return false

        other as FastingData

        if (id != other.id) return false
        if (accountId != other.accountId) return false
        if (fastingDate != other.fastingDate) return false
        if (startFasting != other.startFasting) return false
        if (endFasting != other.endFasting) return false
        if (expectedEndFasting != other.expectedEndFasting) return false

        return true
    }

    override fun toString(): String {
        return "FastingData(id='$id', accountId='$accountId', createdOn='$createdOn', fastingDate='$fastingDate', fastingHr=$fastingHr, eatingHr=$eatingHr, plannedCycleStarts='$plannedCycleStarts', startFasting='$startFasting', endFasting='$endFasting', calculatedFastingDuration='$calculatedFastingDuration', calculatedEatingDuration='$calculatedEatingDuration', code='$code', startEating='$startEating', endEating='$endEating', status='$status', expectedEndFasting='$expectedEndFasting')"
    }
}