package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.datamapping.exception

class MappingNotFoundException(version: String) : RuntimeException("No data mapping found for form version $version")
