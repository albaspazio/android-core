// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id(Plugins.androidLibrary) version Versions.androidLibrary apply(false)
    id(Plugins.kotlinAndroid) version Versions.kotlin apply(false)
    id(Plugins.kotlinParcelize) version Versions.kparcelablePlugin apply(false)
}

tasks.register("clean", Delete::class){
    delete(rootProject.buildDir)
}