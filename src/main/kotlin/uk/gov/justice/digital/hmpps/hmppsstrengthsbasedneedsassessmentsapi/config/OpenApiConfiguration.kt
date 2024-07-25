package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springdoc.core.utils.SpringDocUtils
import org.springframework.boot.info.BuildProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Configuration
class OpenApiConfiguration(buildProperties: BuildProperties) {
  private val version: String = buildProperties.version

  init {
    val schema = Schema<LocalDateTime>()
    schema.example(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))
    SpringDocUtils.getConfig().replaceWithSchema(LocalDateTime::class.java, schema)
  }

  @Bean
  fun customOpenAPI(buildProperties: BuildProperties): OpenAPI? = OpenAPI()
    .servers(
      listOf(
        Server().url("https://api.strengths-based-needs-dev.hmpps.service.justice.gov.uk").description("Development"),
        Server().url("https://api.strengths-based-needs-test.hmpps.service.justice.gov.uk").description("Test"),
        Server().url("http://localhost:8080").description("Local"),
      ),
    )
    .info(
      Info()
        .title("HMPPS Strength Based Needs Assessments API")
        .version(version)
        .description("API for strength based Needs assessments")
        .contact(
          Contact()
            .name("HMPPS Digital Studio")
            .email("feedback@digital.justice.gov.uk")
            .url("https://github.com/ministryofjustice/hmpps-strengths-based-needs-assessments-ui"),
        ),
    )
    .components(
      Components().addSecuritySchemes(
        "bearer-jwt",
        SecurityScheme()
          .type(SecurityScheme.Type.HTTP)
          .scheme("bearer")
          .bearerFormat("JWT")
          .`in`(SecurityScheme.In.HEADER)
          .name("Authorization"),
      ),
    )
    .addSecurityItem(SecurityRequirement().addList("bearer-jwt"))
}
