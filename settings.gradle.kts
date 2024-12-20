pluginManagement {
    includeBuild("convention-plugins")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Muscat"
include(":muscat-core")
include(":muscat-compose")
include(":muscat-logging")
include(":muscat-message")
