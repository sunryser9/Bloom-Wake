# 🌸 BloomWake — Cycle-Smart Mornings
### The viral women's wellness alarm app built for organic growth

---

## 🚀 What Makes This Top 0.001% — Every Viral Growth System Included

### 1. 📸 THE VIRAL ENGINE — Shareable Phase Cards
Generated natively on-device using Android Canvas API. Beautiful 1080×1920 image:
- Phase name, emoji, gradient background per phase
- Day of cycle prominently shown
- User's streak count
- Phase tagline + superpower
- Science insight
- "Download BloomWake" CTA watermark

**Why it spreads:** Women tap Share → Instagram Stories / WhatsApp / TikTok. 
Friends see it, want to know what phase they're in. Downloads spike.

### 2. ⚡ INSTANT DEMO — Aha Moment in 60 Seconds
Before any sign-up or onboarding, users try a real breathing mission.
- Step 0: "Feel the difference in 60 seconds" — one button
- Step 1: Live animated breathing orb with real timer
- Step 2: Insight + science fact — then "Set up my cycle →"

**Why it converts:** Users feel the app before committing. Cold → warm in 60s.

### 3. 🔬 SCIENCE INTEGRATION
Every mission has a `scienceFact` field with real research citations.
Women share these facts. "Did you know estrogen peaks make you 30% more creative?" 
→ That's a tweet. That's a story caption.

### 4. 🔔 RE-ENGAGEMENT NOTIFICATION LOOP
Three distinct notification types:
- **Phase Transition** — "Tomorrow: Ovulatory peak ✨ You'll feel unstoppable"
- **Midday Nudge** — Phase insight + specific action
- **Streak Protection** — "Don't break your 7-day streak 🔥"

### 5. 📊 ENERGY SCORE BARS
Animated bars for Energy / Mood / Focus / Social per phase.
Visually stunning. Screenshot-worthy. Shareable.

### 6. 🌅 TOMORROW PREVIEW
Home screen shows tomorrow's phase with transition warning.
"⚡ Phase transition tomorrow — Luteal begins, schedule gentleness in."
Creates daily re-opens even without an alarm.

### 7. 🏆 STREAK MILESTONE SYSTEM
Special celebration screens at Day 1, Day 3, Day 7, every 7 days.
"One week! You're cycle-synced! 🏆" → immediately followed by Share button.
Milestone + share = viral loop.

### 8. 🎯 SMART FREEMIUM GATE
Free: Unlimited missions (not gated — users must be hooked first)
Gated: Premium insights, cycle predictions, partner mode (future)
Weekly counter shows remaining missions as social proof, not punishment.

---

## 📱 User Flow

```
App Open
  └── InstantDemoScreen (60-second live mission)
        └── WelcomeScreen
              └── CycleSetupScreen (date picker + cycle length)
                    └── GoalsScreen (Less Stress / Energy / Sleep / Hormones)
                          └── HomeScreen ──────────────────────────────────────┐
                                │                                               │
                                ├── [Set Alarm] → AlarmSetScreen               │
                                ├── [Try Mission] → AlarmRingingScreen          │
                                │     └── MissionScreen                         │
                                │           └── InsightScreen (+ Share Card)   │
                                │                 └── ← HomeScreen ────────────┘
                                └── [Share Card] → ShareCardGenerator → OS Share Sheet
```

---

## 🗂 Project Structure

```
BloomWake/
├── app/src/main/
│   ├── AndroidManifest.xml          ← FileProvider added for image sharing
│   └── java/com/gentlewake/
│       ├── BloomWakeApp.kt         ← Notification channels
│       ├── MainActivity.kt
│       ├── alarm/
│       │   ├── AlarmReceiver.kt
│       │   ├── AlarmScheduler.kt
│       │   ├── AlarmService.kt
│       │   └── NotificationHelper.kt  ← Re-engagement notifications (NEW)
│       ├── data/
│       │   └── UserPreferencesRepository.kt  ← + longestStreak, totalMissions, userName
│       ├── ui/
│       │   ├── navigation/Navigation.kt  ← InstantDemo first
│       │   ├── screens/
│       │   │   ├── InstantDemoScreen.kt  ← NEW: 60-second aha moment
│       │   │   ├── WelcomeScreen.kt
│       │   │   ├── CycleSetupScreen.kt
│       │   │   ├── GoalsScreen.kt
│       │   │   ├── HomeScreen.kt         ← Full viral redesign
│       │   │   ├── AlarmSetScreen.kt
│       │   │   └── AlarmRingingScreen.kt ← Dark theme + science + milestones
│       │   ├── theme/Theme.kt            ← Dark-first Material 3
│       │   └── viewmodel/
│       └── utils/
│           ├── CycleCalculator.kt     ← + tomorrow preview, score bars, share captions
│           ├── MissionGenerator.kt    ← + science facts per mission
│           └── ShareCardGenerator.kt  ← NEW: Canvas-drawn viral image
│   └── res/
│       └── xml/file_paths.xml         ← FileProvider config
```

---

## 🔥 Viral Growth Mechanics — How It Hits Millions

| Mechanic | How It Works | Viral Coefficient |
|---|---|---|
| Phase Cards | Beautiful image → Stories → "What's your phase?" | High |
| Science Facts | Shareable stats about hormones | Medium-High |
| Streak Milestones | "7 days cycle-synced 🏆" + instant share | High |
| Tomorrow Preview | Opens app daily, creates FOMO | Retention |
| Instant Demo | Converts cold visitors before sign-up | Conversion |
| Identity Language | "In my Luteal era 🍂" | Very High |

---

## 🛠 Build Instructions

1. Open `BloomWake/` in Android Studio Hedgehog or later
2. Wait for Gradle sync
3. Run on device or emulator (API 26+, Android 8.0+)

**Required before shipping:**
- Add launcher icons in `res/mipmap-*/`
- Replace `@mipmap/ic_launcher` refs if using custom icon

---

## 📦 Dependencies (all in libs.versions.toml)
- Compose BOM 2024.12.01
- Navigation Compose 2.8.5
- DataStore Preferences 1.1.2
- Material 3 (via Compose BOM)
- Material Icons Extended
- Coroutines 1.9.0
- ViewModel Compose 2.8.7

Zero third-party analytics. Zero ads SDK. 100% on-device.
