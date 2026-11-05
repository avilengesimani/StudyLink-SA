# StudyLink SA – Student Academic Management Application

## 1. Project Overview

StudyLink SA is an Android mobile application developed to help students organise and manage their academic activities in one convenient platform.

The application provides students with tools to manage assignments, examinations and timetables, while also supporting learning through flashcards and quizzes. It aims to improve academic organisation, encourage effective study habits and make important academic information easier to access.

This project was developed as part of the **OPSC6312 – Programming Fundamentals** module, focusing on Android application development, database integration, authentication, offline functionality and user-friendly interface design.

## 2. Project Objectives

The main objectives of StudyLink SA are to:

- Provide a secure registration and login system.
- Allow students to manage their academic information.
- Provide flashcards and quizzes to support learning.
- Store and retrieve student information using cloud database services.
- Support access to saved academic information when offline.
- Provide notifications and reminders for important academic activities.
- Support multiple languages to improve accessibility.
- Create a simple and user-friendly mobile experience.

## 3. Main Features

### User Authentication
- Student registration using email and password.
- Secure login through Supabase Authentication.
- Google Sign-In integration.
- User-specific academic information.

### Academic Management
- Assignment management.
- Examination management.
- Timetable management.
- Organisation of important academic activities.

### Learning Tools
- Flashcards to assist with revision.
- Quizzes to test knowledge.
- Learning activities designed to support student progress.

### Database Integration
- Supabase cloud services for authentication and online data management.
- PostgreSQL database accessed through the Supabase API.
- Room database for local storage of academic records.

### Offline Functionality
- Locally stored academic information can be accessed without an internet connection.
- WorkManager supports background synchronisation when connectivity becomes available.

### Notifications
- Notifications and reminders for academic activities.
- Firebase Cloud Messaging integration for push notification functionality.

### Language Support
The application includes language options for:
- English
- isiZulu
- isiXhosa

### User Settings
- Manage application preferences.
- Change language settings.
- Configure notification preferences.
- Update personal display information.

## 4. Technologies Used

| Technology | Purpose |
|---|---|
| Android Studio | Android application development |
| Kotlin | Main programming language |
| XML | User interface layouts |
| Supabase | Authentication and cloud database services |
| PostgreSQL | Online relational database |
| Room | Local database storage |
| SQLite | Local data persistence |
| WorkManager | Background synchronisation |
| Firebase Cloud Messaging | Push notification functionality |
| Retrofit | API communication |
| Git and GitHub | Version control and source code management |
| Gradle | Project build and dependency management |

## 5. Application Requirements

To run the project, the following software is required:

- Android Studio.
- Android SDK.
- Kotlin support.
- An Android device or emulator.
- Internet connection for cloud-based features.
- A configured Supabase project.
- Firebase configuration for push notification functionality.

**Minimum Android SDK:** API 24

**Application ID:** `com.example.studylinksa`

## 6. How to Install and Run the Application

1. Download or clone the StudyLink SA repository from GitHub.
2. Open Android Studio.
3. Select **Open** and choose the downloaded project folder.
4. Allow Android Studio to synchronise the Gradle files.
5. Configure the required Supabase and Google Sign-In credentials using the configuration expected by the project.
6. Add the required Firebase configuration file if it is not already included.
7. Connect an Android device or start an emulator.
8. Select the application and click **Run**.
9. Register an account or sign in to access the application.

**Important:** Do not upload private API keys, service-role keys, passwords or other confidential credentials to GitHub. Use the appropriate local configuration files for development.

## 7. Database and API Integration

StudyLink SA uses Supabase services to support online authentication and academic data management.

The application communicates with the Supabase backend to access information such as:

- Student profiles.
- Assignments.
- Examinations.
- Timetable information.

Room provides local storage for academic records, while WorkManager is used to support background synchronisation when an internet connection is available.

The application also uses external API functionality to retrieve quiz questions.

## 8. Project Structure

The project follows an Android application structure using Kotlin source files and XML layouts.

Main application components include:

- `DashboardActivity` – Main application dashboard.
- `SettingsActivity` – Application settings.
- `TimetableActivity` – Timetable management.
- `AssignmentsActivity` – Assignment management.
- `ExamsActivity` – Examination management.
- `FlashcardActivity` – Flashcard learning functionality.
- `QuizActivity` – Quiz functionality.
- `AcademicRepository` – Academic data management.
- `AcademicDao` – Local database access.
- `AcademicSyncWorker` – Background data synchronisation.
- `NotificationHelper` – Notification management.

## 9. Testing

The application should be tested on an Android emulator and a physical Android device.

The following areas are important during testing:

- User registration and login.
- Google Sign-In.
- Academic record creation and retrieval.
- Database connectivity.
- Offline access and data synchronisation.
- Notification delivery.
- Language selection.
- User settings.
- Input validation and error handling.
- Navigation between application screens.

Testing should also include invalid inputs, unavailable internet connections and other situations that could affect application stability.

## 10. Screenshots and Demonstration Video

Screenshots of the completed application should be added to this section to demonstrate its interface and functionality.

Suggested screenshots include:

1. Login screen.
2. Registration screen.
3. Dashboard.
4. Assignments screen.
5. Examinations screen.
6. Timetable screen.
7. Flashcards screen.
8. Quiz screen.
9. Settings and language selection.

**Demonstration video:**  
[Insert your unlisted YouTube demonstration video link here.]

The demonstration video should include a voice-over explaining the main features and showing the application running on an Android device.

## 11. GitHub and Version Control

GitHub is used to store and manage the StudyLink SA source code.

The repository supports version control and provides a record of the application's development.

**GitHub Repository:**  
https://github.com/avilengesimani/StudyLink-SA

The final repository should contain:

- Complete Kotlin source code.
- XML layout files.
- Required application resources.
- Gradle configuration files.
- README documentation.
- Automated tests.
- GitHub Actions workflow, where configured.
- Release notes and final project version.

## 12. Future Improvements

Possible future improvements include:

- Expanding language translations across all application screens.
- Improving cloud database integration and synchronisation.
- Expanding automated testing.
- Improving notification scheduling and delivery.
- Adding further learning and academic tracking tools.
- Completing Google Play Store preparation and publication requirements.
- Improving application security and performance.

## 13. Conclusion

StudyLink SA was developed to provide students with a centralised platform for managing academic activities and supporting their learning. Through features such as authentication, academic management, quizzes, flashcards, database integration and notifications, the application aims to make student organisation more convenient and accessible.

The project also provided practical experience in Android development, Kotlin programming, API integration, database management, testing and version control. Further improvements can strengthen the application's functionality, reliability and readiness for wider use.

## 14. Developer and Academic Information

**Project Name:** StudyLink SA  
**Module:** OPSC6312 – Programming Fundamentals  
**Assessment:** Part 3 – Final Asset Development, Additions, Tweaking and Final Submission  
**Development Platform:** Android Studio  
**Programming Language:** Kotlin  
**Repository:** https://github.com/avilengesimani/StudyLink-SA
