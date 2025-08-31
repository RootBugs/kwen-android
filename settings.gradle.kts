pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()


    }
}  // note: validation

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        google()
        mavenCentral()
    }
}



rootProject.name = "Kwen"
include(":app")
