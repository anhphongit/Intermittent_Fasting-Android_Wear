package com.Halza.Master.presentation.service

import android.content.Context
import android.content.SharedPreferences
import com.Halza.Master.presentation.model.FastingData
import com.Halza.Master.presentation.model.NextDataResponse
import com.Halza.Master.presentation.utils.AppKey
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class SharedPreferencesService(private val mContext: Context) {
    private val prefs: SharedPreferences =
        mContext.getSharedPreferences(mContext.packageName, Context.MODE_PRIVATE)

    fun getShowingFasting(): FastingData {
        val res = prefs.getString(AppKey.SHOWING_FASTING, "")
        return if (res != "") Gson().fromJson(res, FastingData::class.java) else FastingData()
    }

    fun saveShowingFasting(data: FastingData?) {
        val dataStr = if (data != null) Gson().toJson(data) else ""
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.putString(AppKey.SHOWING_FASTING, dataStr)
        editor.apply()
    }

    fun getCurrentFasting(): FastingData? {
        val res = prefs.getString(AppKey.CURRENT_FASTING, "")
        return if (res != "") Gson().fromJson(res, FastingData::class.java) else null
    }

    fun saveCurrentFasting(data: FastingData?) {
        val dataStr = if (data != null) Gson().toJson(data) else ""
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.putString(AppKey.CURRENT_FASTING, dataStr)
        editor.apply()
    }

    fun getPreviousFasting(): FastingData? {
        val res = prefs.getString(AppKey.PREVIOUS_FASTING, "")
        return if (res != "") Gson().fromJson(res, FastingData::class.java) else null
    }

    fun savePreviousFasting(data: FastingData?) {
        val dataStr = if (data != null) Gson().toJson(data) else ""
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.putString(AppKey.PREVIOUS_FASTING, dataStr)
        editor.apply()
    }

    fun getNextFasting(): NextDataResponse? {
        val res = prefs.getString(AppKey.NEXT_FASTING, "")
        return if (res != "") Gson().fromJson(res, NextDataResponse::class.java) else null
    }

    fun saveNextFasting(data: NextDataResponse?) {
        val dataStr = if (data != null) Gson().toJson(data) else ""
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.putString(AppKey.NEXT_FASTING, dataStr)
        editor.apply()
    }

    fun getHistoryFasting(): List<FastingData> {
        val res = prefs.getString(AppKey.HISTORY_FASTING, "")
        val empMapType: Type = object : TypeToken<List<FastingData>>() {}.type
        return if (res != "") Gson().fromJson(res, empMapType) else listOf()
    }

    fun saveHistoryFasting(data: List<FastingData>) {
        val dataStr = Gson().toJson(data)
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.putString(AppKey.HISTORY_FASTING, dataStr)
        editor.apply()
    }

    fun getOutOfSyncFasting(): HashMap<String, FastingData> {
        val res = prefs.getString(AppKey.OUT_OF_SYNC_FASTING, "")
        val empMapType: Type = object : TypeToken<LinkedTreeMap<String, FastingData>>() {}.type

        return if (res != "") java.util.HashMap(
            Gson().fromJson<LinkedTreeMap<String, FastingData>>(
                res,
                empMapType
            ).toMap()
        ) else hashMapOf()
    }

    fun saveOutOfSyncFasting(data: HashMap<String, FastingData>) {
        val dataStr = Gson().toJson(data)
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.putString(AppKey.OUT_OF_SYNC_FASTING, dataStr)
        editor.apply()
    }

    fun clearOutOfSyncFastingData() {
        saveOutOfSyncFasting(hashMapOf())
    }
}