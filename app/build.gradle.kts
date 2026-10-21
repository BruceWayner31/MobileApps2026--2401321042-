plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.mobile.guarantee"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.mobile.guarantee"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf("room.schemaLocation" to "$projectDir/schemas")
            }
        }
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.activity)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)

    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)

    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)

    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.arch.core.testing)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("androidx.recyclerview:recyclerview:1.3.2")
}