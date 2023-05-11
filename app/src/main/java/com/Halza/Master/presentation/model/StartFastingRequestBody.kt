package com.Halza.Master.presentation.model
//start Fasting body request for api
data class StartFastingRequestBody(
    var fastingDate: String="",var startFasting: String="",var endFasting: String="",var startEating: String="",var endEating: String="",var calculatedFastingDuration:String="",var calculatedEatingDuration:Double=0.0,var status:String="")
