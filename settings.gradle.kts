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
include(":host-app-main")
include(":host-app-impl")
include(":pluing-basic-impl")
include(":plugin-apk-exporter")
include(":plugin-contract")
include(":plugin-compose-impl")
include(":host-app-sample")
