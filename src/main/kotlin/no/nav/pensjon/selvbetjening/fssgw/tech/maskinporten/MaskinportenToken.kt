package no.nav.pensjon.selvbetjening.fssgw.tech.maskinporten

data class MaskinportenToken(
    val access_token: String,
    val token_type: String,
    val expires_in: Int,
    val scope: String,
)
