package no.nav.pensjon.selvbetjening.fssgw.tech.sts

fun interface ServiceTokenGetter {

    fun getServiceUserToken(serviceUserId: Int): ServiceTokenData
}
