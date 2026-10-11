package com.hachaton.walkinghistory.ui.screens

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class HistoryItem(val placeName: String, val type: String, val time: String)
data class DailyHistory(val date: String, val items: List<HistoryItem>)

class HistoryManager(context: Context) {
    private val prefs = context.getSharedPreferences("walking_history_prefs", Context.MODE_PRIVATE)

    fun addEntry(placeName: String, isFromFavorites: Boolean) {
        val type = if (isFromFavorites) "Przesłuchano (Ulubione)" else "Odwiedzono na trasie"
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val now = Date()
        val dateStr = dateFormat.format(now)
        val timeStr = timeFormat.format(now)

        val entryStr = "$dateStr|$timeStr|$placeName|$type"

        val currentHistory = prefs.getString("history_list", "") ?: ""
        val newHistory = if (currentHistory.isEmpty()) entryStr else "$entryStr;;;$currentHistory"

        prefs.edit().putString("history_list", newHistory).apply()
    }

    fun getHistory(): List<DailyHistory> {
        val currentHistory = prefs.getString("history_list", "") ?: ""
        if (currentHistory.isEmpty()) return emptyList()

        val entries = currentHistory.split(";;;")
        val parsed = entries.mapNotNull {
            val parts = it.split("|")
            if (parts.size == 4) {
                Pair(parts[0], HistoryItem(parts[2], parts[3], parts[1]))
            } else null
        }

        val grouped = mutableMapOf<String, MutableList<HistoryItem>>()
        for (p in parsed) {
            if (!grouped.containsKey(p.first)) {
                grouped[p.first] = mutableListOf()
            }
            grouped[p.first]!!.add(p.second)
        }

        return grouped.map { DailyHistory(it.key, it.value) }
    }
}