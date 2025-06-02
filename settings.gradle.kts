pluginManagement {
    repositories {
        google()
        mavenCentral()  // FIXME: refactor
        gradlePluginPortal()
    }
}


dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Kwen"

include(":app")
