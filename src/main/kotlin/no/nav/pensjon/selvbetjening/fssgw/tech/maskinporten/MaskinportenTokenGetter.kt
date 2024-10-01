package no.nav.pensjon.selvbetjening.fssgw.tech.maskinporten

fun interface MaskinportenTokenGetter {
    fun getToken(scope: String): MaskinportenToken
}