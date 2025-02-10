import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
  id("uk.gov.justice.hmpps.gradle-spring-boot") version "7.1.0"
  kotlin("plugin.spring") version "2.1.10"
  id("org.jetbrains.kotlin.kapt") version "2.1.10"
  id("org.jetbrains.kotlinx.kover") version "0.9.1"
}

configurations {
  testImplementation { exclude(group = "org.junit.vintage") }
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-webflux")

  // OAuth dependencies
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.security:spring-security-oauth2-client")
  implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
  constraints {
    implementation("com.nimbusds:nimbus-jose-jwt:10.0.1") {
      because("previous versions have a high vulnerability CVE-2023-52428")
    }
  }

  // Database dependencies
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.postgresql:postgresql:42.7.5")
  implementation("org.flywaydb:flyway-core")
  runtimeOnly("org.flywaydb:flyway-database-postgresql")
  implementation("com.vladmihalcea:hibernate-types-60:2.21.1")
  kapt("org.hibernate:hibernate-jpamodelgen-jakarta:5.6.15.Final")

  // OpenAPI dependencies
  implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.4")

  // Test dependencies
  testImplementation("com.h2database:h2")
  testImplementation(kotlin("test"))
  testImplementation("com.ninja-squad:springmockk:4.0.2")
  testImplementation("io.jsonwebtoken:jjwt-impl:0.12.6")
  testImplementation("io.jsonwebtoken:jjwt-jackson:0.12.6")

  // Dev dependencies
  developmentOnly("org.springframework.boot:spring-boot-devtools")
}

kotlin {
  jvmToolchain(21)
}

tasks {
  withType<KotlinCompile> {
    compilerOptions.jvmTarget = JvmTarget.JVM_21
  }
  withType<BootRun> {
    jvmArgs = listOf(
      "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005",
    )
  }
}

tasks.register<Test>("unitTests") {
  useJUnitPlatform {
    excludeTags("integration")
  }
}

tasks.register<Test>("integrationTests") {
  useJUnitPlatform {
    includeTags("integration")
  }
}

// this is to address JLLeitschuh/ktlint-gradle#809
ktlint {
  version = "1.5.0"
}
