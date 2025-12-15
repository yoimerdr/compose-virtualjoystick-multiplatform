import com.android.build.api.dsl.androidLibrary
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.vanniktech.mavenPublish)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

group = "io.github.yoimerdr.compose"
version = "1.0.0"

kotlin {
    jvm()
    androidLibrary {
        namespace = "${group}.virtualjoystick"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        withJava() // enable java compilation support
        withHostTestBuilder {}.configure {}
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }

        compilations.configureEach {
            compilerOptions.configure {
                jvmTarget.set(
                    JvmTarget.JVM_11
                )
            }
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    js(IR) {
        browser()
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    sourceSets {

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

mavenPublishing {
    publishToMavenCentral()

    signAllPublications()

    coordinates(group.toString(), "virtualjoystick", version.toString())

    pom {
        name = "Compose Virtual Joystick Multiplatform"
        description = "A customizable virtual joystick library for Compose Multiplatform supporting Android, iOS, JVM, JS, and WASM platforms. Provides touch-based directional controls with configurable dead zones, multiple direction types, and customizable visual styles."
        inceptionYear = "2025"
        url = "https://github.com/yoimerdr/compose-virtualjoystick-multiplatform/"
        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }
        developers {
            developer {
                id = "yoimerdr"
                name = "Yoimer Davila"
                email = "yoraldarez020@gmail.com"
                organization = ""
                organizationUrl = ""
                url = "https://github.com/yoimerdr"
            }
        }
        scm {
            url = "https://github.com/yoimerdr/compose-virtualjoystick-multiplatform/"
            connection = "scm:git:git://github.com/yoimerdr/compose-virtualjoystick-multiplatform.git"
            developerConnection = "scm:git:ssh://git@github.com/yoimerdr/compose-virtualjoystick-multiplatform.git"
        }
    }
}
