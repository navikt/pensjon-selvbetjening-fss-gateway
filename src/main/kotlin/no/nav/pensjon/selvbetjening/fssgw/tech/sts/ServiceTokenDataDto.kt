package no.nav.pensjon.selvbetjening.fssgw.tech.sts

import com.fasterxml.jackson.annotation.JsonProperty

data class ServiceTokenDataDto(

        @JsonProperty("access_token")
        val accessToken: String,

        @JsonProperty("token_type")
        val tokenType: String,

        @JsonProperty("expires_in")
        val expiresIn: Long
)
