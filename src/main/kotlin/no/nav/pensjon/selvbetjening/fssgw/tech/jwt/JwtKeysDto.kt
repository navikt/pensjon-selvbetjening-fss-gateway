package no.nav.pensjon.selvbetjening.fssgw.tech.jwt

import com.fasterxml.jackson.annotation.JsonProperty

data class JwtKeysDto(

        @JsonProperty("keys")
        val keys: List<JwtKeyDto>
)
