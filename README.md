# SmartRead – Study & Reward Timer

A simple Android app to study in focused blocks and take short reward breaks. Set your study time and reward time; the app runs **Study → Reward → Study** in a loop so you keep a steady rhythm.

![Android](https://img.shields.io/badge/Platform-Android-green)  
![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF)  
![Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4)

---

## Features

| Feature | Description |
|--------|-------------|
| **Study & reward timers** | Set study (1–120 min) and reward (1–60 min). Large countdown shows current phase and remaining time. Start, Pause, Resume, Reset. |
| **Subject tracking** | Assign a subject (e.g. Math, Physics) per session. Use “Only one subject (General)” or switch subjects. Active subject shown while the timer runs. |
| **History** | Completed study blocks are saved with subject, times, and date. Open **History** from the header to review past sessions. |
| **Study & exam tips** | Open **Tips** (lightbulb) for short advice on time management, study techniques, focus & memory, exam day, and mindset. |
| **Notifications & alarm** | When a phase ends you get a notification and a 10-second alarm, even if the app is in the background (notification permission on Android 13+). |
| **Light / dark theme** | Toggle theme with the sun/moon button in the header. |
| **Custom times** | Use +/- or type values in the study and reward fields; validation keeps values in range. |

---

## Screenshots

_Add screenshots of the timer screen, settings, history, and tips here._

---

## Tech Stack

- **Language:** Kotlin  
- **UI:** Jetpack Compose, Material 3  
- **Architecture:** ViewModel + StateFlow for timer state  
- **Persistence:** SharedPreferences + JSON for history  
- **Min SDK:** 26 | **Target / Compile SDK:** 36  

---

## Getting Started

### Prerequisites

- Android Studio (latest stable)
- JDK 11+
- Android device or emulator (API 26+)

### Clone & run

```bash
git clone https://github.com/YOUR_USERNAME/SmartRead.git
cd SmartRead
```

Open the project in Android Studio, sync Gradle, then run on a device or emulator (Run ▶️).

### Build release APK

```bash
./gradlew assembleRelease
```

APK output: `app/build/outputs/apk/release/`

---

## How to use

1. **Set times** – When idle, set study and reward minutes (and subject if not using “Only one subject”).
2. **Start** – Tap **Start** to begin the study countdown.
3. **Phase switch** – When study ends, reward countdown starts; when reward ends, study starts again. Notifications and alarm fire at each phase change.
4. **Pause / Resume** – Use **Pause** and **Resume** as needed.
5. **Reset** – **Reset** returns to idle and keeps your study/reward/subject settings.
6. **History** – Tap the history icon to see past sessions.
7. **Tips** – Tap the lightbulb to read study and exam tips.

---

## Project structure

```
app/src/main/
├── java/com/example/smartread/
│   ├── MainActivity.kt           # Entry, theme, notifications/sound wiring
│   ├── history/
│   │   ├── StudySessionRecord.kt # Session data + JSON
│   │   └── HistoryRepository.kt  # Load/save history
│   ├── timer/
│   │   ├── TimerPhase.kt         # Idle, Study, Reward
│   │   ├── StudyTimerViewModel.kt # Timer state & logic
│   ├── tips/
│   │   └── StudyTips.kt         # Tips content
│   ├── ui/
│   │   ├── TimerScreen.kt        # Main UI, settings, countdown, history/tips sheets
│   │   └── theme/               # Colors, typography, theme
│   └── utils/
│       ├── NotificationHelper.kt
│       └── AlarmSoundHelper.kt
└── res/
    ├── drawable-nodpi/          # App logo
    ├── mipmap-anydpi/           # Launcher icons
    └── values/                  # Strings, themes
```

---

## License

This project is open source. Use it for learning or as a base for your own study timer.

---

**SmartRead** – Study for a set time, take a short reward, repeat.
