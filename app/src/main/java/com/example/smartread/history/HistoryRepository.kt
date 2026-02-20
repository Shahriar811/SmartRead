package com.example.smartread.history

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray

class HistoryRepository(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _sessions = MutableStateFlow(loadSessions())
    val sessions: StateFlow<List<StudySessionRecord>> = _sessions.asStateFlow()

    fun addSession(record: StudySessionRecord) {
        val list = _sessions.value + record
        _sessions.value = list
        saveSessions(list)
    }

    fun clearHistory() {
        _sessions.value = emptyList()
        saveSessions(emptyList())
    }

    fun getSubjectSuggestions(): List<String> {
        val subjects = _sessions.value.map { it.subject }.distinct()
        return (listOf(DEFAULT_SUBJECT) + subjects).distinct()
    }

    private fun loadSessions(): List<StudySessionRecord> {
        val json = prefs.getString(KEY_SESSIONS, null) ?: return emptyList()
        return try {
            val arr = JSONArray(json)
            List(arr.length()) { i -> StudySessionRecord.fromJson(arr.getJSONObject(i)) }
                .sortedByDescending { it.completedAt }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun saveSessions(list: List<StudySessionRecord>) {
        val arr = JSONArray()
        list.forEach { arr.put(it.toJson()) }
        prefs.edit().putString(KEY_SESSIONS, arr.toString()).apply()
    }

    companion object {
        private const val PREFS_NAME = "smartread_history"
        private const val KEY_SESSIONS = "sessions"
        const val DEFAULT_SUBJECT = "General"
    }
}
