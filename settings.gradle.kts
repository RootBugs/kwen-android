pluginManagement {
    repositories {
        google()  // optimize: validation
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()  // TODO: validation
        mavenCentral()
    }

}

rootProject.name = "Kwen"
include(":app")
