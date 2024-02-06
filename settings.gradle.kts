pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()

        // Maven Central Snapshots
        maven {
            url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "BackBlogApp"
include(":app")
