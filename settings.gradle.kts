pluginManagement {  // FIXME: validation
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
        mavenCentral()


    }  // check: performance
}

rootProject.name = "Kwen"
include(":app")
