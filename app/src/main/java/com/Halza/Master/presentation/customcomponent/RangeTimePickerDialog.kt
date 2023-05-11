package com.Halza.Master.presentation.customcomponent

import android.R.color
import android.app.TimePickerDialog
import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.View
import android.widget.NumberPicker
import android.widget.TimePicker
import java.text.DateFormat
import java.util.*


//A time dialog that allows setting a min and max time.
class RangeTimePickerDialog(
    context: Context?,
    themeRsid: Int,
    callBack: OnTimeSetListener?,
    hourOfDay: Int,
    minute: Int,
    is24HourView: Boolean
) :
    TimePickerDialog(context, themeRsid, callBack, hourOfDay, minute, is24HourView) {
    private var minHour = -1
    private var minMinute = -1
    private var maxHour = 25
    private var maxMinute = 25
    private var currentHour = 0
    private var currentMinute = 0
    private val calendar = Calendar.getInstance()
    private val dateFormat: DateFormat

    init {
        currentHour = hourOfDay
        currentMinute = minute
        dateFormat = DateFormat.getTimeInstance(DateFormat.SHORT)
        try {
            val superclass: Class<*> = javaClass.superclass
            val mTimePickerField = superclass.getDeclaredField("mTimePicker")
            mTimePickerField.isAccessible = true
            val mTimePicker = mTimePickerField[this] as TimePicker
            mTimePicker.setOnTimeChangedListener(this)


        } catch (e: NoSuchFieldException) {
            e.message
        } catch (e: IllegalArgumentException) {
            e.message
        } catch (e: IllegalAccessException) {
            e.message
        }
    }

    fun setMin(hour: Int, minute: Int) {
        minHour = hour
        minMinute = minute
    }

    fun setMax(hour: Int, minute: Int) {
        maxHour = hour
        maxMinute = minute
    }

    override fun onTimeChanged(view: TimePicker, hourOfDay: Int, minute: Int) {
        var validTime = true
        if (hourOfDay < minHour || hourOfDay == minHour && minute < minMinute) {
            validTime = false
        }
        if (hourOfDay > maxHour || hourOfDay == maxHour && minute > maxMinute) {
            validTime = false
        }
        if (validTime) {
            currentHour = hourOfDay
            currentMinute = minute
        }
        updateTime(currentHour, currentMinute)
//        updateDialogTitle(view, currentHour, currentMinute)
    }

    private fun updateDialogTitle(timePicker: TimePicker, hourOfDay: Int, minute: Int) {
        calendar[Calendar.HOUR_OF_DAY] = hourOfDay
        calendar[Calendar.MINUTE] = minute
        val title = dateFormat.format(calendar.time)
        setTitle(title)
    }

    private fun applyStyLing(timePickerDialog: TimePicker) {
        val system = Resources.getSystem()
        val hourNumberPickerId = system.getIdentifier("hour", "id", "android")
        val minuteNumberPickerId = system.getIdentifier("minute", "id", "android")
        val ampmNumberPickerId = system.getIdentifier("amPm", "id", "android")
        val hourNumberPicker =
            timePickerDialog.findViewById<View>(hourNumberPickerId) as NumberPicker
        val minuteNumberPicker =
            timePickerDialog.findViewById<View>(minuteNumberPickerId) as NumberPicker
        val ampmNumberPicker =
            timePickerDialog.findViewById<View>(ampmNumberPickerId) as NumberPicker
        setNumberPickerDividerColour(hourNumberPicker)
        setNumberPickerDividerColour(minuteNumberPicker)
        setNumberPickerDividerColour(ampmNumberPicker)
    }

    private fun setNumberPickerDividerColour(number_picker: NumberPicker) {
        val count = number_picker.childCount
        for (i in 0 until count) {
            try {
                val superclass: Class<*> = javaClass.superclass
                val dividerField = superclass.getDeclaredField("mSelectionDivider")
                dividerField.isAccessible = true
                val colorDrawable =
                    ColorDrawable(context.getResources().getColor(color.holo_red_dark))
                dividerField[number_picker] = colorDrawable
                number_picker.invalidate()
            } catch (e: NoSuchFieldException) {
                Log.w("setNumberPickerTxtClr", e)
            } catch (e: IllegalAccessException) {
                Log.w("setNumberPickerTxtClr", e)
            } catch (e: java.lang.IllegalArgumentException) {
                Log.w("setNumberPickerTxtClr", e)
            }
        }
    }
    public fun updateValue(hourOfDay: Int, minute: Int) {

        updateTime(hourOfDay, minute)
    }


}