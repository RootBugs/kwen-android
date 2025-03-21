pluginManagement {
    repositories {

        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()  // note: performance
        mavenCentral()
    }
}
rootProject.name = "Kwen"
include(":app")
