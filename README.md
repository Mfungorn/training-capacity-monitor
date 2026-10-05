# Training Capacity Monitor

A comprehensive fitness tracking application built with Kotlin Multiplatform, designed to help athletes and fitness enthusiasts monitor their training capacity, manage mesocycles, and track workout progress across Android and iOS platforms.

<img width="315" height="640" alt="telegram-cloud-photo-size-2-5339508114555871007-y" src="https://github.com/user-attachments/assets/f5cce319-19d6-4131-851f-d0bbd7935ea4" />
<img width="315" height="640" alt="telegram-cloud-photo-size-2-5339508114555871013-y" src="https://github.com/user-attachments/assets/c04edcb8-df2f-4836-9212-8184b08f1bbc" />
<img width="315" height="640" alt="telegram-cloud-photo-size-2-5339508114555871009-y" src="https://github.com/user-attachments/assets/3c31e5e3-7470-485d-9107-61b057efd1f7" />


## Features

- **Dashboard**: Visual overview of training capacity with difficulty graphs and performance metrics
- **Mesocycle Management**: Create, edit, and track training mesocycles with customizable parameters
- **Training Programs**: TBD
- **Workout Form**: Log daily workouts with RIR (Reps in Reserve) tracking and capacity calculations
- **Data Visualization**: Interactive graph showing training difficulty trends over time
- **Cross-Platform**: Shared codebase ensures consistent experience across Android and iOS

## Technology Stack

### Core Technologies
- **Kotlin Multiplatform**: Share business logic across platforms
- **Compose Multiplatform**: Declarative UI framework for cross-platform development
- **Material3 Design**: Modern UI components with dark/light theme support

### Architecture
- **MVIKotlin**: Model-View-Intent architecture for predictable state management
- **Decompose**: Navigation component with lifecycle management
- **Koin**: Dependency injection framework
- **Coroutines & Flow**: Asynchronous programming and reactive data streams

### Data Layer
- **DataStore**: Local database with type-safe SQL queries
- **Repository Pattern**: Clean separation between data sources and business logic
- **Use Cases**: Domain-specific business logic encapsulation

## Project Structure

* [/composeApp](./composeApp/src) - Shared code across all platforms
  - [commonMain](./composeApp/src/commonMain/kotlin) - Common business logic, UI, and domain layer
  - [androidMain](./composeApp/src/androidMain/kotlin) - Android-specific implementations
  - [iosMain](./composeApp/src/iosMain/kotlin) - iOS-specific implementations

* [/core](./core/) - Shared modules
  - [common](./core/common/) - Common utilities and base classes
  - [domain](./core/domain/) - Domain models and use cases
  - [data](./core/data/) - Data layer with repositories and local storage
  - [ui](./core/ui/) - Shared UI components and utilities

* [/feature](./feature/) - Feature modules
  - [dashboard](./feature/dashboard/) - Main dashboard with graphs and metrics
  - [mesocycles](./feature/mesocycles/) - Mesocycle management
  - [form](./feature/form/) - Workout logging form
  - [programs](./feature/programs/) - Training program selection

* [/iosApp](./iosApp/iosApp) - iOS application entry point with SwiftUI integration

### Build and Run Android Application

To build and run the development version of the Android app, use the run configuration from the run widget
in your IDE’s toolbar or build it directly from the terminal:
- on macOS/Linux
  ```shell
  ./gradlew :androidApp:assembleDebug
  ```
- on Windows
  ```shell
  .\gradlew.bat :androidApp:assembleDebug
  ```

### Build and Run iOS Application

To build and run the development version of the iOS app, use the run configuration from the run widget
in your IDE’s toolbar or open the [/iosApp](./iosApp) directory in Xcode and run it from there.

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)
