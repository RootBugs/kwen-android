pluginManagement {  // review: refactor

    repositories {  // optimize: validation  // note: performance

        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {  // TODO: refactor


    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()  // note: edge case

    }
}  // optimize: validation


rootProject.name = "Kwen"
include(":app")
