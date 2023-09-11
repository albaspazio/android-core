plugins {
    id(Plugins.androidLibrary)
    id(Plugins.kotlinAndroid)
    id("kotlin-parcelize")
}

android {

    compileSdkVersion(Configs.compileSdkVersion)
    defaultConfig {

        minSdkVersion(Configs.minSdkVersion)
        targetSdkVersion(Configs.targetSdkVersion)
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile(ProGuards.proguardTxt), ProGuards.androidDefault)        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    api(Dependencies.AndroidX.ktxCore)
    api(Dependencies.AndroidX.navFragment)
    api(Dependencies.AndroidX.navUi)
    api(Dependencies.AndroidX.appCompat)

    api(Dependencies.Kotlin.stdLib)
    api(Dependencies.Kotlin.reflect)

    api(Dependencies.AndroidX.constraintLayout)
    api(Dependencies.AndroidX.material)

    api(Dependencies.AndroidX.livecycledataKtx)
    implementation(Dependencies.AndroidX.livecyclecommon)
    implementation(Dependencies.AndroidX.localbroadcastmanager)

    api(Dependencies.rx.rxandroid)
    api(Dependencies.rx.rxrelay)
    api(Dependencies.rx.rxkotlin)

    api(Dependencies.sunmail.mail)
    api(Dependencies.sunmail.activation)

    testImplementation(Dependencies.junit)
    androidTestImplementation(Dependencies.AndroidX.testRunner)
    androidTestImplementation(Dependencies.AndroidX.testEspressoCore)
}