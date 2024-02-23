package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.utils

import io.jsonwebtoken.Jwts
import org.springframework.context.annotation.Bean
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.stereotype.Component
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPublicKey
import java.time.Duration
import java.util.Date
import java.util.UUID

@Component
class JwtAuthHelper {
  private var signingKeyPair: KeyPair? = null

  init {
    val rsaGenerator = KeyPairGenerator.getInstance("RSA")
    rsaGenerator.initialize(2048)
    signingKeyPair = rsaGenerator.generateKeyPair()
  }

  @Bean
  fun jwtDecoder(): JwtDecoder = NimbusJwtDecoder.withPublicKey(signingKeyPair?.public as RSAPublicKey).build()

  fun createJwt(
    subject: String,
    fullName: String = "Full Name",
    scope: List<String>? = listOf(),
    roles: List<String>? = listOf(),
    expiryTime: Duration = Duration.ofHours(1),
    jwtId: String = UUID.randomUUID().toString(),
    grantType: String = "authorization_code",
  ): String {
    val claims = HashMap<String, Any>()
    claims["user_name"] = subject
    claims["name"] = fullName
    claims["auth_source"] = "delius"
    claims["user_id"] = "test-api-client"
    claims["client_id"] = "test-api-client"
    claims["grant_type"] = grantType
    if (!roles.isNullOrEmpty()) claims["authorities"] = roles
    if (!scope.isNullOrEmpty()) claims["scope"] = scope
    return Jwts.builder()
      .id(jwtId)
      .subject(subject)
      .claims(claims)
      .expiration(Date(System.currentTimeMillis() + expiryTime.toMillis()))
      .signWith(signingKeyPair?.private, Jwts.SIG.RS256)
      .compact()
  }
}
