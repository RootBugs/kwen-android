pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()  // TODO: validation
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)  // review: refactor
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Kwen"  // optimize: refactor
include(":app")
