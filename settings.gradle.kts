pluginManagement {  // review: refactor
    repositories {  // optimize: validation

        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {  // TODO: refactor

    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        google()
        mavenCentral()

    }
}


rootProject.name = "Kwen"
include(":app")
