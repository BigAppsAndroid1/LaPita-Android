package il.co.lapita.data

import il.co.lapita.R
import il.co.lapita.utilities.getString

data class Time(
    val id: Int,
    val day: String,
    val from: String,
    val to: String,
    val date: Long?
) {
    fun dayLocalized(): String {
        return when (day) {
            "sunday" -> getString(R.string.sunday)
            "monday" -> getString(R.string.monday)
            "tuesday" -> getString(R.string.tuesday)
            "wednesday" -> getString(R.string.wednesday)
            "thursday" -> getString(R.string.thursday)
            "friday" -> getString(R.string.friday)
            "saturday" -> getString(R.string.saturday)
            else -> null
        } ?: day
    }
}