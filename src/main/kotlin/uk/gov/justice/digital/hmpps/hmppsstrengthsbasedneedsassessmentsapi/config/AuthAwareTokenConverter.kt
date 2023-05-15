package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.config

import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter

class AuthAwareTokenConverter : Converter<Jwt, AbstractAuthenticationToken> {
  private val jwtGrantedAuthoritiesConverter: Converter<Jwt, Collection<GrantedAuthority>> = JwtGrantedAuthoritiesConverter()

  override fun convert(source: Jwt): AbstractAuthenticationToken {
    val claims = source.claims
    val principal = findPrincipal(claims)
    val authorities = extractAuthorities(source)
    return AuthAwareAuthenticationToken(source, principal, authorities)
  }

  private fun isUserGrantType(claims: Map<String, Any?>) = claims.containsKey("grant_type") && claims["grant_type"] == "authorization_code"

  private fun findPrincipal(claims: Map<String, Any?>): String {
    return if (isUserGrantType(claims)) {
      claims["user_name"] as String
    } else {
      claims["client_id"] as String
    }
  }

  private fun extractAuthorities(jwt: Jwt): Collection<GrantedAuthority> {
    val authorities = mutableListOf<GrantedAuthority>().apply { addAll(jwtGrantedAuthoritiesConverter.convert(jwt)!!) }
    if (jwt.claims.containsKey("authorities")) {
      @Suppress("UNCHECKED_CAST")
      val claimAuthorities = (jwt.claims["authorities"] as Collection<String>).toList()
      authorities.addAll(claimAuthorities.map(::SimpleGrantedAuthority))
    }
    return authorities.toSet()
  }
}

class AuthAwareAuthenticationToken(
  jwt: Jwt,
  private val principal: String,
  authorities: Collection<GrantedAuthority>,
) : JwtAuthenticationToken(jwt, authorities) {
  override fun getPrincipal(): Any {
    return principal
  }
}
