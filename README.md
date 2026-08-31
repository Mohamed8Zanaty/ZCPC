# ⚡ ZCPC

A modern Android companion app for competitive programmers. Currently supporting **Codeforces**, ZCPC helps you track your performance, analyze your mistakes, and stay ahead of the competition.

Built entirely with modern Android development best practices, including **Jetpack Compose** and **Clean Architecture**.

## ✨ Features

- **Personalized Dashboard:** Enter your Codeforces handle once (persisted via DataStore) to instantly load your profile on every launch.
- **Problem Analytics:** Visualize your progress with rating distribution charts and topics mastery breakdown.
- **Fail-Fast Analysis:** A dedicated section for problems you've attempted but haven't solved yet. Turn your failures into victories!
- **Rival Tracking:** Add rivals to track their progress and see what problems they are struggling with in real-time.
- **Offline-First Caching:** Profile and contest data is aggressively cached using Room, ensuring availability even without an internet connection.
- **Live Contest Schedule:** Fetches upcoming and currently running contests.
- **One-Tap Access:** Open any problem or contest registration page securely via Chrome Custom Tabs—without losing your active browser session.

## 🛠️ Tech Stack & Architecture

This project strictly follows the **Clean Architecture** pattern (Data ↔ Domain ↔ Presentation) using a unidirectional data flow (MVI-inspired).

- **UI:** Jetpack Compose, Material Design 3, Type-Safe Navigation Compose
- **Architecture:** Clean Architecture, ViewModel, StateFlow
- **Dependency Injection:** Dagger Hilt
- **Network:** Retrofit, OkHttp, Kotlinx Serialization
- **Local Storage:** Room Database, Preferences DataStore
- **Async/Threading:** Kotlin Coroutines & Flow
- **Image Loading:** Coil
- **Web Integration:** AndroidX Browser (Chrome Custom Tabs)

## 📸 Screenshots
<p align="center">
  <img src="doc/profile.png" width="250">
  <img src="doc/contests.png" width="250">
</p>

## 🚀 How to Run

1. Clone the repository:
   ```bash
   git clone https://github.com/Mohamed8Zanaty/ZCPC.git
2. Open in Android Studio (Ladybug or newer).
3. Build and Run!
