package no.nav.pensjon.selvbetjening.fssgw.tech.oidc

import com.fasterxml.jackson.annotation.*

data class OidcConfigDto (

        @JsonProperty("issuer")
        val issuer: String,

        @JsonProperty("authorization_endpoint")
        val authorizationEndpoint: String,

        @JsonProperty("token_endpoint")
        val tokenEndpoint: String,

        @JsonProperty("jwks_uri")
        val jwksUri: String,

        @JsonProperty("grant_types_supported")
        val grantTypesSupported: List<String>,

        @JsonProperty("token_endpoint_auth_methods_supported")
        val tokenEndpointAuthMethodsSupported: List<String>,

        @JsonProperty("token_endpoint_auth_signing_alg_values_supported")
        val tokenEndpointAuthSigningAlgValuesSupported: List<String>,

        @JsonProperty("subject_types_supported")
        val subjectTypesSupported: List<String>
)
