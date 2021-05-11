package no.nav.pensjon.selvbetjening.fssgw.tech.oauth2

interface Oauth2ConfigGetter {

    fun getIssuer(): String

    fun getAuthorizationEndpoint(): String

    fun getTokenEndpoint(): String

    fun getJsonWebKeySetUri(): String

    fun refresh()
}
