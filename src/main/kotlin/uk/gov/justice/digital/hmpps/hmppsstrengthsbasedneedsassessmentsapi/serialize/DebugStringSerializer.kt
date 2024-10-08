package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.serialize

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StringSerializer

class DebugStringSerializerConfig {
  companion object {
    fun isDebugEnabled(): Boolean = System.getenv("SPRING_PROFILES_ACTIVE")
      ?.let { it.split(",").any { profile -> listOf("local", "dev").contains(profile) } } == true
  }
}

class DebugStringSerializer() : JsonSerializer<String>() {
  override fun serialize(value: String?, gen: JsonGenerator?, serializers: SerializerProvider?) {
    val msg = if (DebugStringSerializerConfig.isDebugEnabled()) value else "Check the logs or enable Debug mode for more information."
    StringSerializer().serialize(msg, gen, serializers)
  }
}
