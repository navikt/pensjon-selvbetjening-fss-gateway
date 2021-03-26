package no.nav.pensjon.selvbetjening.fssgw.tech.sts

interface ServiceTokenGetter {

    fun getServiceUserToken(): ServiceTokenData
}
