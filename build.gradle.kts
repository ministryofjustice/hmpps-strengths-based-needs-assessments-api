import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
  id("uk.gov.justice.hmpps.gradle-spring-boot") version "5.15.1"
  kotlin("jvm") version "1.9.22"
  kotlin("plugin.serialization") version "1.9.22"
  kotlin("plugin.spring") version "1.9.22"
  id("org.jetbrains.kotlin.kapt") version "1.9.22"
}

configurations {
  testImplementation { exclude(group = "org.junit.vintage") }
}

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-webflux")

  // OAuth dependencies
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.security:spring-security-oauth2-client")
  implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
  constraints {
    implementation("com.nimbusds:nimbus-jose-jwt:9.37.2") {
      because("previous versions have a high vulnerability CVE-2023-52428")
    }
  }

  // Database dependencies
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.postgresql:postgresql:42.7.1")
  implementation("org.flywaydb:flyway-core")
  implementation("com.vladmihalcea:hibernate-types-60:2.21.1")
  kapt("org.hibernate:hibernate-jpamodelgen-jakarta:5.6.15.Final")

  // OpenAPI dependencies
  implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")

  // JSON serialisation
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")

  // Test dependencies
  testImplementation("com.h2database:h2")
  testImplementation(kotlin("test"))
  testImplementation("com.ninja-squad:springmockk:4.0.2")

  // Dev dependencies
  developmentOnly("org.springframework.boot:spring-boot-devtools")
}

java {
  toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
  withType<KotlinCompile> {
    kotlinOptions {
      jvmTarget = "21"
    }
  }
  withType<BootRun> {
    jvmArgs = listOf(
      "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005",
    )
  }
}
