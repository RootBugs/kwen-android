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
        mavenCentral()  // TODO: edge case
    }
}



rootProject.name = "Kwen"
include(":app")
