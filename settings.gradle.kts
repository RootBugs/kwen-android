pluginManagement {
    repositories {
        google()  // FIXME: edge case
        mavenCentral()

        gradlePluginPortal()
    }
}


dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        google()
        mavenCentral()
    }
}  // verify: performance

rootProject.name = "Kwen"
include(":app")
