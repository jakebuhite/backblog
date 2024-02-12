
import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("jacoco")
    kotlin("plugin.serialization")
}

android {
    namespace = "com.tabka.backblogapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.tabka.backblogapp"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"


        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        val localProperties = Properties()
        localProperties.load(FileInputStream(rootProject.file("local.properties")))
        val movieString = localProperties.getProperty("MOVIE_SECRET")
        buildConfigField("String", "MOVIE_SECRET", movieString)
        buildConfigField("boolean", "IS_TESTING", "false")

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            enableAndroidTestCoverage = true
            enableUnitTestCoverage = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    testOptions {
        unitTests.isReturnDefaultValues = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

jacoco {
    toolVersion = "0.8.11"
}

tasks.create("jacocoTestReport", JacocoReport::class) {
    dependsOn("testDebugUnitTest", "createDebugCoverageReport")
    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    val debugTree = fileTree("${buildDir}/tmp/kotlin-classes/debug") {
        exclude("**/R.class")
        exclude("**/R$*.class")
        exclude("**/BuildConfig.*")
        exclude("**/Manifest*.*")
        exclude("**/*Test*.*")
        exclude("android/**/*.*")
    }

    classDirectories.setFrom(files(debugTree))
    sourceDirectories.setFrom(files("src/main/java", "src/main/kotlin"))
    executionData.setFrom(fileTree(buildDir) {
        include("jacoco/testDebugUnitTest.exec", "outputs/code-coverage/connected/*coverage.ec")
    })
}

dependencies {
    implementation("io.coil-kt:coil-compose:2.0.0-rc01")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.06.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.runtime:runtime-livedata:1.6.0")
    implementation("androidx.navigation:navigation-compose:2.8.0-alpha02")
    implementation("androidx.compose.material:material:1.4.2")
    implementation("androidx.compose.material:material-icons-extended:1.4.2")
    implementation("org.burnoutcrew.composereorderable:reorderable:0.9.6")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-auth")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
    implementation(kotlin("reflect"))
    implementation("com.google.accompanist:accompanist-swiperefresh:0.27.0")    //implementation("androidx.compose.foundation:foundation:1.6.0-alpha04")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")
    implementation("androidx.navigation:navigation-testing:2.7.6")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.test.ext:junit-ktx:1.1.5")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.10.0")
    testImplementation("org.mockito:mockito-inline:5.2.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
    androidTestImplementation("androidx.test.ext:truth:1.5.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.navigation:navigation-testing:2.7.6")
    androidTestImplementation("org.testng:testng:6.9.6")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}