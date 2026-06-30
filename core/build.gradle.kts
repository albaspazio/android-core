plugins {
    id(libs.plugins.android.library.get().pluginId)
    id(libs.plugins.kotlin.android.get().pluginId)
    id(libs.plugins.kotlin.parcelize.get().pluginId)
}

android {

    namespace = Configs.corenamespace
    compileSdk = Configs.compileSdkVersion

    defaultConfig {
        minSdk = Configs.minSdkVersion
        targetSdk = Configs.targetSdkVersion
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile(ProGuards.proguardTxt), ProGuards.androidDefault)        }
    }

    compileOptions {
        val javaVer = JavaVersion.toVersion(rootProject.ext["javaVersion"] as String)
        sourceCompatibility = javaVer
        targetCompatibility = javaVer
    }
    kotlinOptions {
        jvmTarget = rootProject.ext["javaVersion"] as String
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.lifecycle.viewmodel) {
        exclude(group = "androidx.lifecycle", module ="lifecycle-viewmodel")
    }

    api(libs.androidx.core.ktx)
    api(libs.androidx.navigation.fragment)
    api(libs.androidx.navigation.ui)
    api(libs.androidx.appcompat)

    api(libs.kotlin.stdlib)
    api(libs.kotlin.reflect)

    api(libs.androidx.constraintlayout)
    api(libs.androidx.material)

    api(libs.androidx.lifecycle.livedata)
    implementation(libs.androidx.lifecycle.common)
    implementation(libs.androidx.localbroadcastmanager)

    implementation(libs.okhttp)

    api(libs.rxandroid)
    api(libs.rxrelay)
    api(libs.rxkotlin)

    api(files("libs/activation.jar"))
    api(files("libs/additionnal.jar"))
    api(files("libs/mail.jar"))

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.espresso.core)
}