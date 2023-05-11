package com.Halza.Master.presentation.utils

import java.time.LocalDateTime

//Model Class for update UI
data class MainDataState(
    var Progress: Float = 0.0f,//Variable for progress percntage
    var fastingPeriod: String = "",//var for remaining time for fasting it shows how many hours:min user fasting
    var Fastinghour: Int = 0,//the planned Fasting Hour
    var eatingHour: Int = 0,//planed Eating hour based on the plan
    val Percntage: Float = 0f,//Percntage that user achived from the fasting hour -Deperecated
    var nextFastingTime: String = "Fasting",//Next Fasting Time for user  showing in first page it will show Fasting while ongoing fasting or next fasting Time when user end
    var FastingStartTime: String = "",//Fasting start Time
    var FastingEndTime: String = "",//Fasting End Time
    var fastingHistoryDataList: MutableMap<Any, Float> =mutableMapOf(Pair("21", 16f), Pair("22", 12f), Pair("23", 15f),Pair("24", 16f), Pair("25", 12f), Pair("26", 15f), Pair("27", 18f)),//List of user fasting hours
    val showingPercntage: Boolean=false,//Hid/Show Percentage
    var userHasConnected: Boolean=true,//for check user has plan or not
    var editVisiable: Boolean=false,//to show/Hide edit icon
    var NextFastingText: String="",//for Next Fasting text in second page to change to Fasting when user start
    var minHourForEnd: Int=0,//to pust validation for the end fasting not before start fasting
    var minMinForEnd: Int=0,//to pust validation for the end fasting not before start fasting
    var startEndText: String="",//Text for Button to change dynamiclly while run time
    var isUserOverFasting: Boolean=false,//to check if user over fasting than his plan
    var startFastingTime: LocalDateTime = LocalDateTime.now(),//To save start Fasting Time to used for validation
    var PreviuosStartFastingTime: LocalDateTime= LocalDateTime.now(),//Get Previouse Fasting Start DateTime
    var PreviuosEndFastingTime: LocalDateTime= LocalDateTime.now().plusDays(-1).withHour(16).withMinute(5),//Get Previouse Fasting End DateTime
    var NewUser: Boolean=false,//to Check if User New that mean doesn't have Plan Before to use it in Dta Pickers
  )


