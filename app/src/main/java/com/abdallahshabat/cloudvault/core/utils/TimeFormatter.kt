package com.abdallahshabat.cloudvault.core.utils

import java.util.concurrent.TimeUnit

object TimeFormatter {

    fun getTimeAgo(timestamp: Long): String {

        val now = System.currentTimeMillis()

        val difference = now - timestamp

        val seconds =
            TimeUnit.MILLISECONDS.toSeconds(difference)

        val minutes =
            TimeUnit.MILLISECONDS.toMinutes(difference)

        val hours =
            TimeUnit.MILLISECONDS.toHours(difference)

        val days =
            TimeUnit.MILLISECONDS.toDays(difference)


        return when {

            seconds < 60 ->
                "Just now"

            minutes < 60 ->
                "$minutes minutes ago"

            hours < 24 ->
                "$hours hours ago"

            days == 1L ->
                "Yesterday"

            else ->
                "$days days ago"

        }

    }

}