import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
  id("uk.gov.justice.hmpps.gradle-spring-boot") version "10.0.0"
  kotlin("plugin.spring") version "2.3.0"
  kotlin("plugin.jpa") version "2.3.0"
  id("org.jetbrains.kotlin.kapt") version "2.3.0"
  id("org.jetbrains.kotlinx.kover") version "0.9.4"
}

configurations {
  testImplementation { exclude(group = "org.junit.vintage") }
}

dependencies {
  implementation("uk.gov.justice.service.hmpps:hmpps-kotlin-spring-boot-starter:2.0.0")
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("tools.jackson.module:jackson-module-kotlin:3.0.3")
  implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.1")
  runtimeOnly("io.netty:netty-codec-classes-quic")

  // Database dependencies
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-flyway")
  implementation("org.postgresql:postgresql:42.7.8")
  runtimeOnly("org.flywaydb:flyway-database-postgresql")
  kapt("org.hibernate.orm:hibernate-jpamodelgen:7.1.0.Final")

  // Test dependencies
  testImplementation("uk.gov.justice.service.hmpps:hmpps-kotlin-spring-boot-starter-test:2.0.0")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:2.3.0")
  testImplementation("com.ninja-squad:springmockk:5.0.1")
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

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.compilerOptions {
  freeCompilerArgs.set(listOf("-Xannotation-default-target=param-property"))
}
