package com.example.smartread.history

import org.json.JSONObject
import java.util.Date

/**
 * A single recorded study session: subject, study/reward times, and total study seconds for this block.
 */
data class StudySessionRecord(
    val id: Long,
    val subject: String,
    val studyMinutes: Int,
    val rewardMinutes: Int,
    val totalStudySeconds: Long,
    val completedAt: Long
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("id", id)
        put("subject", subject)
        put("studyMinutes", studyMinutes)
        put("rewardMinutes", rewardMinutes)
        put("totalStudySeconds", totalStudySeconds)
        put("completedAt", completedAt)
    }

    companion object {
        fun fromJson(obj: JSONObject): StudySessionRecord = StudySessionRecord(
            id = obj.getLong("id"),
            subject = obj.getString("subject"),
            studyMinutes = obj.getInt("studyMinutes"),
            rewardMinutes = obj.getInt("rewardMinutes"),
            totalStudySeconds = obj.getLong("totalStudySeconds"),
            completedAt = obj.getLong("completedAt")
        )
    }
}
