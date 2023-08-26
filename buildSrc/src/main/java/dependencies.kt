object Configs {

    const val corenamespace     = "org.albaspazio.core"

    const val compileSdkVersion = 31
    const val minSdkVersion     = 24
    const val targetSdkVersion  = 26
}

object Plugins {

    const val androidLibrary        = "com.android.library"
    const val kotlinAndroid         = "org.jetbrains.kotlin.android"
    const val kotlinParcelize       = "org.jetbrains.kotlin.plugin.parcelize"
}

object Versions {

    const val kotlin = "1.6.10"
    const val ktxCore = "1.8.0"
    const val appCompat = "1.5.1"
    const val androidLibrary = "7.2.0"
    const val kparcelablePlugin = "1.7.0"
    const val constraintLayout = "2.1.4"
    const val material = "1.6.1"
    const val lifecycle = "2.5.1"
    const val localbroadcastmanager = "1.1.0"
    const val navFragment = "2.3.5"
    const val moshi = "1.12.0"

    const val junit = "4.13.2"
    const val testRunner = "1.5.2"
    const val testEspressoCore = "3.5.1"

}

object Dependencies {

    object AndroidX {
        const val navFragment       = "androidx.navigation:navigation-fragment-ktx:${Versions.navFragment}"
        const val navUi             = "androidx.navigation:navigation-ui-ktx:${Versions.navFragment}"
        const val ktxCore           = "androidx.core:core-ktx:${Versions.ktxCore}"
        const val appCompat         = "androidx.appcompat:appcompat:${Versions.appCompat}"
        const val constraintLayout  = "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
        const val material          = "com.google.android.material:material:${Versions.material}"

        const val livecycledataKtx  = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifecycle}"
        const val livecyclecommon   = "androidx.lifecycle:lifecycle-common-java8:${Versions.lifecycle}"
        const val localbroadcastmanager   = "androidx.localbroadcastmanager:localbroadcastmanager:${Versions.localbroadcastmanager}"

        const val testRunner        = "androidx.test:runner:${Versions.testRunner}"
        const val testEspressoCore  = "androidx.test.espresso:espresso-core:${Versions.testEspressoCore}"
    }

    object Kotlin {
        const val stdLib    = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"
        const val reflect   = "org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}"
    }

    object Moshi {
        const val moshi = "com.squareup.moshi:moshi:${Versions.moshi}"
        const val moshiKt = "com.squareup.moshi:moshi-kotlin:${Versions.moshi}"
    }

    const val junit             = "junit:junit:${Versions.junit}"
}

object ProGuards {
    val androidDefault = "proguard-rules.pro"
    val proguardTxt = "proguard-android.txt"
}