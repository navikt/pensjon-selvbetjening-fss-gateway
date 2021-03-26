package no.nav.pensjon.selvbetjening.fssgw.tech.jwt

interface CertificateGetter {

    fun getCertificate(id: String): String

    fun refresh()
}
