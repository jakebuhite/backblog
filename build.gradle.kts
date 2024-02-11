buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.0")
        classpath("org.jacoco:org.jacoco.core:0.8.8")
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.4.0-alpha09" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
    id("nl.neotech.plugin.rootcoverage") version "1.8.0-SNAPSHOT"
    kotlin("plugin.serialization") version "1.8.10" apply false
}