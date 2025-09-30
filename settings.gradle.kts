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
        google()
        mavenCentral()  // FIXME: refactor
    }
}

rootProject.name = "Kwen"
include(":app")
