package com.Halza.Master.presentation.utils

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class CommonUtil {
    companion object {
        val dateTimePattern = DateTimeFormatter.ISO_DATE_TIME

        fun today() = LocalDateTime.now(ZoneId.of("UTC"))

        fun parseDateTime(
            dateStr: String,
            pattern: DateTimeFormatter = dateTimePattern
        ): LocalDateTime? {
            return if (dateStr != "") LocalDateTime.parse(dateStr, pattern) else null
        }

        fun toMilliseconds(dateStr: String, pattern: DateTimeFormatter = dateTimePattern): Long {
            val localDt = parseDateTime(dateStr, pattern)
            return toMilliseconds((localDt))
        }

        fun toMilliseconds(date: LocalDateTime?): Long {
            if (date == null) return 0

            val zdt = date.atZone(ZoneId.of("UTC"))
            return zdt.toInstant().toEpochMilli()
        }

        fun compareDateTime(
            dt1: String,
            dt2: String,
            pattern: DateTimeFormatter = dateTimePattern
        ): Int {
            val localDt1 = parseDateTime(dt1, pattern)
            val localDt2 = parseDateTime(dt2, pattern)
            return compareDateTime(localDt1, localDt2)
        }

        fun compareDateTime(dt1: LocalDateTime?, dt2: LocalDateTime?): Int {
            val dtInMilli1 = toMilliseconds(dt1)
            val dtInMilli2 = toMilliseconds(dt2)

            if (dtInMilli1 > dtInMilli2) return 1
            if (dtInMilli1 < dtInMilli2) return -1
            return 0
        }

        fun plusHourToDateTimeString(dtStr: String, hour: Long): String {
            val dt = parseDateTime(dtStr)
            val dtAfterPlus = dt?.plusHours(hour)
            return dateTimeToISOString((dtAfterPlus))
        }

        fun dateTimeToISOString(dt: LocalDateTime?): String {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            return dt?.format(formatter) ?: ""
        }

        fun dateTimeAtSystemTimeZone(dt: LocalDateTime): ZonedDateTime {
            val utcZonedDateTime = dt.atZone(ZoneId.of("UTC"))
            return utcZonedDateTime.withZoneSameInstant(ZoneId.systemDefault())
        }

        fun startOfDate(dt: LocalDateTime): LocalDateTime =
            LocalDateTime.of(dt.year, dt.monthValue, dt.dayOfMonth, 0, 0)

        fun endOfDate(dt: LocalDateTime): LocalDateTime =
            LocalDateTime.of(dt.year, dt.monthValue, dt.dayOfMonth, 23, 59)
    }
}