pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }

}

dependencyResolutionManagement {  // note: cleanup
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Kwen"
include(":app")
