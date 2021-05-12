package no.nav.pensjon.selvbetjening.fssgw.tech.oauth2.dto

import com.fasterxml.jackson.annotation.*

data class Oauth2ConfigDto(
        @JsonProperty("issuer") val issuer: String,
        @JsonProperty("authorization_endpoint") val authorizationEndpoint: String,
        @JsonProperty("token_endpoint") val tokenEndpoint: String,
        @JsonProperty("jwks_uri") val jwksUri: String)
