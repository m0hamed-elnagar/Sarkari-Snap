# Taaza Today – Multi-Flavor, Multi-Language News & Short-Video Android Platform

> Kotlin-first Android application delivering real-time Hindi & English news, short-form videos ("Shorts"), and push-driven content discovery — built with admin & consumer product flavors.

---

## 1. Value Proposition

India's regional-language users need bilingual news that works seamlessly across different content types. Taaza Today solves this by:

* **Unified Content Aggregation** - Bringing together blog articles and YouTube shorts in one cohesive experience
* **Seamless Language Support** - Instant Hindi ⇄ English switching with persistent user preferences
* **Targeted Notifications** - Topic-based push notifications (FCM) ensuring users receive relevant content
* **Dual-App Strategy** - Separate admin and consumer builds for content management vs. consumption
* **Scalable Architecture** - Production-ready foundation supporting future growth and features

**Who benefits?**
* **End Users** - Clean, fast access to bilingual news and video content
* **Content Teams** - Dedicated admin tools for real-time content management
* **Business** - Modular architecture supporting future monetization and analytics

---

## 2. Tech Stack / Skills Used

| Category | Technology |
|---|---|
| **Language** | Kotlin 100% (coroutines, flow, sealed classes) |
| **UI Framework** | Jetpack Compose (Material 3) |
| **Architecture** | MVVM + Clean Architecture (Presentation → Domain ← Data) |
| **Dependency Injection** | Koin with flavor-specific modules |
| **Networking** | Ktor client + Kotlinx-Serialization |
| **Database** | Room with TypeConverters, upsert operations |
| **State Management** | Kotlin Coroutines + Flow (StateFlow/SharedFlow) |
| **Push Notifications** | Firebase Cloud Messaging with topic segmentation |
| **Localization** | Dynamic language switching with persistent storage |
| **Build System** | Gradle product flavors (`admin`, `user`) × build types |
| **Image Loading** | Coil for efficient image handling |
| **Content Integration** | YouTube Android Player, custom embed handling |

---

## 3. Engineering Highlights

### **Advanced Database Architecture**
```kotlin
@Dao
interface FavoritePostDao {
    fun getAllFavoritePosts(): Flow<List<PostEntity>>
    suspend fun upsert(post: PostEntity)
    suspend fun getFavoritePost(id: String): PostEntity?
}
```
- **Reactive Data Streams** - Flow for real-time UI updates
- **Type-Safe Queries** - Room's compile-time verification
- **Efficient Operations** - Suspend functions for async database operations

### **Scalable Notification System**
```kotlin
object FcmSender{
    suspend fun sendNotification(
        context: Context,
        targetToken: String,
        title: String,
        body: String,
        deeplink: String
    ): Result
}
```
- **Multi-language Topics** - Support for English and Hindi notification channels
- **Priority Management** - High/normal priority levels for critical updates
- **Deep Linking** - Seamless navigation to specific content

### **Modern UI Architecture**
```kotlin
@Composable
fun NotificationInputDialog(
    onDismiss: () -> Unit,
    onSend: (token: String, title: String, body: String, deeplink: String) -> Unit
) {
    // State management with remember and mutableStateOf
    // Dependency injection with Koin
}
```
- **Declarative UI** - Jetpack Compose with Material Design 3
- **State Hoisting** - Clean separation of UI and business logic
- **Dependency Injection** - Koin for testable and maintainable code

### **Multi-Flavor Build System**
- **Product Flavors** - `admin` and `user` variants with shared codebase
- **Conditional Features** - Admin-specific tools like notification composer
- **Build Optimization** - Efficient resource and code sharing between flavors

---

## 4. Key Features

### **Core Functionality**
- **Bilingual Content** - English and Hindi news articles with seamless switching
- **Short-Form Video** - Integrated YouTube shorts with in-app playback
- **Content Management** - Favorite posts and shorts with cross-session persistence
- **Push Notifications** - Topic-based alerts with priority management

### **User Experience**
- **Modern Material Design** - Clean, intuitive interface following latest guidelines
- **Smooth Navigation** - Deep linking support for content sharing
- **Performance Optimized** - Efficient loading and smooth scrolling
- **Accessibility Ready** - Built with accessibility best practices

### **Admin Capabilities**
- **Notification Composer** - Targeted push notification creation tool
  ![Home Screen in admin](assets/home_admin.jpg)
*Home Screen showing Hindi & English news toggle*


[//]: # (- **Content Management** - Tools for content curation and management)
- **Analytics Ready** - Foundation for engagement tracking

---

## 5. Installation & Setup

### Prerequisites
- Android Studio Hedgehog or later
- Android SDK API 21+
- Kotlin 1.9.0+

### Build Instructions
```bash
# Clone the repository
git clone https://github.com/your-username/taaza-today.git

# Build user variant
./gradlew assembleUserDebug

# Build admin variant  
./gradlew assembleAdminDebug

# Install on device
adb install app/build/outputs/apk/user/debug/app-user-debug.apk
```

### Configuration
1. **Firebase Setup** - Add `google-services.json` to app module
2. **API Configuration** - Configure content source endpoints
3. **Build Selection** - Choose between admin or user flavors

---

## 6. Architecture & Code Quality

### **Clean Architecture Implementation**
```
app/
├── src/
│   ├── admin/          # Admin-specific features
│   ├── user/           # User-facing features  
│   ├── main/           # Shared code
│   └── shared/         # Common components
```

### **Code Quality Highlights**
- **Separation of Concerns** - Clear data/domain/presentation layers
- **Reactive Programming** - Flow for state management and data streaming
- **Modern Android Practices** - Compose, Coroutines, Room
- **Maintainable Architecture** - Repository pattern, dependency injection

---

## 7. Roadmap & Future Enhancements

### **Planned Features**
- [ ] **Enhanced Personalization** - AI-driven content recommendations
- [ ] **Social Features** - User comments and sharing capabilities
- [ ] **Advanced Analytics** - User behavior insights and engagement tracking
- [ ] **Monetization** - Ad integration and premium content options

### **Technical Improvements**
- [ ] **Caching Strategy** - Advanced content caching mechanisms
- [ ] **Dynamic Features** - On-demand feature delivery
- [ ] **Performance Monitoring** - Advanced analytics and performance tracking
- [ ] **Cross-Platform** - Potential KMM implementation for iOS

---

## 8. License

This project is proprietary software. All rights reserved.

## 9. Contact

**Developer**: Mohamed Elnagar  
**Email**: mohamed.3lnagar@gmail.com 


---

## 10. Technical Achievement Highlights

* **Engineered a multi-language Android platform** using Kotlin, Jetpack Compose, and Clean Architecture
* **Architected scalable data persistence** with Room Database, implementing reactive Flow streams and efficient CRUD operations
* **Developed enterprise notification system** integrating Firebase Cloud Messaging with priority-based delivery and deep linking
* **Implemented modern declarative UI** with Jetpack Compose, improving app performance and maintainability
* **Designed bilingual content management** with dynamic language switching for regional market expansion
* **Built robust dependency injection** system using Koin, enabling modular development across feature teams
* **Created comprehensive build system** with multiple flavors supporting streamlined development workflows
* **Developed type-safe data handling** with Room's compile-time verification, ensuring data integrity
* **Pioneered reactive architecture patterns** combining MVVM with Repository pattern for seamless data synchronization
* **Integrated multiple content sources** including blog articles and YouTube shorts in a unified interface

---

*This project demonstrates production-ready Android development with emphasis on modern architecture, scalability, and user experience.*