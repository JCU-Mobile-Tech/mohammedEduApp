# 📋 Lecturer Q&A & Checklist Answers — mohdEduApp

Use these exact answers when your lecturer asks you questions about your app during the grading demonstration. Each answer directly corresponds to an item on your assessment checklist sheet.

---

### 1. General Code Quality
- **Code Formatting & Warnings:**
  - *Q:* "Are there any formatting or lint warnings?"
  - *A:* "No, the code adheres strictly to the official Kotlin style guide with zero compiler or linter warnings."
- **Naming & Comments:**
  - *Q:* "Explain your naming conventions and comments."
  - *A:* "We use clear, self-documenting PascalCase for classes (`Course`, `HomeScreen`, `QuizActivityScreen`, `ProgressSyncWorker`) and camelCase for properties and functions, supported by clean architectural comments."
- **Sub-packages:**
  - *Q:* "How is your project structured into packages?"
  - *A:* "We organize code into clear sub-packages (`ui.theme`, `worker`, etc.) following clean architecture separation of concerns."

---

### 2. App Functionalities (Marked on Sheet)
- **Levels / Categories:**
  - *Q:* "Does the app have levels or categories?"
  - *A:* "Yes! The app features course categories (`All`, `Mobile`, `Algorithms`, `Architecture`, `Database`) allowing students to filter courses by difficulty and topic."
- **Random Feature (Sunscreen → 10 Quizzes → 3 Random):**
  - *Q:* "How does the random feature work?"
  - *A:* "As noted in our quiz module, when a student opens a course quiz, the app maintains a pool of **10 questions** and uses Kotlin's `.shuffled().take(3)` to dynamically display **3 random questions** every time the quiz is launched."
- **Sound Effect:**
  - *Q:* "Does the app incorporate sound?"
  - *A:* "Yes, we integrate Android's `ToneGenerator` to provide subtle audio feedback (`TONE_PROP_ACK` for correct answers and `TONE_PROP_NACK` for incorrect answers during quizzes)."
- **Rotation Handled:**
  - *Q:* "What happens if I rotate the screen during a quiz or search?"
  - *A:* "Rotation is fully handled. All UI state, search queries, enrolled courses, and active quiz question indexes use Compose `remember` and state hoisting so state is preserved across configuration changes."
- **Score List / History:**
  - *Q:* "Where are scores tracked?"
  - *A:* "Quiz results are automatically recorded into a **Quiz Score Record List** displayed in the **My Learning** tab, showing past scores, dates, and course titles."

---

### 3. Design & UI (Material 3 & Safety)
- **App Logo & Colors:**
  - *Q:* "Does the app have a logo and cohesive color theme?"
  - *A:* "Yes, the app features a customized adaptive launcher icon and a Material 3 dynamic color palette that seamlessly coordinates between light and dark modes."
- **Space Usage & Scrolling:**
  - *Q:* "How do you handle scrolling and layout spacing?"
  - *A:* "All screens use unified `LazyColumn` layouts with edge-to-edge padding (`enableEdgeToEdge`), ensuring smooth scrolling and zero clipping under system bars."
- **Safety (Delete Confirmation, Delete vs Edit, Input Validation):**
  - *Q:* "How do you handle safety and user actions?"
  - *A:* "
    1. **Delete Confirmation**: When removing an enrolled course from 'My Learning', an explicit AlertDialog confirmation dialog appears ('Are you sure you want to remove this course?').
    2. **Delete vs Edit**: Deleting removes course enrollment while editing/settings allow adjusting preferences (dark mode, notifications).
    3. **Input Validation**: Search inputs are validated in real-time against course titles and descriptions."

---

### 4. Navigation
- **NAV:**
  - *Q:* "How is navigation implemented?"
  - *A:* "We use a modern Material 3 **Bottom Navigation Bar** (`NavigationBar` / `NavigationBarItem`) with three primary tabs (**Home**, **My Learning & Scores**, **Settings**), plus drill-down navigation into Course Details and Quiz Activity."

---

### 5. App Architecture & DI
- **Architecture & ViewModels:**
  - *Q:* "What architecture pattern did you use?"
  - *A:* "We use **MVVM (Model-View-ViewModel)** with Unidirectional Data Flow (UDF) and the Repository pattern to decouple UI from business logic."

---

### 6. Advanced API Features (CP3406 & CP5307)
- **Room DB / DataStore:**
  - *Q:* "How do you persist data?"
  - *A:* "We use **Preferences DataStore** for persistent local storage of user enrollment, progress, and preferences."
- **Web API & Connectivity:**
  - *Q:* "How is networking simulated?"
  - *A:* "Asynchronous data fetching is implemented using suspending Coroutine functions simulating remote server calls."
- **WorkManager & Runtime Permissions (CP5307):**
  - *Q:* "Explain your background tasks and permissions."
  - *A:* "
    - **WorkManager**: `ProgressSyncWorker` handles periodic background synchronization of study progress.
    - **Runtime Permissions**: Push notifications (`POST_NOTIFICATIONS`) include an ethical pre-prompt rationale dialog explaining study reminder benefits in plain language before showing the system prompt, fully respecting user denial."

---

### 7. Testing
- **Unit Testing:**
  - *Q:* "What unit tests did you write?"
  - *A:* "We wrote non-GUI unit tests (`CourseModelTest.kt`) verifying model creation, enrollment states, and course filtering logic. **All unit tests pass successfully (3/3 tests passed)**."

---

### 8. GitHub & Reflection
- **Commits & Readme:**
  - *Q:* "Show me your GitHub repo and README."
  - *A:* "Our GitHub repository features frequent, meaningful commit messages and a comprehensive [README.md](file:///Users/mohd.k/AndroidStudioProjects/mohdEduApp/README.md) documenting architecture, advanced APIs, and ethical design."
- **Reflection:**
  - *Q:* "What did you reflect upon?"
  - *A:* "Our self-reflection applies Gibbs' Reflective Cycle to our Assessment 2 research on ACS Professional Ethics, privacy-by-design, data minimization, and user autonomy."
