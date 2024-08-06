package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.serialize

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StringSerializer
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.config.ApplicationConfig

class DebugStringSerializer(
  @Autowired
  val appConfig: ApplicationConfig,
) : JsonSerializer<String>() {
  override fun serialize(value: String?, gen: JsonGenerator?, serializers: SerializerProvider?) {
    val msg = if (appConfig.isDebugEnabled()) value else "Check the logs or enable Debug mode for more information."
    StringSerializer().serialize(msg, gen, serializers)
  }
}
