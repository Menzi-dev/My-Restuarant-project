plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")   // 👈 This connects Firebase
}

android {
    namespace = "com.example.urbanstylerestaurant"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.urbanstylerestaurant"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation ("com.google.android.material:material:1.11.0")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")
    implementation ("com.github.bumptech.glide:glide:4.16.0'")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")



    // ✅ ADD THESE 2 LINES for Firebase from your version catalog
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.gridlayout)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
