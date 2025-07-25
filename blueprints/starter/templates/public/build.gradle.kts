@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.appPlatform)
  alias(libs.plugins.androidLibrary)
  alias(libs.plugins.kotlinMultiplatform)
}

appPlatform {
  enableModuleStructure(true)
  enableKotlinInject(true)
  enableMoleculePresenters(true)
}

kotlin {
  jvm("desktop") {
    compilerOptions {
      jvmTarget.set(JvmTarget.JVM_11)
    }
  }

  androidTarget {
    compilerOptions {
      jvmTarget.set(JvmTarget.JVM_11)
    }
  }

  iosX64()
  iosArm64()
  iosSimulatorArm64()

  wasmJs {
    browser {
      outputModuleName = project.name.replace("-", "")
    }
  }
}

android {
  namespace = "software.amazon.app.platform.template.templates"
  compileSdk = libs.versions.android.compileSdk.get().toInt()

  defaultConfig {
    minSdk = libs.versions.android.minSdk.get().toInt()
  }
}
