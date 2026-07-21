package com.nooshyar.app.core.util

import java.util.Calendar

object JalaliDate {
    private val gregorianDaysInMonth = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
    private val jalaliDaysInMonth = intArrayOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)

    data class Jalali(val year: Int, val month: Int, val day: Int)

    fun today(): Jalali {
        val cal = Calendar.getInstance()
        return gregorianToJalali(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
    }

    fun formatToday(): String {
        val j = today()
        val monthNames = arrayOf(
            "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
            "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند"
        )
        return "${j.day} ${monthNames[j.month - 1]} ${j.year}"
    }

    fun formatTime(millis: Long): String {
        val cal = Calendar.getInstance().apply { timeInMillis = millis }
        return String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
    }

    fun formatMinutes(minutes: Int): String {
        val h = minutes / 60
        val m = minutes % 60
        return String.format("%02d:%02d", h, m)
    }

    fun startOfDay(millis: Long = System.currentTimeMillis()): Long {
        return Calendar.getInstance().apply {
            timeInMillis = millis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    fun endOfDay(millis: Long = System.currentTimeMillis()): Long {
        return startOfDay(millis) + 24 * 60 * 60 * 1000 - 1
    }

    fun startOfWeek(): Long {
        val cal = Calendar.getInstance()
        cal.firstDayOfWeek = Calendar.SATURDAY
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        if (cal.timeInMillis > System.currentTimeMillis()) {
            cal.add(Calendar.WEEK_OF_YEAR, -1)
        }
        return cal.timeInMillis
    }

    fun minutesUntilSleep(nowMillis: Long, sleepTimeMinutes: Int): Float {
        val cal = Calendar.getInstance().apply { timeInMillis = nowMillis }
        val nowMinutes = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
        var diff = sleepTimeMinutes - nowMinutes
        if (diff < 0) diff += 24 * 60
        return diff / 60f
    }

    fun monthName(month: Int): String {
        val names = arrayOf(
            "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
            "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند"
        )
        return names.getOrElse(month - 1) { "" }
    }

    fun weekdayName(millis: Long): String {
        val cal = Calendar.getInstance().apply { timeInMillis = millis }
        return when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SATURDAY -> "شنبه"
            Calendar.SUNDAY -> "یکشنبه"
            Calendar.MONDAY -> "دوشنبه"
            Calendar.TUESDAY -> "سه‌شنبه"
            Calendar.WEDNESDAY -> "چهارشنبه"
            Calendar.THURSDAY -> "پنجشنبه"
            Calendar.FRIDAY -> "جمعه"
            else -> ""
        }
    }

    fun formatDayLabel(millis: Long): String {
        val j = fromMillis(millis)
        return "${j.day} ${monthName(j.month)}"
    }

    fun fromMillis(millis: Long): Jalali {
        val cal = Calendar.getInstance().apply { timeInMillis = millis }
        return gregorianToJalali(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH)
        )
    }

    /** شروع ماه تقویمی میلادی فعلی (برای گزارش ماهانه) */
    fun startOfMonth(millis: Long = System.currentTimeMillis()): Long {
        return Calendar.getInstance().apply {
            timeInMillis = millis
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    fun endOfMonth(millis: Long = System.currentTimeMillis()): Long {
        return Calendar.getInstance().apply {
            timeInMillis = millis
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis
    }

    fun startOfYear(millis: Long = System.currentTimeMillis()): Long {
        return Calendar.getInstance().apply {
            timeInMillis = millis
            set(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    fun daysInMonth(millis: Long = System.currentTimeMillis()): Int {
        return Calendar.getInstance().apply { timeInMillis = millis }
            .getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    fun dayMillisInMonth(dayOfMonth: Int, monthMillis: Long = System.currentTimeMillis()): Long {
        return Calendar.getInstance().apply {
            timeInMillis = monthMillis
            set(Calendar.DAY_OF_MONTH, dayOfMonth)
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    fun currentMonthTitle(): String {
        val j = today()
        return "${monthName(j.month)} ${j.year}"
    }

    private fun gregorianToJalali(gy: Int, gm: Int, gd: Int): Jalali {
        var gYear = gy
        val gDays = intArrayOf(0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334)
        var jy = if (gYear <= 1600) 0 else 979
        gYear -= if (gYear <= 1600) 621 else 1600
        val gy2 = if (gm > 2) gYear + 1 else gYear
        var days = (365 * gYear + (gy2 + 3) / 4 - (gy2 + 99) / 100 + (gy2 + 399) / 400 - 80 + gd + gDays[gm - 1]).toLong()
        jy += (33 * (days / 12053)).toInt()
        days %= 12053
        jy += (4 * (days / 1461)).toInt()
        days %= 1461
        if (days > 365) {
            jy += ((days - 1) / 365).toInt()
            days = (days - 1) % 365
        }
        val jm = if (days < 186) (days / 31).toInt() + 1 else ((days - 186) / 30).toInt() + 7
        val jd = if (days < 186) (days % 31).toInt() + 1 else ((days - 186) % 30).toInt() + 1
        return Jalali(jy, jm, jd)
    }
}
