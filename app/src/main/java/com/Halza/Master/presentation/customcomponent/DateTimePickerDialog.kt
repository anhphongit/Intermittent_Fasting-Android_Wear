package com.Halza.Master.presentation.customcomponent

import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import android.widget.DatePicker
import android.widget.TimePicker
import com.Halza.Master.R
import com.Halza.Master.presentation.utils.CommonUtil
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

class DateTimePickerDialog private constructor(
    context: Context,
    pickedDateTime: LocalDateTime,
    minDateTime: LocalDateTime?,
    maxDateTime: LocalDateTime?,
    private val title: String?,
    private val onPickComplete: (dateTime: LocalDateTime) -> Unit
) {
    // Builder class for Builder Pattern
    data class Builder(
        private val context: Context,
        private val pickedDate: LocalDateTime = CommonUtil.today()
    ) {
        private var minDateTime: LocalDateTime? = null
        private var maxDateTime: LocalDateTime? = null
        private var title: String? = null
        private var onPickComplete: (dateTime: LocalDateTime) -> Unit = {}

        fun title(title: String?) = apply { this.title = title }
        fun minDate(minDate: LocalDateTime?) = apply { this.minDateTime = minDate }
        fun maxDate(maxDate: LocalDateTime?) = apply { this.maxDateTime = maxDate }
        fun onPickComplete(onPickComplete: (dateTime: LocalDateTime) -> Unit = {}) =
            apply { this.onPickComplete = onPickComplete }

        fun show() = DateTimePickerDialog(
            context, pickedDate, minDateTime, maxDateTime, title, onPickComplete
        ).show()
    }

    // Convert all data time to system timezone for UI Showing
    private val systemTimeZonePickedDateTime = CommonUtil.dateTimeAtSystemTimeZone(pickedDateTime)
    private val systemTimeZoneMaxDateTime =
        if (maxDateTime != null) CommonUtil.dateTimeAtSystemTimeZone(maxDateTime) else null
    private val systemTimeZoneMinDateTime =
        if (minDateTime != null) CommonUtil.dateTimeAtSystemTimeZone(minDateTime) else null

    private val datePickerListener = { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
        onDatePicked(
            mYear, mMonth, mDayOfMonth
        )
    }
    private val timePickerListener = { _: TimePicker, mHour: Int, mMin: Int ->
        onTimePicked(mHour, mMin)
    }
    private val datePickerDialog = DatePickerDialog(
        context,
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P) R.style.CustomDatePickerDialog12 else R.style.CustomDatePickerDialog,
        datePickerListener,
        systemTimeZonePickedDateTime.year,
        systemTimeZonePickedDateTime.monthValue - 1,
        systemTimeZonePickedDateTime.dayOfMonth
    )
    private val timePickerDialog = RangeTimePickerDialog(
        context,
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P) R.style.CustomDatePickerDialog1 else R.style.CustomDatePickerDialog,
        timePickerListener,
        systemTimeZonePickedDateTime.hour,
        systemTimeZonePickedDateTime.minute,
        false
    )
    private lateinit var pickingRes: LocalDateTime

    private fun show() {
        showDatePicker()
    }

    private fun showDatePicker() {
        if (title != null) datePickerDialog.setTitle(title)
        if (systemTimeZoneMinDateTime != null) {
            datePickerDialog.datePicker.minDate =
                systemTimeZoneMinDateTime.toInstant().toEpochMilli()

            if (CommonUtil.compareDateTime(
                    systemTimeZonePickedDateTime.toLocalDateTime(),
                    systemTimeZoneMinDateTime.toLocalDateTime()
                ) == -1
            ) datePickerDialog.datePicker.updateDate(
                systemTimeZoneMinDateTime.year,
                systemTimeZoneMinDateTime.monthValue - 1,
                systemTimeZoneMinDateTime.dayOfMonth
            )
        }
        if (systemTimeZoneMaxDateTime != null) {
            datePickerDialog.datePicker.maxDate =
                systemTimeZoneMaxDateTime.toInstant().toEpochMilli()

            if (CommonUtil.compareDateTime(
                    systemTimeZonePickedDateTime.toLocalDateTime(),
                    systemTimeZoneMaxDateTime.toLocalDateTime()
                ) == 1
            ) datePickerDialog.datePicker.updateDate(
                systemTimeZoneMaxDateTime.year,
                systemTimeZoneMaxDateTime.monthValue - 1,
                systemTimeZoneMaxDateTime.dayOfMonth
            )
        }


        datePickerDialog.show()
    }

    private fun showTimePicker(pickingDate: LocalDateTime) {
        var availableMinDate = CommonUtil.startOfDate(pickingDate)
        var availableMaxDate = CommonUtil.endOfDate(pickingDate)
        var availablePickDate = systemTimeZonePickedDateTime.toLocalDateTime()

        if (title != null) timePickerDialog.setTitle(title)

        if (systemTimeZoneMinDateTime != null) {
            if (CommonUtil.compareDateTime(
                    availableMinDate, systemTimeZoneMinDateTime.toLocalDateTime()
                ) == -1
            ) availableMinDate = systemTimeZoneMinDateTime.toLocalDateTime()
        }

        if (systemTimeZoneMaxDateTime != null) {
            if (CommonUtil.compareDateTime(
                    availableMaxDate, systemTimeZoneMaxDateTime.toLocalDateTime()
                ) == 1
            ) availableMaxDate = systemTimeZoneMaxDateTime.toLocalDateTime()
        }

        if (CommonUtil.compareDateTime(
                availablePickDate, availableMinDate
            ) == -1
        ) availablePickDate = availableMinDate

        if (CommonUtil.compareDateTime(
                availablePickDate, availableMaxDate
            ) == 1
        ) availablePickDate = availableMaxDate



        timePickerDialog.setMin(availableMinDate.hour, availableMinDate.minute)
        timePickerDialog.setMax(availableMaxDate.hour, availableMaxDate.minute)
        timePickerDialog.updateValue(availablePickDate.hour, availablePickDate.minute)
        timePickerDialog.show()
    }

    private fun onDatePicked(mYear: Int, mMonth: Int, mDayOfMonth: Int) {
        val res = LocalDateTime.of(mYear, mMonth + 1, mDayOfMonth, 0, 0)
        pickingRes = res
        showTimePicker(res)
    }

    // Convert Date Time to UTC for result
    private fun onTimePicked(mHour: Int, mMin: Int) {
        pickingRes = pickingRes.withHour(mHour).withMinute(mMin)
        val utcPickingRes =
            pickingRes.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"))
        onPickComplete(utcPickingRes.toLocalDateTime())
    }
}