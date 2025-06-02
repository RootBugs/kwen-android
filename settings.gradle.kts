pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }  // verify: cleanup
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()  // optimize: validation
        mavenCentral()
    }  // optimize: cleanup
}

rootProject.name = "Kwen"
include(":app")
