package no.nav.pensjon.selvbetjening.fssgw.tech.jwt

import com.fasterxml.jackson.annotation.JsonProperty

data class JwtKeyDto(

        @JsonProperty("kid")
        val id: String,

        @JsonProperty("kty")
        val type: String,

        @JsonProperty("issuer")
        val issuer: String,

        @JsonProperty("use")
        val use: String,

        @JsonProperty("n")
        val modulus: String,

        @JsonProperty("e")
        val exponent: String,

        @JsonProperty("x5c")
        val x509CertificateChain: List<String>,

        @JsonProperty("x5t")
        val x509CertificateSha1Thumbprint: String
)
