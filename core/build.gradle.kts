plugins {
    id(Plugins.androidLibrary)
    id(Plugins.kotlinAndroid)
    id("kotlin-parcelize")
}

android {

    compileSdkVersion(Configs.compileSdkVersion)
    defaultConfig {

//        namespace = Configs.corenamespace
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


    api("io.reactivex.rxjava2:rxandroid:2.1.1")
    api("com.jakewharton.rxrelay2:rxrelay:2.1.1")
    api("io.reactivex.rxjava2:rxkotlin:2.4.0")

    api("com.sun.mail:android-mail:1.6.7")
    api("com.sun.mail:android-activation:1.6.7")

    //api("androidx.datastore:datastore-preferences:1.0.0")

    testImplementation(Dependencies.junit)
    androidTestImplementation(Dependencies.AndroidX.testRunner)
    androidTestImplementation(Dependencies.AndroidX.testEspressoCore)
}