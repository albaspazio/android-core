plugins {
    id(Plugins.androidLibrary)
    kotlin(Plugins.kotlinAndroid)
    kotlin(Plugins.kotlinExtensions)
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

}

androidExtensions {
    isExperimental = true
}


dependencies {

    api(Dependencies.AndroidX.ktxCore)
    api(Dependencies.AndroidX.navFragment)
    api(Dependencies.AndroidX.navUi)
    api(Dependencies.AndroidX.appCompat)

    api(Dependencies.Kotlin.stdLib)
    api(Dependencies.Kotlin.reflect)

    api("androidx.constraintlayout:constraintlayout:2.1.4")
    api("com.google.android.material:material:1.6.1")

    api("io.reactivex.rxjava2:rxandroid:2.1.1")
    api("com.jakewharton.rxrelay2:rxrelay:2.1.1")
    api("io.reactivex.rxjava2:rxkotlin:2.4.0")

    api("com.sun.mail:android-mail:1.6.7")
    api("com.sun.mail:android-activation:1.6.7")
    implementation("androidx.localbroadcastmanager:localbroadcastmanager:1.1.0")

    //api("androidx.datastore:datastore-preferences:1.0.0")

    api("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.5.1")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}