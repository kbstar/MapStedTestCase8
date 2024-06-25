plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.daggerHiltAndroid)
    alias(libs.plugins.navigationSafeArgs)

    id("kotlin-kapt")
    id("kotlin-parcelize")
}
android {
    namespace = "com.mapsted.mapstedtceight"

    compileSdk = 34
    defaultConfig {
        minSdk = 29
        targetSdk = 34

        applicationId = "com.mapsted.mapstedtceight"
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        debug {
            isMinifyEnabled = false
            isDebuggable = true
            isJniDebuggable = true

            buildConfigField("String", "APP_SERVER_URL", "\"https://debug.google.com\"")
        }
        release {
            //proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "\"proguard-rules.pro\"")
            isMinifyEnabled = false

            buildConfigField("String", "APP_SERVER_URL", "\"https://release.google.com\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.aar"))))

    implementation(libs.coreKtxAndroidX)
    implementation(libs.lifecycleRuntime)
    implementation(libs.lifecycleViewModel)
    implementation(libs.activityAndroidX)
    implementation(libs.fragmentAndroidX)

    implementation(libs.appcompatAndroidX)
    implementation(libs.material)
    implementation(libs.constraintlayoutAndroidX)
    implementation(libs.recyclerview)

    implementation(libs.navigationRuntimeKTX)
    implementation(libs.navigationDragmentKTX)
    implementation(libs.navigationUiKTX)
    implementation(libs.hiltNavigationFragment)

    implementation(libs.gson)
    implementation(libs.retrofit)
    implementation(libs.retrofitGsonConverter)
    implementation(libs.retrofitConverterScalars)
    implementation(libs.okhttpLoggingInterceptor)

    implementation(libs.injectJavaX)
    implementation(libs.hiltAndroid)
    kapt(libs.hiltCompiler)

    implementation(libs.junit)
    implementation(libs.junitAndroidX)
    implementation(libs.espressoCoreAndroidX)
}