plugins {
    id(Plugins.androidLibrary)
    id(Plugins.kotlinAndroid)
    id("kotlin-parcelize")
}

android {

//    sourceSets["main"].java.srcDirs("libs")

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

//    packagingOptions {
//        resources {
//            pickFirsts.add("META-INF/NOTICE.md")
//            pickFirsts.add("META-INF/LICENSE.md")
//        }
//    }
//    packagingOptions {
//        exclude("META-INF/NOTICE*")
//        exclude("META-INF/LICENSE*")
//    }
}

dependencies {

    implementation(Dependencies.AndroidX.livecycleviewmodel) {
        exclude(group = "androidx.lifecycle", module ="lifecycle-viewmodel")
    }

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

    implementation("com.squareup.okhttp3:okhttp:4.12.0")


    api(Dependencies.rx.rxandroid)
    api(Dependencies.rx.rxrelay)
    api(Dependencies.rx.rxkotlin)

//    api(Dependencies.sunmail.mail)
//    api(Dependencies.sunmail.activation)

    api(files("libs/activation.jar"))
    api(files("libs/additionnal.jar"))
    api(files("libs/mail.jar"))


    testImplementation(Dependencies.junit)
    androidTestImplementation(Dependencies.AndroidX.testRunner)
    androidTestImplementation(Dependencies.AndroidX.testEspressoCore)
}