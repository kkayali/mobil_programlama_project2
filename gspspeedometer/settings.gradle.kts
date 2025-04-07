pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://jitpack.io") // ✅ JitPack burada olmalı
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS) // ✅ Bu önemli!
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io") // ✅ JitPack burada da olmalı
    }
}

rootProject.name = "SPEedometer"
include(":app")
