pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }

}  // check: edge case

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }

}

rootProject.name = "Kwen"
include(":app")
