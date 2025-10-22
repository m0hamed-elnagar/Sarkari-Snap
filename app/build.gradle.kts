import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.gms.google.services)
    id("com.google.firebase.crashlytics")

}

android {
    namespace = "com.rawderm.taaza.today"
    compileSdk = 36

    bundle {
        language {
            enableSplit = false
        }
    }
    signingConfigs {
        create("release") {
            storeFile = rootProject.file("key/blogger-release-key.jks")
            storePassword = project.findProperty("KEYSTORE_PASSWORD") as String? ?: ""
            keyAlias = project.findProperty("KEY_ALIAS") as String? ?: ""
            keyPassword = project.findProperty("KEY_PASSWORD") as String? ?: ""
        }
    }

    defaultConfig {
        applicationId = "com.rawderm.taaza.today"
        minSdk = 26
        targetSdk = 36
        versionCode = 7
        versionName = "1.3"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        val localProps = Properties().apply {
            load(FileInputStream(rootProject.file("local.properties")))
        }
        val apiKey = localProps.getProperty("BLOGGER_API_KEY") ?: ""
        buildConfigField("String", "BLOGGER_API_KEY", "\"$apiKey\"")
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }
    buildTypes {

        release {
            isCrunchPngs = false
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            isCrunchPngs = false
            // custom debug options if needed
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeBom.get()
    }
    kotlin {
        jvmToolchain(17)
    }

    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
}

dependencies {
    // Core
    implementation(libs.androidx.core.ktx)

    // Lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Activity
    implementation(libs.androidx.activity.compose)

    // Compose BOM + UI
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Navigation
    implementation(libs.navigation.compose)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // Koin
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    implementation(libs.androidx.ui.text)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui.text.google.fonts)
    testImplementation(libs.koin.test)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.config)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    // Ktor
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.auth)

    // Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.compose.core)
    implementation(libs.coil.network.ktor3)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Logging
    implementation(libs.timber)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit.ktx)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // Debugging tools
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.junit4)

    // for handles messy HTML
    implementation(libs.jsoup)
    implementation("com.github.ireward:compose-html:1.0.2")
    implementation("com.mohamedrejeb.richeditor:richeditor-compose:1.0.0-rc13")

//    webview
    implementation("com.google.accompanist:accompanist-webview:0.34.0")
    implementation("androidx.browser:browser:1.5.0")

//    swipe to refresh
    implementation("com.google.accompanist:accompanist-swiperefresh:0.34.0")
    implementation("androidx.compose.material:material:1.6.8")
    // build.gradle (:app)  or (:feature)
    // paging
    implementation("androidx.paging:paging-compose:3.3.6")
    implementation("androidx.paging:paging-runtime-ktx:3.3.6")
    implementation("androidx.paging:paging-common-ktx:3.3.6")

    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation(libs.material.icons.extended)
    implementation("com.izettle:html2bitmap:1.10")
    // ExoPlayer
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.ui)

    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:13.0.0")

    //datastore
    implementation(libs.androidx.datastore.preferences)
    //lang
    implementation(libs.lingver)
    // ads
    implementation("com.google.android.gms:play-services-ads:24.7.0")

    //google play update check
    implementation(libs.app.update.ktx)
    implementation("com.google.android.play:app-update:2.1.0")


}