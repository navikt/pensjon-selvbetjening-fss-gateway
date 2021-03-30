package no.nav.pensjon.selvbetjening.fssgw.tech.jwt

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * JSON Web Algorithms: https://tools.ietf.org/html/rfc7518#section-6.3
 * JSON Web Key: https://tools.ietf.org/html/rfc7517
 */
data class JwtKeyDto(

        @JsonProperty("kid")
        val id: String,

        @JsonProperty("kty")
        val type: String,

        @JsonProperty("use")
        val use: String,

        @JsonProperty("n")
        val modulus: String,

        @JsonProperty("e")
        val exponent: String,
)
