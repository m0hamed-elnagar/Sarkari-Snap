# Keep all classes that are referenced from the manifest
#-keep class com.rawderm.taaza.today.** { *; }

# Room Database
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keepclassmembers class * {
    @androidx.room.* *;
}


# Kotlin Coroutines
-keepclassmembers class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepclassmembers class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.internal.DispatchedContinuation {
*;}

# Serialization
-keepclassmembers class * {
    @kotlinx.serialization.* *;
}
-keep class com.facebook.infer.annotation.** { *; }

# Koin DI
-keep class org.koin.** { *; }
-keepclassmembers class * {
    @org.koin.core.annotation.* *;
}

# Jetpack Compose
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.ui.** { *; }
-keep class androidx.compose.foundation.** { *; }
-keep class androidx.compose.material3.** { *; }

# ViewModel
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# Firebase
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# Ktor
-keep class io.ktor.** { *; }
-keepclasseswithmembers class * {
    @io.ktor.util.** *;
}

# OkHttp
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**


# GSON/Serialization (if used)
-keepattributes Signature
-keepattributes *Annotation*

# Keep relevant resources
-keepclassmembers class **.R$* {
    public static <fields>;
}

# Parcelable
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}
-keep class com.google.ads.mediation.admob.AdMobAdapter {
    *;
}

-keep class com.google.ads.mediation.* {
    *;
}
-keep class com.rawderm.taaza.today.*
-dontwarn com.inmobi.sdk.**
-keep class com.google.android.gms.ads.mediation.customevent.CustomEventAdapter { *; }
# Keep View constructors and setters for XML layouts (if any)
-keepclassmembers class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(***);
}

# For custom views with custom attributes
-keepclassmembers class **.CustomView {
    public <init>(android.content.Context, android.util.AttributeSet);
}

# Keep application class
-keep class com.rawderm.taaza.today.app.* { *; }

# Keep data classes used in Room/Serialization
-keep class com.rawderm.taaza.today.bloger.data.database.** { *; }
-keep class com.rawderm.taaza.today.bloger.data.** { *; }

# Keep FCM related classes
-keep class com.rawderm.taaza.today.bloger.data.* { *; }

# Keep DAO interfaces
-keep class * implements androidx.room.Dao
-keepclassmembers class * implements androidx.room.Dao {
    <methods>;
}

# Keep entities
-keep class com.rawderm.taaza.today.bloger.data.database.PostEntity { *; }
-keep class com.rawderm.taaza.today.bloger.data.database.ShortEntity { *; }

# StringListConverter for Room
-keep class com.rawderm.taaza.today.bloger.data.database.StringListConverter { *; }

# LanguageManager and important business logic
-keep class com.rawderm.taaza.today.bloger.data.LanguageManager { *; }

## Notification related classes
#-keep class com.rawderm.taaza.today.core.notifications.NotificationInputDialog { *; }
#-keep class com.rawderm.taaza.today.bloger.domain.notifications { *; }

# Remove logging in production
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# General optimization
-optimizations !code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification
-dontpreverify