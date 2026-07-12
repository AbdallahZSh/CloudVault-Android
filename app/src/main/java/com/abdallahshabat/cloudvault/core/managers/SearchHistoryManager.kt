package com.abdallahshabat.cloudvault.core.managers

import android.content.Context

object SearchHistoryManager {

    private const val PREF = "search_history"

    private const val KEY = "history"

    fun save(
        context: Context,
        query: String
    ) {

        if (query.isBlank())
            return

        val pref =
            context.getSharedPreferences(
                PREF,
                Context.MODE_PRIVATE
            )

        val history =
            pref.getStringSet(KEY, mutableSetOf())
                ?.toMutableSet()
                ?: mutableSetOf()

        history.remove(query)

        history.add(query)

        pref.edit()
            .putStringSet(KEY, history)
            .apply()

    }

    fun getHistory(
        context: Context
    ): List<String> {

        return context
            .getSharedPreferences(
                PREF,
                Context.MODE_PRIVATE
            )
            .getStringSet(KEY, emptySet())
            ?.toList()
            ?.reversed()
            ?: emptyList()

    }

}