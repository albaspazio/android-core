// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id(libs.plugins.android.library.get().pluginId) version libs.versions.androidGradlePlugin.get() apply(false)
    id(libs.plugins.kotlin.android.get().pluginId) version libs.versions.kotlinPlugin.get() apply(false)
    id(libs.plugins.kotlin.parcelize.get().pluginId) version libs.versions.kotlinPlugin.get() apply(false)
}

tasks.register("clean", Delete::class){
    delete(rootProject.buildDir)
}