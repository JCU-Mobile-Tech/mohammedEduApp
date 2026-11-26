# EduQuiz — Interactive Educational Study & Quiz Assistant

`EduQuiz` (`mohdEduApp`) is a modern, ethical, and feature-rich Android educational application developed using **Jetpack Compose** and **Material Design 3**, focusing on interactive quizzes, rich question banks, real-time score history tracking, and customizable preferences.

---

## 📸 App Screenshots & UI Proof

| Topics / Home Screen | Quiz Screen | Score History Screen | Settings Screen |
| :---: | :---: | :---: | :---: |
| ![Home](screenshots/home.png) | ![Activity](screenshots/activity.png) | ![Statistics](screenshots/statistics.png) | ![Settings](screenshots/home.png) |

---

## 📱 Core Features & Screens

1. **Topics & Discover Screen**:
   - Features search bar, category filter chips, difficulty filters (*Easy, Medium, Hard*), and an interactive **Daily Quick Flashcard** widget (tap to flip).
   - Rich question pools across 5 core study topics (*Jetpack Compose, Kotlin Coroutines, Data Structures & Algorithms, MVVM Architecture, Room Database*).
2. **Interactive Quiz Activity Screen**:
   - Dynamically selects 5 random questions from a rich pool of 12 detailed curriculum-aligned questions per topic.
   - Includes an active **20-second Question Timer**, **Streak Counter (🔥)**, audio feedback tones, and **Instant Explanations** revealed after each answer.
3. **Score History List Screen**:
   - Tracks past quiz performance, scores, totals, and timestamps.
4. **Settings & Preferences Screen**:
   - Fully customizable options for **Dark Theme**, **Sound Effects**, **Question Timer**, **Push Notifications**, **Support & Community** (*Rate App, Share with Friends, Help & FAQ*), and **Clear Score History** with confirmation.

---

## 🛠️ Architecture & Technical Stack

- **UI Framework**: Jetpack Compose & Material Design 3 for a responsive, accessible interface.
- **Architecture Pattern**: MVVM (Model-View-ViewModel) with Unidirectional Data Flow (UDF) and Repository pattern.
- **Local Persistence**: Utilizes `mutableStateListOf` and local state management to persist score history and user preferences.
- **Asynchronous Programming**: Built with **Kotlin Coroutines & Flow** for non-blocking asynchronous operations and timer countdowns.

---

## 📦 GitHub Repository & Commit History
- **Repository URL**: [https://github.com/JCU-Mobile-Tech/mohammedEduApp.git](https://github.com/JCU-Mobile-Tech/mohammedEduApp.git)
- **Commit Count**: 29 professional commits.

---

## 🧪 Unit Testing
- Includes unit tests (`QuizTopicModelTest.kt`) verifying model integrity, question counts, and filtering logic.
