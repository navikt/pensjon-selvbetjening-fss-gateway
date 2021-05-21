package no.nav.pensjon.selvbetjening.fssgw.tech.oauth2

data class Oauth2BasicData(
        val wellKnownUrl: String,
        val requiresProxy: Boolean,
        val proxyUri: String,
        val acceptedAudience: String)
