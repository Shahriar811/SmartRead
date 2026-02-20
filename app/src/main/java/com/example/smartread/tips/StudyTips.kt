package com.example.smartread.tips

/**
 * Study and exam tips to help users perform better.
 */
data class TipCategory(
    val emoji: String,
    val title: String,
    val tips: List<String>
)

object StudyTipsData {
    val categories: List<TipCategory> = listOf(
        TipCategory(
            emoji = "⏱️",
            title = "Time Management",
            tips = listOf(
                "Use the Pomodoro technique: study for 25–30 min, then take a 5 min break.",
                "Tackle the hardest subjects when you're most alert (usually morning).",
                "Block specific times for study and treat them like fixed appointments.",
                "Avoid cramming; spread revision over days or weeks for better retention."
            )
        ),
        TipCategory(
            emoji = "📝",
            title = "Study Techniques",
            tips = listOf(
                "Summarize in your own words after each section to check understanding.",
                "Use flashcards or spaced repetition apps for facts and definitions.",
                "Teach the topic to someone else (or out loud) to find gaps.",
                "Practice with past papers or similar questions under exam conditions.",
                "Draw mind maps or diagrams to see how ideas connect."
            )
        ),
        TipCategory(
            emoji = "🧠",
            title = "Focus & Memory",
            tips = listOf(
                "Study in a quiet place; use earplugs or focus music if needed.",
                "Put your phone away or use app blockers during study blocks.",
                "Get 7–8 hours of sleep; sleep helps consolidate what you learned.",
                "Take short walks or stretch between sessions to stay fresh.",
                "Review material within 24 hours, then again after a few days."
            )
        ),
        TipCategory(
            emoji = "📋",
            title = "Exam Day",
            tips = listOf(
                "Read all instructions and questions before you start writing.",
                "Answer easy questions first to build confidence and secure marks.",
                "Manage time: divide total time by number of questions and stick to it.",
                "Leave 5–10 minutes at the end to check answers and fix mistakes.",
                "Eat a light meal and stay hydrated; avoid heavy or new foods."
            )
        ),
        TipCategory(
            emoji = "💪",
            title = "Mindset & Health",
            tips = listOf(
                "Stay positive; remind yourself of past successes and progress.",
                "Exercise regularly; even 15–20 min can improve mood and focus.",
                "Take real breaks: step away from the desk and relax fully.",
                "Ask teachers or friends when stuck; don’t let doubts pile up.",
                "Reward yourself after completing goals to stay motivated."
            )
        )
    )
}
