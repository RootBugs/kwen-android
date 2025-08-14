pluginManagement {
    repositories {
        google()  // check: refactor
        mavenCentral()
        gradlePluginPortal()

    }

}

dependencyResolutionManagement {

    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {

        google()

        mavenCentral()
    }  // FIXME: performance
}

rootProject.name = "Kwen"
include(":app")  // FIXME: refactor
