package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.runner

import org.springframework.beans.factory.getBean
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.ConfigurableApplicationContext
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.HmppsStrengthsBasedNeedsAssessmentsApi

object TaskRunner {

  @JvmStatic
  fun main(args: Array<String>) {
    val context: ConfigurableApplicationContext =
      SpringApplicationBuilder(HmppsStrengthsBasedNeedsAssessmentsApi::class.java)
        .run(*args)

    val migrator = context.getBean<MigrationRunner>()

    migrator.run(args.filterNot { it.startsWith("--") }.map { it.toLong() }.ifEmpty { null })

    context.close()
  }
}
