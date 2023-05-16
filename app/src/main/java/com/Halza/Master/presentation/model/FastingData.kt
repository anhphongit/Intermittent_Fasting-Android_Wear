package com.Halza.Master.presentation.model

data class FastingData(
    val id: String = "",
    val accountId: String = "",//account Id
    val createdOn: String = "",
    val fastingDate: String = "",
    val fastingHr: Int = 0,
    val eatingHr: Int = 0,
    val plannedCycleStarts: String = "",
    val startFasting: String? = "",
    val endFasting: String = "",
    val calculatedFastingDuration: String = "",
    val calculatedEatingDuration: String = "",
    val code: String = "",
    val startEating: String = "",
    val endEating: String = "",
    val status: String = "",
    val expectedEndFasting: String = "",//expecting end fasting as the fasting not ended until user click end
) {
    override fun equals(other: Any?): Boolean {
        val otherData = other as FastingData?;
        return (otherData?.id ?: "") == (this.id ?: "") &&
                (otherData?.accountId ?: "") == (this.accountId ?: "") &&
                (otherData?.fastingDate ?: "") == (this.fastingDate ?: "") &&
                (otherData?.startFasting ?: "") == (this.startFasting ?: "") &&
                (otherData?.endFasting ?: "") == (this.endFasting ?: "") &&
                (otherData?.expectedEndFasting ?: "") == (this.expectedEndFasting ?: "")
    }
}