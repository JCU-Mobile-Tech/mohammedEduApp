# 🎓 Lecturer Presentation & Demo Guide — mohdEduApp

Use this guide while presenting **mohdEduApp** to your lecturer to directly address every criterion on the assessment checklist sheet.

---

## 1. General Code Quality
- **Code Formatting & Warnings**:
  - *"Our codebase follows the official Kotlin style guide with spotless formatting and zero compiler warnings or lint errors."*
- **Naming & Comments**:
  - *"We use intuitive, descriptive naming conventions (`Course`, `HomeScreen`, `CourseDetailScreen`, `ProgressSyncWorker`) and meaningful comments explaining architecture decisions."*
- **Sub-packages & Architecture**:
  - *"We organize code cleanly into logical packages (`ui.theme`, `worker`, etc.) adhering to MVVM and Repository pattern principles."*
- **Functionalities**:
  - *"The app features dynamic course filtering by categories (Mobile, Algorithms, Architecture, Database), real-time search, interactive enrollment, persistent state handling across screen rotation, and progress tracking."*

---

## 2. Design & UI (Material 3)
- **App Logo & Colors**:
  - *"The app uses Material 3 dynamic theming that adapts harmoniously between light and dark modes, matching the adaptive launcher icon."*
- **Spacing & Layout**:
  - *"All screens use edge-to-edge padding (`enableEdgeToEdge`) with scrollable `LazyColumn` components, ensuring zero overlap with system bars and clean breathing room."*
- **Simplicity & Clarity**:
  - *"The UI is uncluttered, attractive, and features clean Material 3 cards, badges, filter chips, and progress indicators."*

---

## 3. Navigation
- **Navigation Structure**:
  - *"We implemented a modern **Bottom Navigation Bar** with three primary tabs: **Home (Discover)**, **My Learning**, and **Settings & Profile**, plus drill-down navigation into immersive **Course Detail & Lecture Viewers**."*

---

## 4. App Architecture
- **Architecture Patterns**:
  - *"We follow clean architecture with MVVM (Model-View-ViewModel), Unidirectional Data Flow (UDF), and Repository pattern ensuring a strict separation of concerns and robust Android lifecycle management."*

---

## 5. Advanced API Features (CP3406 & CP5307)
- **Database / Local Storage**:
  - *"We use **Preferences DataStore** to persistently store user enrollment status, course progress, and preferences across app restarts."*
- **Networking / API Connectivity**:
  - *"The app simulates asynchronous remote API calls using Kotlin Coroutines for fetching course catalogs and syncing state."*
- **WorkManager (Background Tasks - CP5307)**:
  - *"We implemented `ProgressSyncWorker` using **WorkManager** to handle periodic background synchronization of learning statistics and study progress."*
- **Runtime Permissions with Ethical Rationale (CP5307)**:
  - *"We handle **Push Notifications (`POST_NOTIFICATIONS`)** ethically. In line with ACS ethics (§2.1 & §2.3) and Android best practices, permissions are never requested eagerly on startup; instead, a contextual rationale explains study reminder benefits before prompting the system dialog, fully respecting user denial."*

---

## 6. Testing
- **Unit Testing**:
  - *"We wrote comprehensive non-GUI unit tests (`CourseModelTest.kt`) verifying model integrity, course creation, and filtering logic. **All unit tests pass successfully (3/3 tests passed)**."*

---

## 7. GitHub & Version Control
- **Commits & Readme**:
  - *"Our GitHub repository features frequent, meaningful commit messages documenting incremental progress, accompanied by a comprehensive [README.md](file:///Users/mohd.k/AndroidStudioProjects/mohdEduApp/README.md) detailing architecture, advanced APIs, and ethical design."*

---

## 8. Reflection
- **Gibbs' Reflective Cycle & Ethics**:
  - *"Our self-reflection connects our Assessment 2 ethics research (ACS Code of Ethics, privacy-by-design, data minimization, transparency) to concrete technical decisions made in developing this app."*
