package com.Halza.Master.presentation.service

import android.content.Context
import android.content.SharedPreferences
import com.Halza.Master.presentation.utils.AppKey

class SharedPreferencesService(private val mContext: Context) {
    private val prefs: SharedPreferences =
        mContext.getSharedPreferences(mContext.packageName, Context.MODE_PRIVATE)

    fun getShowingFasting(): String? {
        return prefs.getString(AppKey.SHOWING_FASTING, "")
    }

    fun saveShowingFasting(data: String?) {
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.putString(AppKey.SHOWING_FASTING, data)
        editor.apply()
    }

    fun getCurrentFasting(): String? {
        return prefs.getString(AppKey.CURRENT_FASTING, "")
    }

    fun saveCurrentFasting(data: String?) {
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.putString(AppKey.CURRENT_FASTING, data)
        editor.apply()
    }

    fun getPreviousFasting(): String? {
        return prefs.getString(AppKey.PREVIOUS_FASTING, "")
    }

    fun savePreviousFasting(data: String?) {
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.putString(AppKey.PREVIOUS_FASTING, data)
        editor.apply()
    }

    fun getHistoryFasting(): String? {
        return prefs.getString(AppKey.HISTORY_FASTING, "")
    }

    fun saveHistoryFasting(data: String?) {
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.putString(AppKey.HISTORY_FASTING, data)
        editor.apply()
    }

    fun getOutOfSyncFasting(): String? {
        return prefs.getString(AppKey.OUT_OF_SYNC_FASTING, "")
    }

    fun saveOutOfSyncFasting(data: String?) {
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.putString(AppKey.OUT_OF_SYNC_FASTING, data)
        editor.apply()
    }
}