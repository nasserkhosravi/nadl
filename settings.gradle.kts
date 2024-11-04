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
        mavenCentral()
    }
}

rootProject.name = "NadlProj"
include(":base-app-main")
include(":base-app-expose")
include(":myLibraryImpl")
include(":dynamic-lib-apk-exporter")
include(":myLibraryApi")
