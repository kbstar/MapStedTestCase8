pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        mavenCentral()
        maven(url = "https://jitpack.io")
        maven(url = "https://oss.jfrog.org/libs-snapshot")
        maven(url = "https://repo1.maven.org/maven2")
    }
}

rootProject.name = "MapSted TC8"
include(":app")
