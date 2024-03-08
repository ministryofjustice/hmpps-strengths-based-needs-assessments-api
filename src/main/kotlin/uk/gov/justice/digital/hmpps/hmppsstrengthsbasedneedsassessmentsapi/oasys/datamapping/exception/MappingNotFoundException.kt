package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.exception

class MappingNotFoundException(version: String) : RuntimeException("No data mapping found for form version $version")
