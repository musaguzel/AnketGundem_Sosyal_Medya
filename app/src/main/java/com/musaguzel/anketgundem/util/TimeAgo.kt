package com.musaguzel.anketgundem.util

class TimeAgo {

    companion object{
        private val SECOND_MILLIS: Int = 1000
        private val MINUTE_MILLIS : Int = 60 * SECOND_MILLIS
        private val HOUR_MILLIS : Int = 60 * MINUTE_MILLIS
        private val DAY_MILLIS : Int = 24 * HOUR_MILLIS

        fun getTimeAgo(timee: Long) : String? {
            var time = timee
            if (time < 1000000000000L) {
                time *= 1000
            }

            val now: Long = System.currentTimeMillis()
            if (time > now || time <= 0) {
                return null
            }

            val diff = now - time
            if (diff < MINUTE_MILLIS) {
                return "just now"
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago"
            } else if (diff < 50 * MINUTE_MILLIS) {
                return "${diff/ MINUTE_MILLIS} minutes ago"
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago"
            } else if (diff < 24 * HOUR_MILLIS) {
                return "${diff / HOUR_MILLIS} hours ago"
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return "${diff / DAY_MILLIS} days ago"
            }
        }
    }
}