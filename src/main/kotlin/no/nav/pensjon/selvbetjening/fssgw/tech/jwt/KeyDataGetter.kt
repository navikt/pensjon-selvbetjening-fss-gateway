package no.nav.pensjon.selvbetjening.fssgw.tech.jwt

interface KeyDataGetter {

    fun getKeyData(id: String): JwtKeyDto

    fun refresh()
}
