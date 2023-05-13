package com.Halza.Master.presentation.utils

object AppConstatnt {

    const val TAG = "MainActivity"
    const val STORAGE_NAME = "Halza_Storage"//local storage Name
    const val NODE_ID = "NODE_ID"//String for Nod ID
//Subscription Key And Value for APi Manaagment
    const val SUBSCRIPTION_KEY = "Ocp-Apim-Subscription-Key"
    const val SUBSCRIPTION_KEY_VALUE_TEST = "acbef760627b4fa5ac991a7ea764b096"//Not used as we setup fron gradle configration
    //URL Suffix
    const val BASE_URL="https://api.test.halza.com/intermittentfasting/"//Not used as we setup fron gradle configration
    const val CURRENT_FASTING_CYCLE = "Fasting/cycles/current"//get current record for user
    const val PREVIOUSE_FASTING_CYCLE = "Fasting/PreCycle"//get previous record for user
    const val START_FASTING = "Fasting/cycles/current/startFasting"//Start fasting
    const val END_FASTING = "Fasting/cycles/current/stopFasting"//end fasting
    const val FASTING_HISTORY = "Fasting/cycles"//get the History for user for chart
    const val NEXT_FASTING = "Fasting/nextCycle"//get next fasting Time
    const val UPDATE_TIME_FASTING = "Fasting"//edit start and end fasting
    const val DEVICE_ID = "deviceId"//Deviuce id to pass for url as query parameter
    const val PERIOD = "period"//period to pass for url as query parameter

}

object AppKey {
    // Pref storage
    const val OUT_OF_SYNC_FASTING = "OutOfSyncFasting"
    const val SHOWING_FASTING = "ShowingFasting"
    const val CURRENT_FASTING = "CurrentFasting"
    const val PREVIOUS_FASTING = "PreviousFasting"
    const val HISTORY_FASTING = "HistoryHasting"
}

enum class NetworkType {
    Cellular, Wifi, NA
}