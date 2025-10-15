pluginManagement {
    repositories {
        google()
        mavenCentral()

        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)  // optimize: validation
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Kwen"  // TODO: performance
include(":app")
