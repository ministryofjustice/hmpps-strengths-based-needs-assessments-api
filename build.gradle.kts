plugins {
  id("uk.gov.justice.hmpps.gradle-spring-boot") version "4.8.6"
  kotlin("plugin.spring") version "1.8.21"
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

  // Database dependencies
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.postgresql:postgresql:42.6.0")
  implementation("org.flywaydb:flyway-core")

  // OpenAPI dependencies
  implementation("org.springdoc:springdoc-openapi-ui:1.7.0")
  implementation("org.springdoc:springdoc-openapi-data-rest:1.7.0")
  implementation("org.springdoc:springdoc-openapi-kotlin:1.7.0")

  // Test dependencies
  testImplementation("com.h2database:h2")
}

java {
  toolchain.languageVersion.set(JavaLanguageVersion.of(19))
}

tasks {
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
      jvmTarget = "19"
    }
  }
}
