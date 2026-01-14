package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication()
@ConfigurationPropertiesScan
class HmppsStrengthsBasedNeedsAssessmentsApi

fun main(args: Array<String>) {
  runApplication<HmppsStrengthsBasedNeedsAssessmentsApi>(*args)
}
